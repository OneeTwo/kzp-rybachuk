# KZP — LAB_07 Object-Oriented Python Data Processing

Laboratory Work No. 7 for the Cross-Platform Programming course.

**Variant:** 20 — Gym  
**Python package version:** 1.6.0  
**Python:** 3.11+  
**Reference date:** 2026-10-15

## Repository

https://github.com/OneeTwo/kzp-rybachuk

LAB_07 branch:

https://github.com/OneeTwo/kzp-rybachuk/tree/LAB_07

## Continuity from LAB_06

LAB_07 keeps the LAB_06 CSV fields and external report unchanged.

The previous dictionary model remains in src/kzp_lab06/ together with all LAB_06 tests. LAB_07 adds a new object-oriented package in src/kzp_lab07/. This makes the before/after implementation directly testable in the same repository.

Version progression:

- LAB_01 — 1.0.0
- LAB_02 — 1.1.0
- LAB_03 — 1.2.0
- LAB_04 — 1.3.0
- LAB_05 — 1.4.0
- LAB_06 — 1.5.0
- LAB_07 — 1.6.0

The final LAB_07 release tag is v1.6.0.

## Variant 20 — Gym

The CSV contract is preserved:

~~~text
customer,kind,start,end,visits
~~~

Variant object model:

~~~text
Membership
├── MonthlyMembership
└── AnnualMembership
~~~

Common polymorphic operation:

~~~text
visit_cost()
~~~

Variant summary:

~~~text
membership completion state
~~~

The external report remains the LAB_06 report:

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

## Object Model

Membership stores common state and validates it during construction.

The visits attribute is exposed through @property and a setter. Zero is a valid boundary value; negative values and non-integer values are rejected.

Membership implements:

- __str__ for a short reader-facing representation;
- __repr__ for diagnostic output;
- __eq__ for logical equality of the same subtype and state.

Ordering for report analysis remains explicit through a key function instead of adding an artificial global __lt__ order.

## Dataclass

CompletionSummary is declared with @dataclass(frozen=True).

It stores:

- reference date;
- completed membership count;
- active membership count;
- future membership count;
- nearest completion date.

The dataclass is immutable and compared by value.

## Subtypes and Protocol

MonthlyMembership and AnnualMembership override visit_cost().

The methodology fixes the operation as “visit cost by tariff” but does not provide numeric tariff values and requires the CSV fields to stay unchanged. Therefore LAB_07 keeps tariff values as subtype class constants instead of adding a new CSV column:

- monthly tariff constant — 600.0;
- annual tariff constant — 6000.0;
- annual benefit coefficient — 0.90.

These constants exist only for the polymorphic OOP operation and do not change the LAB_06 report.

MembershipProtocol defines the structural contract used by analysis. It contains the shared properties and visit_cost() method. Analysis does not use subtype-specific isinstance branches.

## Loader

src/kzp_lab07/loader.py reads the same UTF-8 CSV through csv.DictReader.

Subtype selection happens only at the loading boundary:

- MONTHLY → MonthlyMembership;
- ANNUAL → AnnualMembership.

After the object is created, analysis works through common properties and the protocol.

Invalid rows produce DataError with the physical CSV row number. Conversion and model errors preserve their original cause where applicable.

## Modules

~~~text
src/kzp_lab07/
├── __init__.py
├── __main__.py
├── analysis.py
├── cli.py
├── errors.py
├── loader.py
├── models.py
└── reporting.py
~~~

Responsibilities:

- models.py — base class, properties, dataclass, subtypes and Protocol;
- loader.py — CSV → objects;
- analysis.py — calculations over the common object contract;
- reporting.py — deterministic LAB_06-compatible report;
- errors.py — DataError;
- cli.py — CLI arguments and exit codes.

## Setup

~~~powershell
git checkout LAB_07
git pull
py -m venv .venv
.\.venv\Scripts\python.exe -m pip install -e ".[dev]"
~~~

## Run

~~~powershell
.\.venv\Scripts\python.exe -m kzp_lab07.cli data\lab06_input.csv reports\summary.txt
~~~

Version:

~~~powershell
.\.venv\Scripts\python.exe -m kzp_lab07.cli --version
~~~

Expected:

~~~text
kzp-lab07 1.6.0
~~~

## Tests

LAB_07 keeps all LAB_06 tests and adds object-model behavior tests for:

- setter zero, valid and invalid values;
- constructor invariants;
- frozen dataclass behavior;
- equality of duplicate objects;
- __str__ and __repr__;
- different subtype formulas;
- runtime Protocol compatibility;
- CSV → subtype conversion;
- row-numbered bad data;
- empty CSV cases;
- completion summary;
- protocol-based analysis;
- report equivalence with LAB_06;
- successful and failing CLI scenarios;
- version 1.6.0.

Run:

~~~powershell
.\.venv\Scripts\python.exe -m pytest
~~~

## Quality and Packaging

~~~powershell
.\.venv\Scripts\ruff.exe check .
.\.venv\Scripts\ruff.exe format --check .
.\.venv\Scripts\python.exe -m build --wheel
~~~

The wheel is verified in a new clean virtual environment through tools/verify_wheel.py.

## CI

.github/workflows/python-ci.yml runs on Ubuntu, Windows and macOS.

Each job runs pytest, Ruff lint, Ruff format check, wheel build, clean-environment wheel installation and packaged CLI verification.

The Java CI workflow and previous Java tests remain in the repository.

Successful LAB_07 branch CI:

https://github.com/OneeTwo/kzp-rybachuk/actions/runs/39172672483

Current result: 51 pytest tests passed, Ruff lint and formatting passed, the 1.6.0 wheel built and installed in a clean environment, and all three OS jobs succeeded.

## GitHub Issues

LAB_07 work is tracked by:

- #72 — Membership object model;
- #73 — frozen completion dataclass;
- #74 — subtypes and Protocol;
- #75 — object loader and analysis;
- #76 — behavior tests;
- #77 — packaging and CI;
- #78 — README and REPORT.

The final Pull Request targets main and closes Issues #72–#78.

## Pull Request

https://github.com/OneeTwo/kzp-rybachuk/pull/79
