"""Verify the built wheel in an isolated virtual environment."""

from __future__ import annotations

import subprocess
import sys
import tempfile
import venv
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def main() -> int:
    """Install the wheel into a clean venv and run the packaged CLI."""
    wheels = sorted((ROOT / "dist").glob("*.whl"))
    if len(wheels) != 1:
        raise RuntimeError(f"Expected exactly one wheel, found {len(wheels)}")

    with tempfile.TemporaryDirectory() as temporary:
        env_dir = Path(temporary) / "wheel-env"
        venv.EnvBuilder(with_pip=True).create(env_dir)

        if sys.platform == "win32":
            python = env_dir / "Scripts" / "python.exe"
        else:
            python = env_dir / "bin" / "python"

        subprocess.run(
            [str(python), "-m", "pip", "install", "--no-deps", str(wheels[0])],
            check=True,
        )

        output = Path(temporary) / "summary.txt"
        result = subprocess.run(
            [
                str(python),
                "-m",
                "kzp_lab06.cli",
                str(ROOT / "data" / "lab06_input.csv"),
                str(output),
            ],
            check=True,
            capture_output=True,
            text=True,
        )

        report = output.read_text(encoding="utf-8")
        if "Active memberships: 5" not in report:
            raise RuntimeError("Installed wheel produced an unexpected report")
        if "kzp_lab06" in result.stderr:
            raise RuntimeError("Installed CLI wrote an unexpected error")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
