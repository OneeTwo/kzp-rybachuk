# Laboratory Work No. 6 Report

## 1. Topic, Number and Variant

**Topic:** Python Data Processing  
**Laboratory Work:** No. 6  
**Variant:** 20 — Gym  
**Operating System:** Windows 11  
**Python:** 3.11+; CI uses Python 3.12  
**Version:** 1.5.0

**Repository:**  
https://github.com/OneeTwo/kzp-rybachuk

**Branch:**  
https://github.com/OneeTwo/kzp-rybachuk/tree/LAB_06

LAB_06 adds a Python stage without deleting the Java sources, Java tests, Maven Wrapper or previous Java CI.

## 2. Objective

The goal is to create a modular Python application that reads Gym CSV data, converts fields to typed values, analyses them with lists, dictionaries, sets and comprehensions, produces a deterministic report, and writes it to a UTF-8 text file.

The complete solution also keeps pytest, Ruff, wheel packaging, versioning, GitHub Issues, Pull Request and cross-platform CI.

## 3. CSV Format

Header:

~~~text
customer,kind,start,end,visits
~~~

Types after loading:

~~~text
customer -> str
kind     -> str
start    -> datetime.date
end      -> datetime.date
visits   -> int
~~~

Input file: data/lab06_input.csv

Example escaped row:

~~~text
"Taras, Strong",MONTHLY,2026-10-05,2026-11-05,15
~~~

A physical file without a header is rejected with DataError. A header-only file is valid and produces an empty list. CSV is read with csv.DictReader, UTF-8 and newline="".

## 4. Module Structure

~~~text
src/kzp_lab06/
├── __init__.py
├── __main__.py
├── analysis.py
├── cli.py
├── errors.py
├── loader.py
└── reporting.py
~~~

loader.py reads and validates CSV. analysis.py performs filtering and aggregation. reporting.py formats and writes the report. errors.py defines DataError. cli.py handles arguments, output and exit codes.

## 5. Data Conversion and Collections

Each valid row becomes a dictionary with strings, date objects and an integer. The program uses a list of records, a set of unique customers, a dictionary of visit totals by kind, and list/set/dictionary comprehensions.

Validation is performed before data reaches the analysis layer.

## 6. Functions, Default Values, *args and **kwargs

The analysis module contains summarize(records, *metrics, reference_date=DEFAULT_REFERENCE_DATE, **labels).

metrics is a tuple, labels is a dictionary, and reference_date has a safe immutable default.

Example:

~~~python
summarize(
    records,
    "visits",
    reference_date=date(2026, 10, 15),
    visits="Total visits",
)
~~~

## 7. Analysis Algorithm

Fixed reference date:

~~~text
2026-10-15
~~~

A membership is active when start <= reference_date <= end.

For active records the program builds the active list, unique-customer set and visits-by-kind dictionary, calculates average visits and finds the leader.

Expected result for the sample:

~~~text
Active memberships: 5
Unique customers: 4
Average visits: 42.40
Leader: Maria Koval (105 visits)
ANNUAL: 185
MONTHLY: 27
~~~

An empty selection is valid: average is 0.00, leader is none, and grouped totals are empty.

## 8. Error Handling

DataError extends ValueError.

Handled cases include invalid header, empty required value, invalid visits integer, negative visits, invalid ISO dates, end before start, missing value and extra row values.

Integer and date conversion use raise ... from error so the original ValueError remains available as the cause.

CLI exit codes:

~~~text
0 — success
2 — expected argument, input or data error
~~~

No bare except is used.

## 9. File Report

Report path example:

~~~text
reports/summary.txt
~~~

The report is written with Path.open, with, encoding="utf-8" and newline="\n". The input CSV is never modified.

Full expected report:

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

## 10. Testing

Python tests are in tests/.

