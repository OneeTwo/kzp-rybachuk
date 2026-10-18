"""CLI behavior tests for LAB_07."""

from pathlib import Path

import pytest

from kzp_lab07.cli import main


def test_cli_success_preserves_external_report(
    tmp_path: Path,
    capsys: pytest.CaptureFixture[str],
) -> None:
    output = tmp_path / "summary.txt"

    code = main(["data/lab06_input.csv", str(output)])

    captured = capsys.readouterr()
    assert code == 0
    assert "Active memberships: 5" in captured.out
    assert "Average visits: 42.40" in captured.out
    assert output.read_text(encoding="utf-8") == captured.out


def test_cli_bad_csv_returns_nonzero_and_does_not_create_report(
    tmp_path: Path,
    capsys: pytest.CaptureFixture[str],
) -> None:
    input_path = tmp_path / "bad.csv"
    output = tmp_path / "report.txt"
    input_path.write_text(
        "customer,kind,start,end,visits\nIvan,MONTHLY,2026-10-01,2026-10-31,abc\n",
        encoding="utf-8",
    )

    code = main([str(input_path), str(output)])

    captured = capsys.readouterr()
    assert code == 2
    assert "Error:" in captured.err
    assert "Row 2" in captured.err
    assert not output.exists()


def test_cli_version_is_1_6_0(capsys: pytest.CaptureFixture[str]) -> None:
    with pytest.raises(SystemExit) as caught:
        main(["--version"])

    captured = capsys.readouterr()
    assert caught.value.code == 0
    assert captured.out == "kzp-lab07 1.6.0\n"
