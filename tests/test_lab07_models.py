"""Behavior tests for the LAB_07 object model."""

import dataclasses
from datetime import date

import pytest

from kzp_lab07.models import (
    AnnualMembership,
    CompletionSummary,
    MembershipProtocol,
    MonthlyMembership,
)


def test_visits_property_accepts_zero_boundary_and_positive_values() -> None:
    membership = MonthlyMembership(
        "Ivan",
        date(2026, 10, 1),
        date(2026, 10, 31),
        0,
    )

    assert membership.visits == 0

    membership.visits = 12

    assert membership.visits == 12


def test_visits_property_rejects_negative_and_non_integer_values() -> None:
    membership = MonthlyMembership(
        "Ivan",
        date(2026, 10, 1),
        date(2026, 10, 31),
        1,
    )

    with pytest.raises(ValueError, match="negative"):
        membership.visits = -1

    with pytest.raises(ValueError, match="integer"):
        membership.visits = 1.5  # type: ignore[assignment]


def test_constructor_rejects_blank_customer_and_invalid_dates() -> None:
    with pytest.raises(ValueError, match="Customer"):
        MonthlyMembership(
            " ",
            date(2026, 10, 1),
            date(2026, 10, 31),
            1,
        )

    with pytest.raises(ValueError, match="End date"):
        MonthlyMembership(
            "Ivan",
            date(2026, 11, 1),
            date(2026, 10, 31),
            1,
        )


def test_completion_summary_is_frozen_and_compares_by_value() -> None:
    first = CompletionSummary(
        reference_date=date(2026, 10, 15),
        completed_count=1,
        active_count=5,
        future_count=0,
        next_completion=date(2026, 10, 20),
    )
    second = CompletionSummary(
        reference_date=date(2026, 10, 15),
        completed_count=1,
        active_count=5,
        future_count=0,
        next_completion=date(2026, 10, 20),
    )

    assert first == second

    with pytest.raises(dataclasses.FrozenInstanceError):
        first.active_count = 4  # type: ignore[misc]


def test_membership_equality_requires_same_subtype_and_state() -> None:
    start = date(2026, 10, 1)
    end = date(2026, 10, 31)
    first = MonthlyMembership("Ivan", start, end, 12)
    duplicate = MonthlyMembership("Ivan", start, end, 12)
    annual = AnnualMembership("Ivan", start, end, 12)

    assert first == duplicate
    assert first != annual
    assert first != object()


def test_str_and_repr_are_readable_and_diagnostic() -> None:
    membership = MonthlyMembership(
        "Ivan",
        date(2026, 10, 1),
        date(2026, 10, 31),
        12,
    )

    assert str(membership) == "Ivan: MONTHLY, 2026-10-01..2026-10-31, 12 visits"
    assert repr(membership).startswith("MonthlyMembership(")
    assert "customer='Ivan'" in repr(membership)


def test_subtypes_have_different_visit_cost_formulas() -> None:
    monthly = MonthlyMembership(
        "Ivan",
        date(2026, 10, 1),
        date(2026, 10, 31),
        12,
    )
    annual = AnnualMembership(
        "Maria",
        date(2026, 1, 1),
        date(2026, 12, 31),
        105,
    )

    assert monthly.visit_cost() == pytest.approx(50.0)
    assert annual.visit_cost() == pytest.approx(51.4285714286)
    assert monthly.visit_cost() != annual.visit_cost()


def test_zero_visits_has_zero_visit_cost_for_both_subtypes() -> None:
    start = date(2026, 10, 1)
    end = date(2026, 10, 31)

    assert MonthlyMembership("A", start, end, 0).visit_cost() == 0.0
    assert AnnualMembership("B", start, end, 0).visit_cost() == 0.0


def test_both_subtypes_satisfy_runtime_protocol() -> None:
    start = date(2026, 10, 1)
    end = date(2026, 10, 31)

    assert isinstance(MonthlyMembership("A", start, end, 1), MembershipProtocol)
    assert isinstance(AnnualMembership("B", start, end, 1), MembershipProtocol)
