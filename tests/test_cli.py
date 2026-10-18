"""Tests for the LAB_06 command-line boundary."""

from pathlib import Path

import pytest

from kzp_lab06.cli import main


def test_cli_success_writes_report_and_returns_zero(
    tmp_path: Path,
    capsys: pytest.CaptureFixture[str],
) -> None:
    input_path = tmp_path / "input.csv"
    output_path = tmp_path / "reports" / "summary.txt"
    input_path.write_text(
        "customer,kind,start,end,visits\n"
        "Ivan,MONTHLY,2026-10-01,2026-10-31,12\n",
        encoding="utf-8",
    )

    code = main([str(input_path), str(output_path)])

    captured = capsys.readouterr()
    assert code == 0
    assert "Active memberships: 1" in captured.out
    assert output_path.exists()


def test_cli_bad_csv_returns_nonzero_and_stderr(
    tmp_path: Path,
    capsys: pytest.CaptureFixture[str],
) -> None:
    input_path = tmp_path / "input.csv"
    output_path = tmp_path / "report.txt"
    input_path.write_text(
        "customer,kind,start,end,visits\n"
        "Ivan,MONTHLY,2026-10-01,2026-10-31,abc\n",
        encoding="utf-8",
    )

    code = main([str(input_path), str(output_path)])

    captured = capsys.readouterr()
    assert code == 2
    assert "Error:" in captured.err
    assert "Row 2" in captured.err
    assert not output_path.exists()


def test_cli_invalid_reference_date_returns_nonzero(
    tmp_path: Path,
    capsys: pytest.CaptureFixture[str],
) -> None:
    input_path = tmp_path / "input.csv"
    output_path = tmp_path / "report.txt"
    input_path.write_text(
        "customer,kind,start,end,visits\n",
        encoding="utf-8",
    )

    code = main(
        [
            str(input_path),
            str(output_path),
            "--reference-date",
            "not-a-date",
        ]
    )

    captured = capsys.readouterr()
    assert code == 2
    assert "Reference date must use ISO YYYY-MM-DD" in captured.err


def test_cli_version(capsys: pytest.CaptureFixture[str]) -> None:
    with pytest.raises(SystemExit) as caught:
        main(["--version"])

    captured = capsys.readouterr()
    assert caught.value.code == 0
    assert captured.out == "kzp-lab06 1.5.0\n"
