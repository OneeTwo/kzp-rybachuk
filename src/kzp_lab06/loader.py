"""CSV loading and typed conversion for Gym memberships."""

from __future__ import annotations

import csv
from datetime import date
from pathlib import Path
from typing import TypeAlias

from .errors import DataError

MembershipRecord: TypeAlias = dict[str, object]

EXPECTED_FIELDS = ["customer", "kind", "start", "end", "visits"]


def _require_text(row: dict[str, str | None], field: str, row_number: int) -> str:
    """Return a normalized non-empty text field."""
    value = row.get(field)
    if value is None or not value.strip():
        raise DataError(f"Row {row_number}: field {field} is required")
    return value.strip()


def _parse_date(row: dict[str, str | None], field: str, row_number: int) -> date:
    """Parse an ISO date and preserve the original parsing error as the cause."""
    value = _require_text(row, field, row_number)
    try:
        return date.fromisoformat(value)
    except ValueError as error:
        raise DataError(
            f"Row {row_number}: field {field} must use ISO YYYY-MM-DD"
        ) from error


def _parse_visits(row: dict[str, str | None], row_number: int) -> int:
    """Parse the visits field and reject negative values."""
    value = _require_text(row, "visits", row_number)
    try:
        visits = int(value)
    except ValueError as error:
        raise DataError(f"Row {row_number}: field visits must be an integer") from error

    if visits < 0:
        raise DataError(f"Row {row_number}: field visits cannot be negative")
    return visits


def parse_row(row: dict[str, str | None], row_number: int) -> MembershipRecord:
    """Convert one DictReader row to a membership record with typed values."""
    if None in row:
        raise DataError(f"Row {row_number}: too many CSV values")

    customer = _require_text(row, "customer", row_number)
    kind = _require_text(row, "kind", row_number)
    start = _parse_date(row, "start", row_number)
    end = _parse_date(row, "end", row_number)
    visits = _parse_visits(row, row_number)

    if end < start:
        raise DataError(f"Row {row_number}: end cannot be before start")

    return {
        "customer": customer,
        "kind": kind,
        "start": start,
        "end": end,
        "visits": visits,
    }


def load_records(path: Path) -> list[MembershipRecord]:
    """Read a UTF-8 CSV file with DictReader and return typed membership records."""
    with path.open("r", encoding="utf-8", newline="") as source:
        reader = csv.DictReader(source)

        if reader.fieldnames is None:
            raise DataError("CSV file has no header")

        if reader.fieldnames != EXPECTED_FIELDS:
            expected = ",".join(EXPECTED_FIELDS)
            actual = ",".join(reader.fieldnames)
            raise DataError(f"Invalid CSV header: expected {expected}; got {actual}")

        records: list[MembershipRecord] = []
        for row_number, row in enumerate(reader, start=2):
            records.append(parse_row(row, row_number))

    return records
