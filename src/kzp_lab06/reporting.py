"""Deterministic text reporting for LAB_06."""

from __future__ import annotations

from datetime import date
from pathlib import Path

from .analysis import (
    DEFAULT_REFERENCE_DATE,
    active_memberships,
    average_visits,
    leader,
    unique_customers,
    visits_by_kind,
)
from .loader import MembershipRecord


def format_report(
    records: list[MembershipRecord],
    reference_date: date = DEFAULT_REFERENCE_DATE,
) -> str:
    """Format the Gym variant report in a deterministic order."""
    active = active_memberships(records, reference_date)
    totals = visits_by_kind(active)
    customers = unique_customers(active)
    best = leader(active)

    lines = [
        f"Reference date: {reference_date.isoformat()}",
        f"Active memberships: {len(active)}",
        f"Unique customers: {len(customers)}",
        f"Average visits: {average_visits(active):.2f}",
    ]

    if best is None:
        lines.append("Leader: none")
    else:
        lines.append(
            f"Leader: {best['customer']} ({int(best['visits'])} visits)"
        )

    lines.append("Visits by kind:")
    if totals:
        for kind, visits in sorted(totals.items()):
            lines.append(f"{kind}: {visits}")
    else:
        lines.append("none")

    return "\n".join(lines) + "\n"


def write_report(path: Path, text: str) -> None:
    """Write a UTF-8 report using a context manager."""
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="\n") as target:
        target.write(text)
