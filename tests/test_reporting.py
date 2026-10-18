"""Tests for deterministic report formatting and writing."""

from datetime import date
from pathlib import Path

from kzp_lab06.reporting import format_report, write_report


def test_format_report_is_deterministic() -> None:
    records = [
        {
            "customer": "Ivan",
            "kind": "MONTHLY",
            "start": date(2026, 10, 1),
            "end": date(2026, 10, 31),
            "visits": 12,
        },
        {
            "customer": "Maria",
            "kind": "ANNUAL",
            "start": date(2026, 1, 1),
            "end": date(2026, 12, 31),
            "visits": 105,
        },
    ]

    report = format_report(records, date(2026, 10, 15))

    assert report == (
        "Reference date: 2026-10-15\n"
        "Active memberships: 2\n"
        "Unique customers: 2\n"
        "Average visits: 58.50\n"
        "Leader: Maria (105 visits)\n"
        "Visits by kind:\n"
        "ANNUAL: 105\n"
        "MONTHLY: 12\n"
    )


def test_format_report_handles_empty_active_selection() -> None:
    report = format_report([], date(2026, 10, 15))

    assert report == (
        "Reference date: 2026-10-15\n"
        "Active memberships: 0\n"
        "Unique customers: 0\n"
        "Average visits: 0.00\n"
        "Leader: none\n"
        "Visits by kind:\n"
        "none\n"
    )


def test_write_report_creates_parent_and_preserves_utf8(tmp_path: Path) -> None:
    output = tmp_path / "reports" / "summary.txt"

    write_report(output, "Клієнт: Марія\n")

    assert output.read_text(encoding="utf-8") == "Клієнт: Марія\n"
