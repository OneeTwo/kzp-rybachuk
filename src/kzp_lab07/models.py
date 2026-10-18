"""Object model for Gym memberships in LAB_07."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass
from datetime import date
from typing import Protocol, runtime_checkable


@runtime_checkable
class MembershipProtocol(Protocol):
    """Common structural contract used by analysis without subtype checks."""

    @property
    def customer(self) -> str:
        """Return the customer name."""
        ...

    @property
    def kind(self) -> str:
        """Return the membership kind."""
        ...

    @property
    def start(self) -> date:
        """Return the membership start date."""
        ...

    @property
    def end(self) -> date:
        """Return the membership end date."""
        ...

    @property
    def visits(self) -> int:
        """Return the number of visits."""
        ...

    def visit_cost(self) -> float:
        """Return the effective cost of one visit."""
        ...


@dataclass(frozen=True)
class CompletionSummary:
    """Immutable summary of membership completion state."""

    reference_date: date
    completed_count: int
    active_count: int
    future_count: int
    next_completion: date | None


class Membership(ABC):
    """Base membership with validated shared state and behavior."""

    KIND = "MEMBERSHIP"

    def __init__(
        self,
        customer: str,
        start: date,
        end: date,
        visits: int,
    ) -> None:
        """Create a validated membership."""
        customer = customer.strip()
        if not customer:
            raise ValueError("Customer cannot be empty")
        if end < start:
            raise ValueError("End date cannot be before start date")

        self._customer = customer
        self._start = start
        self._end = end
        self.visits = visits

    @property
    def customer(self) -> str:
        """Return the customer name."""
        return self._customer

    @property
    def kind(self) -> str:
        """Return the stable CSV kind represented by the subtype."""
        return self.KIND

    @property
    def start(self) -> date:
        """Return the start date."""
        return self._start

    @property
    def end(self) -> date:
        """Return the end date."""
        return self._end

    @property
    def visits(self) -> int:
        """Return the number of visits."""
        return self._visits

    @visits.setter
    def visits(self, value: int) -> None:
        """Set a non-negative visit count."""
        if not isinstance(value, int) or isinstance(value, bool):
            raise ValueError("Visits must be an integer")
        if value < 0:
            raise ValueError("Visits cannot be negative")
        self._visits = value

    @abstractmethod
    def visit_cost(self) -> float:
        """Return the subtype-specific effective cost of one visit."""
        raise NotImplementedError

    def __str__(self) -> str:
        """Return a concise representation for a reader."""
        return (
            f"{self.customer}: {self.kind}, "
            f"{self.start.isoformat()}..{self.end.isoformat()}, "
            f"{self.visits} visits"
        )

    def __repr__(self) -> str:
        """Return a diagnostic representation with constructor-like values."""
        return (
            f"{type(self).__name__}("
            f"customer={self.customer!r}, "
            f"start={self.start!r}, "
            f"end={self.end!r}, "
            f"visits={self.visits!r})"
        )

    def __eq__(self, other: object) -> bool:
        """Compare memberships by concrete subtype and logical state."""
        if not isinstance(other, Membership):
            return NotImplemented
        return type(self) is type(other) and (
            self.customer,
            self.start,
            self.end,
            self.visits,
        ) == (
            other.customer,
            other.start,
            other.end,
            other.visits,
        )


class MonthlyMembership(Membership):
    """Monthly membership with a monthly tariff formula."""

    KIND = "MONTHLY"
    MONTHLY_TARIFF = 600.0

    def visit_cost(self) -> float:
        """Return monthly tariff cost per actual visit."""
        if self.visits == 0:
            return 0.0
        return self.MONTHLY_TARIFF / self.visits


class AnnualMembership(Membership):
    """Annual membership with a reduced annual tariff formula."""

    KIND = "ANNUAL"
    ANNUAL_TARIFF = 6000.0
    ANNUAL_BENEFIT = 0.90

    def visit_cost(self) -> float:
        """Return annual tariff cost per visit with the annual benefit."""
        if self.visits == 0:
            return 0.0
        return self.ANNUAL_TARIFF * self.ANNUAL_BENEFIT / self.visits
