# KZP — LAB_06 Python Data Processing

Laboratory Work No. 6 for the Cross-Platform Programming course.

**Variant:** 20 — Gym  
**Python package version:** 1.5.0  
**Python:** 3.11+  
**Reference date:** 2026-10-15

## Repository

https://github.com/OneeTwo/kzp-rybachuk

LAB_06 branch:

https://github.com/OneeTwo/kzp-rybachuk/tree/LAB_06

## Continuity

LAB_06 starts the Python stage, but the previous Java work is not deleted. The repository still contains the Java sources, Maven tests, Maven Wrapper and previous Java CI. LAB_06 adds a separate Python package, pytest suite, Ruff configuration, wheel packaging and Python CI.

Version progression: LAB_01 1.0.0, LAB_02 1.1.0, LAB_03 1.2.0, LAB_04 1.3.0, LAB_05 1.4.0, LAB_06 1.5.0. The final LAB_06 release tag is v1.5.0.

## Variant 20 — Gym

CSV header:

~~~text
customer,kind,start,end,visits
~~~

Typed values:

- customer — string;
- kind — string;
- start — datetime.date;
- end — datetime.date;
- visits — integer.

Required operations:

- filter memberships active on reference_date;
- aggregate visits by kind;
- build a set of unique customers;
- calculate average visits;
- determine the visit leader.

The fixed reference date is 2026-10-15.

## Input Data

LAB_06 uses data/lab06_input.csv. It contains six records, a duplicate customer value, an inactive membership, a zero-visits boundary record and a quoted customer containing a comma.

## Python Project Structure

~~~text
src/kzp_lab06/
├── __init__.py
├── __main__.py
├── analysis.py
├── cli.py
├── errors.py
├── loader.py
└── reporting.py

tests/
├── test_analysis.py
├── test_cli.py
├── test_loader.py
└── test_reporting.py
~~~

loader.py handles csv.DictReader, validation and typed conversion. analysis.py handles lists, dictionaries, sets, comprehensions and calculations. reporting.py creates a deterministic report and writes UTF-8. errors.py defines DataError. cli.py handles arguments and exit codes.

## Setup

~~~powershell
py -m venv .venv
.\.venv\Scripts\python.exe -m pip install -e ".[dev]"
~~~

The virtual environment is ignored by Git.

## Run

~~~powershell
.\.venv\Scripts\python.exe -m kzp_lab06.cli data\lab06_input.csv reports\summary.txt
~~~

Installed console script:

~~~powershell
kzp-lab06 data\lab06_input.csv reports\summary.txt
~~~

Version:

~~~powershell
.\.venv\Scripts\python.exe -m kzp_lab06.cli --version
~~~

Expected:

~~~text
kzp-lab06 1.5.0
~~~

## Expected Report

~~~text
Reference date: 2026-10-15
Active memberships: 5
Unique customers: 4
Average visits: 42.40
Leader: Maria Koval (105 visits)
Visits by kind:
ANNUAL: 185
MONTHLY: 27
~~~

## Tests and Quality

~~~powershell
.\.venv\Scripts\python.exe -m pytest
.\.venv\Scripts\ruff.exe check .
.\.venv\Scripts\ruff.exe format --check .
.\.venv\Scripts\python.exe -m build
~~~

The tests cover correct data, empty input, duplicates, quoted commas, invalid integers and dates, negative visits, missing columns/values, date boundaries, empty selection, deterministic report output, UTF-8 writing, CLI success/error scenarios and version output.

## CI

.github/workflows/python-ci.yml runs on Ubuntu, Windows and macOS. Each OS runs pytest, Ruff lint, Ruff format check and wheel build, then uploads the wheel artifact.

The existing Java CI workflow is kept so previous Java infrastructure and tests remain available.

## GitHub Issues

LAB_06 is tracked by Issues #64–#70:

- #64 CSV loader and DataError;
- #65 Gym analysis;
- #66 reporting and CLI;
- #67 pytest coverage;
- #68 Ruff and wheel packaging;
- #69 cross-platform Python CI;
- #70 documentation.
