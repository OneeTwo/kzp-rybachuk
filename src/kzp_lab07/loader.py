"""CSV loader that converts rows directly to membership objects."""

from __future__ import annotations

import csv
from datetime import date
from pathlib import Path

from .errors import DataError
from .models import AnnualMembership, Membership, MonthlyMembership

EXPECTED_FIELDS = ["customer", "kind", "start", "end", "visits"]


def _require_text(row: dict[str, str | None], field: str, row_number: int) -> str:
    """Return a normalized non-empty CSV field."""
    value = row.get(field)
    if value is None or not value.strip():
        raise DataError(f"Row {row_number}: field {field} is required")
    return value.strip()


def _parse_date(row: dict[str, str | None], field: str, row_number: int) -> date:
    """Parse an ISO date and preserve the parsing cause."""
    value = _require_text(row, field, row_number)
    try:
        return date.fromisoformat(value)
    except ValueError as error:
        raise DataError(f"Row {row_number}: field {field} must use ISO YYYY-MM-DD") from error


def _parse_visits(row: dict[str, str | None], row_number: int) -> int:
    """Parse visits and preserve the conversion cause."""
    value = _require_text(row, "visits", row_number)
    try:
        return int(value)
    except ValueError as error:
        raise DataError(f"Row {row_number}: field visits must be an integer") from error


def parse_row(row: dict[str, str | None], row_number: int) -> Membership:
    """Convert a DictReader row into the correct Membership subtype."""
    if None in row:
        raise DataError(f"Row {row_number}: too many CSV values")

    customer = _require_text(row, "customer", row_number)
    kind = _require_text(row, "kind", row_number)
    start = _parse_date(row, "start", row_number)
    end = _parse_date(row, "end", row_number)
    visits = _parse_visits(row, row_number)

    membership_type: type[Membership]
    if kind == MonthlyMembership.KIND:
        membership_type = MonthlyMembership
    elif kind == AnnualMembership.KIND:
        membership_type = AnnualMembership
    else:
        raise DataError(f"Row {row_number}: unsupported membership kind {kind!r}")

    try:
        return membership_type(customer, start, end, visits)
    except ValueError as error:
        raise DataError(f"Row {row_number}: invalid membership values") from error


def load_records(path: Path) -> list[Membership]:
    """Read a UTF-8 CSV file and return validated Membership objects."""
    with path.open("r", encoding="utf-8", newline="") as source:
        reader = csv.DictReader(source)

        if reader.fieldnames is None:
            raise DataError("CSV file has no header")

        if reader.fieldnames != EXPECTED_FIELDS:
            expected = ",".join(EXPECTED_FIELDS)
            actual = ",".join(reader.fieldnames)
            raise DataError(f"Invalid CSV header: expected {expected}; got {actual}")

        return [parse_row(row, row_number) for row_number, row in enumerate(reader, start=2)]
