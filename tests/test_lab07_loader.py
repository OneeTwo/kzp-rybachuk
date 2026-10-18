"""Tests for CSV to LAB_07 object conversion."""

from pathlib import Path

import pytest

from kzp_lab07.errors import DataError
from kzp_lab07.loader import load_records
from kzp_lab07.models import AnnualMembership, MonthlyMembership


def write_csv(path: Path, text: str) -> None:
    """Write CSV text as UTF-8."""
    path.write_text(text, encoding="utf-8")


def test_loader_builds_both_subtypes_and_preserves_quoted_comma(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(
        path,
        (
            "customer,kind,start,end,visits\n"
            '"Taras, Strong",MONTHLY,2026-10-05,2026-11-05,15\n'
            "Maria,ANNUAL,2026-01-01,2026-12-31,105\n"
        ),
    )

    records = load_records(path)

    assert isinstance(records[0], MonthlyMembership)
    assert isinstance(records[1], AnnualMembership)
    assert records[0].customer == "Taras, Strong"
    assert records[1].visits == 105


def test_header_only_csv_returns_empty_object_list(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(path, "customer,kind,start,end,visits\n")

    assert load_records(path) == []


def test_physically_empty_csv_is_rejected(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(path, "")

    with pytest.raises(DataError, match="no header"):
        load_records(path)


def test_invalid_kind_has_row_number(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(
        path,
        "customer,kind,start,end,visits\nIvan,WEEKLY,2026-10-01,2026-10-31,12\n",
    )

    with pytest.raises(DataError, match=r"Row 2: unsupported membership kind"):
        load_records(path)


def test_invalid_integer_preserves_cause_and_row_number(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(
        path,
        "customer,kind,start,end,visits\nIvan,MONTHLY,2026-10-01,2026-10-31,abc\n",
    )

    with pytest.raises(DataError) as caught:
        load_records(path)

    assert "Row 2" in str(caught.value)
    assert isinstance(caught.value.__cause__, ValueError)


def test_model_validation_error_is_wrapped_with_row_number(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(
        path,
        "customer,kind,start,end,visits\nIvan,MONTHLY,2026-10-01,2026-10-31,-1\n",
    )

    with pytest.raises(DataError) as caught:
        load_records(path)

    assert "Row 2: invalid membership values" in str(caught.value)
    assert isinstance(caught.value.__cause__, ValueError)


def test_wrong_header_is_rejected(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(path, "customer,kind,start,end\n")

    with pytest.raises(DataError, match="Invalid CSV header"):
        load_records(path)
