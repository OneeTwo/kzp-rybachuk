"""Tests that LAB_07 preserves the external LAB_06 report."""

from datetime import date
from pathlib import Path

from kzp_lab06.loader import load_records as load_lab06_records
from kzp_lab06.reporting import format_report as format_lab06_report
from kzp_lab07.loader import load_records as load_lab07_records
from kzp_lab07.reporting import format_report as format_lab07_report
from kzp_lab07.reporting import write_report


def test_lab07_report_matches_lab06_on_same_csv() -> None:
    input_path = Path("data/lab06_input.csv")
    reference_date = date(2026, 10, 15)

    old_report = format_lab06_report(load_lab06_records(input_path), reference_date)
    new_report = format_lab07_report(load_lab07_records(input_path), reference_date)

    assert new_report == old_report


def test_lab07_full_control_report_is_unchanged() -> None:
    records = load_lab07_records(Path("data/lab06_input.csv"))

    assert format_lab07_report(records, date(2026, 10, 15)) == (
        "Reference date: 2026-10-15\n"
        "Active memberships: 5\n"
        "Unique customers: 4\n"
        "Average visits: 42.40\n"
        "Leader: Maria Koval (105 visits)\n"
        "Visits by kind:\n"
        "ANNUAL: 185\n"
        "MONTHLY: 27\n"
    )


def test_report_writer_uses_utf8_and_creates_parent(tmp_path: Path) -> None:
    output = tmp_path / "reports" / "summary.txt"

    write_report(output, "Клієнт: Марія\n")

    assert output.read_text(encoding="utf-8") == "Клієнт: Марія\n"
