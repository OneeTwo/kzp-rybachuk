"""Object-oriented analysis for Gym memberships."""

from __future__ import annotations

from datetime import date

from .models import CompletionSummary, MembershipProtocol

DEFAULT_REFERENCE_DATE = date(2026, 10, 15)


def active_memberships(
    records: list[MembershipProtocol],
    reference_date: date = DEFAULT_REFERENCE_DATE,
) -> list[MembershipProtocol]:
    """Return memberships active on the reference date."""
    return [record for record in records if record.start <= reference_date <= record.end]


def unique_customers(records: list[MembershipProtocol]) -> set[str]:
    """Return unique customer names."""
    return {record.customer for record in records}


def visits_by_kind(records: list[MembershipProtocol]) -> dict[str, int]:
    """Aggregate visits by kind in deterministic key order."""
    kinds = {record.kind for record in records}
    return {
        kind: sum(record.visits for record in records if record.kind == kind)
        for kind in sorted(kinds)
    }


def average_visits(records: list[MembershipProtocol]) -> float:
    """Return average visits or zero for an empty collection."""
    if not records:
        return 0.0
    return sum(record.visits for record in records) / len(records)


def leader(records: list[MembershipProtocol]) -> MembershipProtocol | None:
    """Return the visit leader using deterministic tie-breaking."""
    if not records:
        return None
    return sorted(
        records,
        key=lambda record: (
            -record.visits,
            record.customer,
            record.kind,
            record.start,
        ),
    )[0]


def lowest_visit_cost(
    records: list[MembershipProtocol],
) -> MembershipProtocol | None:
    """Return the object with the lowest positive polymorphic visit cost."""
    candidates = [record for record in records if record.visits > 0]
    return min(
        candidates,
        key=lambda record: (record.visit_cost(), record.customer),
        default=None,
    )


def completion_summary(
    records: list[MembershipProtocol],
    reference_date: date = DEFAULT_REFERENCE_DATE,
) -> CompletionSummary:
    """Build an immutable summary of completed, active and future memberships."""
    completed = [record for record in records if record.end < reference_date]
    active = [record for record in records if record.start <= reference_date <= record.end]
    future = [record for record in records if record.start > reference_date]
    upcoming_ends = sorted(record.end for record in records if record.end >= reference_date)

    return CompletionSummary(
        reference_date=reference_date,
        completed_count=len(completed),
        active_count=len(active),
        future_count=len(future),
        next_completion=upcoming_ends[0] if upcoming_ends else None,
    )


def summarize(
    records: list[MembershipProtocol],
    *metrics: str,
    reference_date: date = DEFAULT_REFERENCE_DATE,
    **labels: str,
) -> dict[str, object]:
    """Preserve LAB_06 summary behavior while operating on objects."""
    selected = active_memberships(records, reference_date)
    result: dict[str, object] = {"count": len(selected)}

    for metric in metrics:
        if metric != "visits":
            raise ValueError(f"Unknown metric: {metric}")

        label = labels.get(metric, metric)
        result[label] = sum(float(getattr(record, metric)) for record in selected)

    return result
