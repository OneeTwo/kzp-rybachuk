"""Tests for Gym analysis functions."""

from datetime import date

import pytest

from kzp_lab06.analysis import (
    active_memberships,
    average_visits,
    leader,
    summarize,
    unique_customers,
    visits_by_kind,
)


def record(
    customer: str,
    kind: str,
    start: str,
    end: str,
    visits: int,
) -> dict[str, object]:
    """Create one typed membership record for analysis tests."""
    return {
        "customer": customer,
        "kind": kind,
        "start": date.fromisoformat(start),
        "end": date.fromisoformat(end),
        "visits": visits,
    }


def sample_records() -> list[dict[str, object]]:
    """Return records containing active, inactive, duplicate and zero-visit cases."""
    return [
        record("Ivan", "MONTHLY", "2026-10-01", "2026-10-31", 12),
        record("Maria", "ANNUAL", "2026-01-01", "2026-12-31", 105),
        record("Oleh", "MONTHLY", "2026-09-01", "2026-09-30", 8),
        record("Anna", "ANNUAL", "2026-02-01", "2027-01-31", 80),
        record("Taras", "MONTHLY", "2026-10-05", "2026-11-05", 15),
        record("Maria", "MONTHLY", "2026-10-10", "2026-10-20", 0),
    ]


def test_active_memberships_includes_date_boundaries() -> None:
    records = [
        record("A", "MONTHLY", "2026-10-15", "2026-10-31", 1),
        record("B", "MONTHLY", "2026-10-01", "2026-10-15", 2),
        record("C", "MONTHLY", "2026-10-16", "2026-10-31", 3),
    ]

    selected = active_memberships(records, date(2026, 10, 15))

    assert [item["customer"] for item in selected] == ["A", "B"]


def test_unique_customers_removes_duplicates() -> None:
    active = active_memberships(sample_records(), date(2026, 10, 15))

    assert unique_customers(active) == {"Ivan", "Maria", "Anna", "Taras"}


def test_visits_by_kind_aggregates_in_expected_groups() -> None:
    active = active_memberships(sample_records(), date(2026, 10, 15))

    assert visits_by_kind(active) == {"ANNUAL": 185, "MONTHLY": 27}


def test_average_visits_and_leader() -> None:
    active = active_memberships(sample_records(), date(2026, 10, 15))

    assert average_visits(active) == pytest.approx(42.4)
    assert leader(active)["customer"] == "Maria"
    assert leader(active)["visits"] == 105


def test_empty_selection_has_zero_average_and_no_leader() -> None:
    selected = active_memberships(sample_records(), date(2030, 1, 1))

    assert selected == []
    assert average_visits(selected) == 0.0
    assert leader(selected) is None
    assert visits_by_kind(selected) == {}


def test_summarize_uses_default_args_kwargs_and_reference_date() -> None:
    result = summarize(
        sample_records(),
        "visits",
        reference_date=date(2026, 10, 15),
        visits="Total visits",
    )

    assert result == {"count": 5, "Total visits": 212.0}


def test_summarize_rejects_unknown_metric() -> None:
    with pytest.raises(ValueError, match="Unknown metric"):
        summarize(sample_records(), "price")
