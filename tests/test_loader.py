"""Tests for CSV loading and typed conversion."""

from datetime import date
from pathlib import Path

import pytest

from kzp_lab06.errors import DataError
from kzp_lab06.loader import load_records


def write_csv(path: Path, text: str) -> None:
    """Write test CSV text as UTF-8."""
    path.write_text(text, encoding="utf-8")


def test_load_records_converts_types_and_preserves_quoted_comma(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(
        path,
        'customer,kind,start,end,visits\n"Taras, Strong",MONTHLY,2026-10-05,2026-11-05,15\n',
    )

    records = load_records(path)

    assert records == [
        {
            "customer": "Taras, Strong",
            "kind": "MONTHLY",
            "start": date(2026, 10, 5),
            "end": date(2026, 11, 5),
            "visits": 15,
        }
    ]


def test_header_only_csv_returns_empty_list(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(path, "customer,kind,start,end,visits\n")

    assert load_records(path) == []


def test_physically_empty_csv_is_rejected(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(path, "")

    with pytest.raises(DataError, match="no header"):
        load_records(path)


def test_missing_column_is_rejected(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(path, "customer,kind,start,end\nIvan,MONTHLY,2026-10-01,2026-10-31\n")

    with pytest.raises(DataError, match="Invalid CSV header"):
        load_records(path)


def test_missing_value_is_rejected_with_row_and_field(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(path, "customer,kind,start,end,visits\nIvan,MONTHLY,2026-10-01,2026-10-31\n")

    with pytest.raises(DataError, match="Row 2: field visits is required"):
        load_records(path)


def test_invalid_integer_preserves_cause(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(
        path,
        "customer,kind,start,end,visits\nIvan,MONTHLY,2026-10-01,2026-10-31,abc\n",
    )

    with pytest.raises(DataError) as caught:
        load_records(path)

    assert "Row 2" in str(caught.value)
    assert "visits" in str(caught.value)
    assert isinstance(caught.value.__cause__, ValueError)


def test_negative_visits_are_rejected(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(
        path,
        "customer,kind,start,end,visits\nIvan,MONTHLY,2026-10-01,2026-10-31,-1\n",
    )

    with pytest.raises(DataError, match="cannot be negative"):
        load_records(path)


def test_invalid_date_preserves_cause(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(
        path,
        "customer,kind,start,end,visits\nIvan,MONTHLY,not-a-date,2026-10-31,1\n",
    )

    with pytest.raises(DataError) as caught:
        load_records(path)

    assert "field start" in str(caught.value)
    assert isinstance(caught.value.__cause__, ValueError)


def test_end_before_start_is_rejected(tmp_path: Path) -> None:
    path = tmp_path / "input.csv"
    write_csv(
        path,
        "customer,kind,start,end,visits\nIvan,MONTHLY,2026-11-01,2026-10-31,1\n",
    )

    with pytest.raises(DataError, match="end cannot be before start"):
        load_records(path)
