"""Deterministic reporting for LAB_07 with LAB_06-compatible output."""

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
from .models import MembershipProtocol


def format_report(
    records: list[MembershipProtocol],
    reference_date: date = DEFAULT_REFERENCE_DATE,
) -> str:
    """Format exactly the same external report as LAB_06."""
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
        lines.append(f"Leader: {best.customer} ({best.visits} visits)")

    lines.append("Visits by kind:")
    if totals:
        for kind, visits in sorted(totals.items()):
            lines.append(f"{kind}: {visits}")
    else:
        lines.append("none")

    return "\n".join(lines) + "\n"


def write_report(path: Path, text: str) -> None:
    """Write the report with Path, with, UTF-8 and deterministic newlines."""
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="\n") as target:
        target.write(text)