They cover typed loading, quoted comma, header-only empty input, physical empty file, missing column, missing value, invalid integer with cause, negative visits, invalid date with cause, end before start, inclusive date boundaries, duplicate customer values, aggregation, average, leader, empty selection, *args/**kwargs, unknown metric, deterministic report, UTF-8 writing, CLI success, CLI damaged input, invalid reference date and version output.

The previous Java tests remain in the repository.

## 11. Quality and Packaging

Development dependencies are declared in pyproject.toml.

Windows commands:

~~~powershell
.\.venv\Scripts\python.exe -m pytest
.\.venv\Scripts\ruff.exe check .
.\.venv\Scripts\ruff.exe format --check .
.\.venv\Scripts\python.exe -m build
~~~

The wheel is generated in dist/ and is ignored by Git.

Python package version: 1.5.0.  
Final Git tag after verification: v1.5.0.

The Maven project remains 1.4.0 because LAB_06 does not change the Java implementation.

## 12. GitHub Actions

LAB_06 adds .github/workflows/python-ci.yml.

The CI matrix contains Ubuntu, Windows and macOS. Every job installs the package with dev dependencies, runs pytest, ruff check, ruff format --check, builds a wheel and uploads the wheel artifact.

The previous Java workflow is preserved.

The final CI and artifact links are added after the Pull Request is created.

## 13. GitHub Issues and Pull Request

| Issue | Change | Verification |
|---|---|---|
| #64 | CSV loader and DataError | loader tests |
| #65 | Gym analysis | analysis tests |
| #66 | reporting and CLI | report and CLI tests |
| #67 | pytest coverage | complete Python suite |
| #68 | Ruff and wheel packaging | lint, format and build |
| #69 | Python CI | three operating systems |
| #70 | documentation | README and REPORT |

Branch: LAB_06

The final Pull Request targets main and closes Issues #64–#70.

## 14. Academic Integrity

ChatGPT was used as an AI assistant.

Roles used: Manager, Developer, Validator, DevOps, Documenter and Reviewer.

Accepted recommendations include using csv.DictReader, Path, explicit UTF-8, DataError, raise ... from, a fixed reference date, deterministic ordering, pytest, Ruff, wheel packaging and a separate Python CI workflow while preserving Java infrastructure.

The implementation and generated suggestions must be reviewed, run and understood before submission.

## 15. Control Questions

1. What is pyproject.toml used for?  
It defines the Python package, Python version, dependencies, console script, pytest and Ruff configuration.

2. Why create a virtual environment?  
It isolates project dependencies and makes the setup reproducible.

3. Why use newline="" for CSV?  
It lets the csv module handle CSV line endings correctly.

4. What does DictReader return before conversion?  
CSV values are strings; missing values may be None.

5. Why is split(",") unsuitable?  
A comma can occur inside a quoted field and must not be treated as a separator.

6. How do list, dictionary and set differ?  
A list stores ordered items and duplicates, a dictionary maps keys to values, and a set stores unique values.

7. When is comprehension useful?  
For a clear one-step filtering, transformation or collection construction.

8. What are *args and **kwargs?  
*args collects extra positional arguments into a tuple and **kwargs collects named arguments into a dictionary.

9. Why avoid a mutable default value?  
The same mutable object can be reused between calls and retain state.

10. What does raise ... from error do?  
It preserves the original exception as the cause of a higher-level exception.

11. Why separate loading, analysis and reporting?  
Each module has one responsibility and can be tested independently.

12. What does with guarantee for a file?  
The file is closed when the context exits, including after an exception.

13. Why deterministic report ordering?  
The same input should generate the same report on every run and OS.

14. Which empty CSV cases are tested?  
A file without a header is rejected; a header-only file produces an empty record list.

15. What is a behavior test?  
It verifies externally observable results instead of internal variable names or exact implementation details.

16. What does pytest discover?  
Test modules and test functions/classes that follow its discovery rules; this project points pytest to tests/.

17. What does Ruff do?  
ruff check performs linting and ruff format --check verifies formatting.

18. Which CLI codes are used?  
0 for success and 2 for expected argument, input or data errors.

19. Why run tests, Ruff and wheel build in CI?  
They independently verify behavior, code quality and distributable packaging.

20. What must REPORT contain?  
CSV format, reference date, module structure, analysis, errors, full report text, tests, quality commands, wheel, CI, Issues/PR, version/tag and academic-integrity information.

## 16. Conclusion

LAB_06 starts the Python stage while retaining the previous Java project history and tests.

The Python package reads variant-20 Gym CSV data with csv.DictReader, converts fields to typed values, filters active memberships, aggregates visits by kind, collects unique customers, calculates average visits and selects the leader.

The application includes separate loader, analysis, reporting, error and CLI modules, pytest, Ruff, wheel packaging, version 1.5.0 and a three-OS Python CI workflow.
