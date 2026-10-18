"""Analysis functions for Gym membership records."""

from __future__ import annotations

from datetime import date

from .loader import MembershipRecord

DEFAULT_REFERENCE_DATE = date(2026, 10, 15)


def active_memberships(
    records: list[MembershipRecord],
    reference_date: date = DEFAULT_REFERENCE_DATE,
) -> list[MembershipRecord]:
    """Return memberships active on the reference date."""
    return [record for record in records if record["start"] <= reference_date <= record["end"]]


def unique_customers(records: list[MembershipRecord]) -> set[str]:
    """Return unique customer names."""
    return {str(record["customer"]) for record in records}


def visits_by_kind(records: list[MembershipRecord]) -> dict[str, int]:
    """Aggregate visits by membership kind in deterministic key order."""
    kinds = {str(record["kind"]) for record in records}
    return {
        kind: sum(int(record["visits"]) for record in records if record["kind"] == kind)
        for kind in sorted(kinds)
    }


def average_visits(records: list[MembershipRecord]) -> float:
    """Return average visits or zero for an empty collection."""
    if not records:
        return 0.0
    return sum(int(record["visits"]) for record in records) / len(records)


def leader(records: list[MembershipRecord]) -> MembershipRecord | None:
    """Return the visit leader using customer name as a deterministic tie-breaker."""
    if not records:
        return None
    return sorted(
        records,
        key=lambda record: (
            -int(record["visits"]),
            str(record["customer"]),
            str(record["kind"]),
            record["start"],
        ),
    )[0]


def summarize(
    records: list[MembershipRecord],
    *metrics: str,
    reference_date: date = DEFAULT_REFERENCE_DATE,
    **labels: str,
) -> dict[str, object]:
    """Summarize selected metrics for memberships active on a reference date."""
    selected = active_memberships(records, reference_date)
    result: dict[str, object] = {"count": len(selected)}

    for metric in metrics:
        if selected and any(metric not in record for record in selected):
            raise ValueError(f"Unknown metric: {metric}")
        if metric not in {"visits"}:
            raise ValueError(f"Unknown metric: {metric}")

        label = labels.get(metric, metric)
        result[label] = sum(float(record[metric]) for record in selected)

    return result
