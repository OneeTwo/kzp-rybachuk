"""Command-line interface for LAB_06."""

from __future__ import annotations

import argparse
import sys
from datetime import date
from pathlib import Path
from collections.abc import Sequence

from . import __version__
from .errors import DataError
from .loader import load_records
from .reporting import format_report, write_report

DEFAULT_REFERENCE_DATE = "2026-10-15"


def _build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="kzp-lab06",
        description="Generate the LAB_06 Gym membership report from CSV.",
    )
    parser.add_argument("input", type=Path, nargs="?", help="input CSV path")
    parser.add_argument("output", type=Path, nargs="?", help="output report path")
    parser.add_argument(
        "--reference-date",
        default=DEFAULT_REFERENCE_DATE,
        help="date used to select active memberships (YYYY-MM-DD)",
    )
    parser.add_argument(
        "--version",
        action="version",
        version=f"kzp-lab06 {__version__}",
    )
    return parser


def main(argv: Sequence[str] | None = None) -> int:
    """Run the CLI and return a process exit code."""
    parser = _build_parser()
    args = parser.parse_args(argv)

    if args.input is None or args.output is None:
        parser.print_usage(sys.stderr)
        return 2

    try:
        try:
            reference_date = date.fromisoformat(args.reference_date)
        except ValueError as error:
            raise DataError("Reference date must use ISO YYYY-MM-DD") from error

        records = load_records(args.input)
        report = format_report(records, reference_date)
        write_report(args.output, report)
        print(report, end="")
        return 0
    except (DataError, OSError) as error:
        print(f"Error: {error}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
