# Gym Membership Report — LAB_01

Laboratory Work No. 1 for the Cross-Platform Programming course.

**Variant:** 20 — Gym  
**Version:** 1.0.0

## Repository

https://github.com/OneeTwo/kzp-rybachuk

LAB_01 branch:

https://github.com/OneeTwo/kzp-rybachuk/tree/LAB_01

## Description

The application reads gym membership records from a UTF-8 CSV file, validates every line, calculates the required statistics, prints the report to the console, and writes the same report to a file.

## Input Format

Each line contains five semicolon-separated fields:

~~~text
client;plan;months;visits;price
~~~

Example:

~~~text
Іван Петренко;Standard;3;24;1500.00
Марія Коваль;Premium;12;110;6500.00
Олег Бондар;Basic;1;8;700.00
Анна Мельник;Premium;-3;20;1800.00
Тарас Іванчук;Standard;6;abc;2800.00
~~~

Validation rules:

- client must not be blank;
- plan must not be blank;
- months must be greater than 0;
- visits must be non-negative;
- price must be finite and non-negative;
- every row must contain exactly five fields.

Invalid rows are skipped with their line number and reason.

## Calculated Statistics

The program calculates four indicators required for variant 20:

1. number of valid records;
2. average number of visits;
3. total revenue;
4. longest membership duration.

## Build and Verify

Windows:

~~~powershell
.\mvnw.cmd test
.\mvnw.cmd verify
.\mvnw.cmd package
~~~

Linux / macOS:

~~~bash
./mvnw test
./mvnw verify
./mvnw package
~~~

## Run

Default input and output:

~~~powershell
java -jar target\lab01-1.0.0.jar
~~~

Custom input and output:

~~~powershell
java -jar target\lab01-1.0.0.jar --input data\input.csv --output out\report.txt
~~~

Help:

~~~powershell
java -jar target\lab01-1.0.0.jar --help
~~~

The program also accepts the form required in the assignment:

~~~powershell
java -jar target\lab01-1.0.0.jar -help
~~~

Version:

~~~powershell
java -jar target\lab01-1.0.0.jar --version
~~~

Expected result:

~~~text
lab01 1.0.0
~~~

## Example Output

~~~text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
~~~

## Testing

JUnit 5 tests cover:

- exact statistics for several valid records;
- empty input line;
- incorrect number of fields;
- blank client or plan;
- invalid numeric format;
- zero and negative numeric boundaries;
- NaN and Infinity price values;
- input with no valid records;
- Ukrainian text and Ukrainian file names;
- automatic creation of the output directory;
- both help forms;
- version output.

The LAB_01 test suite contains 12 tests.

## Project Structure

~~~text
src/main/java/ua/lpnu/kzp/Main.java
src/test/java/ua/lpnu/kzp/MainTest.java
data/input.csv
pom.xml
.github/workflows/ci.yml
README.md
REPORT.md
~~~

## Technologies

- Java 21
- Maven
- Maven Wrapper
- JUnit 5
- SpotBugs
- Maven Shade Plugin
- GitHub Actions

## Continuous Integration

GitHub Actions runs Maven verify on:

- Ubuntu;
- Windows;
- macOS.

The executable JAR is uploaded as a workflow artifact.

Successful LAB_01 CI run:

https://github.com/OneeTwo/kzp-rybachuk/actions/runs/37315128863

## Review Corrections

The post-review correction is tracked in:

https://github.com/OneeTwo/kzp-rybachuk/issues/62

Review Pull Request:

https://github.com/OneeTwo/kzp-rybachuk/pull/63

The corrections remove leftover merge-conflict text, remove a duplicate file write, separate processing stages more clearly, and expand the test suite.
