"""Tests for object-oriented LAB_07 analysis."""

from datetime import date

import pytest

from kzp_lab07.analysis import (
    active_memberships,
    average_visits,
    completion_summary,
    leader,
    lowest_visit_cost,
    summarize,
    unique_customers,
    visits_by_kind,
)
from kzp_lab07.models import AnnualMembership, MonthlyMembership


def sample_records() -> list[MonthlyMembership | AnnualMembership]:
    """Return the LAB_06 control records as LAB_07 objects."""
    return [
        MonthlyMembership("Ivan Petrenko", date(2026, 10, 1), date(2026, 10, 31), 12),
        AnnualMembership("Maria Koval", date(2026, 1, 1), date(2026, 12, 31), 105),
        MonthlyMembership("Oleh Bondar", date(2026, 9, 1), date(2026, 9, 30), 8),
        AnnualMembership("Anna Melnyk", date(2026, 2, 1), date(2027, 1, 31), 80),
        MonthlyMembership("Taras, Strong", date(2026, 10, 5), date(2026, 11, 5), 15),
        MonthlyMembership("Maria Koval", date(2026, 10, 10), date(2026, 10, 20), 0),
    ]


def test_previous_lab06_analysis_results_are_preserved() -> None:
    active = active_memberships(sample_records(), date(2026, 10, 15))

    assert len(active) == 5
    assert unique_customers(active) == {
        "Ivan Petrenko",
        "Maria Koval",
        "Anna Melnyk",
        "Taras, Strong",
    }
    assert visits_by_kind(active) == {"ANNUAL": 185, "MONTHLY": 27}
    assert average_visits(active) == pytest.approx(42.4)
    assert leader(active).customer == "Maria Koval"


def test_completion_summary_matches_variant_requirement() -> None:
    summary = completion_summary(sample_records(), date(2026, 10, 15))

    assert summary.completed_count == 1
    assert summary.active_count == 5
    assert summary.future_count == 0
    assert summary.next_completion == date(2026, 10, 20)


def test_empty_completion_summary_is_valid() -> None:
    summary = completion_summary([], date(2026, 10, 15))

    assert summary.completed_count == 0
    assert summary.active_count == 0
    assert summary.future_count == 0
    assert summary.next_completion is None


def test_lowest_visit_cost_uses_protocol_without_subtype_branching() -> None:
    records = sample_records()

    result = lowest_visit_cost(records)

    assert result is not None
    assert result.customer == "Taras, Strong"


def test_summarize_keeps_lab06_args_kwargs_contract() -> None:
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
