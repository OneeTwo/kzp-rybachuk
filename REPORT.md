# Laboratory Work No. 1 Report

## 1. Topic, Number and Variant

**Topic:** Java Project Deployment and Basic Data Processing  
**Laboratory Work:** No. 1  
**Variant:** 20 — Gym  
**Operating System:** Windows 11  
**Version:** 1.0.0

**GitHub repository:**  
https://github.com/OneeTwo/kzp-rybachuk

**LAB_01 branch:**  
https://github.com/OneeTwo/kzp-rybachuk/tree/LAB_01

**Git tag v1.0.0:**  
https://github.com/OneeTwo/kzp-rybachuk/tree/v1.0.0

The repository link is included explicitly because the laboratory report must point to the GitHub repository containing the implementation.

## 2. Objective

The objective was to create the first reproducible version of a cross-platform Java console application for the Gym subject area.

The completed project:

- reads UTF-8 CSV input;
- validates each record without terminating on one invalid row;
- calculates four indicators for variant 20;
- prints the report to the console;
- writes the same report to an output file;
- supports command-line arguments, help and version output;
- builds with Maven Wrapper;
- runs JUnit 5 tests and SpotBugs;
- creates an executable JAR;
- verifies the project with GitHub Actions on Windows, Ubuntu and macOS.

## 3. Task

Variant 20 record format:

~~~text
client:String; plan:String; months:int; visits:int; price:double
~~~

Actual CSV rows use semicolons without type names:

~~~text
Іван Петренко;Standard;3;24;1500.00
~~~

Validation rules:

- client must not be blank;
- plan must not be blank;
- months must be greater than 0;
- visits must be greater than or equal to 0;
- price must be finite and greater than or equal to 0;
- a row must contain exactly five fields.

The program calculates four indicators:

1. number of valid records;
2. average number of visits;
3. total revenue;
4. longest membership duration.

## 4. Program Structure

Main files:

~~~text
src/main/java/ua/lpnu/kzp/Main.java
src/test/java/ua/lpnu/kzp/MainTest.java
data/input.csv
pom.xml
.github/workflows/ci.yml
README.md
REPORT.md
~~~

Data flow:

~~~text
command-line arguments
        ↓
Path input / Path output
        ↓
Files.readAllLines(..., UTF_8)
        ↓
validation of each CSV row
        ↓
statistics calculation
        ↓
Locale.ROOT report formatting
        ↓
console + UTF-8 output file
~~~

After review, the large processing block was split into clearer stages:

- main controls arguments and I/O;
- calculateStatistics validates rows and performs calculations;
- formatReport formats the final text;
- writeReport handles output-file creation;
- printHelp handles command usage.

This keeps file reading, calculations and formatting from being mixed in one large block.

## 5. Infrastructure

The project uses:

- Java 21;
- Maven;
- Maven Wrapper;
- JUnit 5;
- SpotBugs;
- Maven Shade Plugin;
- GitHub Actions.

Windows commands:

~~~powershell
.\mvnw.cmd test
.\mvnw.cmd verify
.\mvnw.cmd package
~~~

Linux/macOS commands:

~~~bash
./mvnw test
./mvnw verify
./mvnw package
~~~

Executable JAR:

~~~text
target/lab01-1.0.0.jar
~~~

Version command:

~~~powershell
java -jar target\lab01-1.0.0.jar --version
~~~

Expected output:

~~~text
lab01 1.0.0
~~~

GitHub Actions matrix:

~~~text
ubuntu-latest
windows-latest
macos-latest
~~~

Successful post-review LAB_01 CI run:

https://github.com/OneeTwo/kzp-rybachuk/actions/runs/37315128863

The JAR artifacts for Linux, Windows and macOS are attached to that successful workflow run.

## 6. GitHub Issues and Pull Requests

The work was organized with GitHub Issues and Pull Requests.

| Issue / PR | Purpose |
|---|---|
| Issue #1 | Initial LAB_01 setup |
| Issue #7 / #8 | Maven project and Wrapper |
| Issue #9 | Gym statistics and report generation |
| Issue #10 | CSV parsing and validation |
| Issue #11 | Command-line arguments |
| Issue #12 | JUnit tests and SpotBugs |
| Issue #13 | Cross-platform GitHub Actions |
| Issue #14 | Maven Wrapper correction |
| Issue #15 | Documentation and Javadoc |
| Issue #62 | Post-review corrections and additional tests |
| PR #2 | Initial LAB_01 setup |
| PR #3 | Cross-platform CI |
| PR #5 | Javadoc and REPORT review corrections |
| PR #16 | LAB_01 documentation merge |
| PR #17 | Final LAB_01 push |

Post-review issue:

https://github.com/OneeTwo/kzp-rybachuk/issues/62

Post-review Pull Request:

https://github.com/OneeTwo/kzp-rybachuk/pull/63

The review correction removes defects found after the original submission and expands the test suite.

## 7. Examples

Input file:

~~~text
Іван Петренко;Standard;3;24;1500.00
Марія Коваль;Premium;12;110;6500.00
Олег Бондар;Basic;1;8;700.00
Анна Мельник;Premium;-3;20;1800.00
Тарас Іванчук;Standard;6;abc;2800.00
~~~

Console output:

~~~text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
~~~

The file written to out/report.txt contains the same four report lines:

~~~text
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
~~~

Examples of validation messages:

~~~text
Line 2 skipped: empty line
Line 1 skipped: expected 5 fields
Line 1 skipped: client or plan is empty
Line 1 skipped: invalid number format
Line 1 skipped: invalid numeric value
~~~

## 8. Testing

The original version contained only two integration tests. After review, the suite was expanded to 12 tests.

The current tests verify:

1. exact statistics for several valid records;
2. empty input line;
3. incorrect number of fields;
4. blank client or plan;
5. invalid numeric format;
6. zero and negative numeric boundary values;
7. NaN and Infinity price values;
8. behavior when there are no valid records;
9. Ukrainian text and Ukrainian file names;
10. creation of a missing output directory;
11. both supported help forms;
12. version output.

Important edge cases from the assignment are therefore covered directly.

GitHub Actions confirmed: Tests run: 12, Failures: 0, Errors: 0, Skipped: 0. SpotBugs completed successfully on the review CI run.

Commands used for final verification:

~~~powershell
.\mvnw.cmd test
.\mvnw.cmd verify
.\mvnw.cmd package
~~~

The review CI completed successfully on Ubuntu, Windows and macOS.

## 9. Documentation

README.md documents:

- program purpose;
- repository and LAB_01 links;
- input format;
- validation rules;
- calculated indicators;
- build commands;
- run commands;
- help and version modes;
- example output;
- testing scope;
- CI information.

Javadoc is present for:

- public Main class;
- public main method;
- statistics calculation helper;
- report formatting helper;
- report writing helper;
- help helper.

The previous REPORT.md contained unresolved merge-conflict markers. They were removed during the post-review correction.

## 10. Academic Integrity

ChatGPT was used as an AI assistant.

Roles used during the work:

- requirements consultant;
- DevOps assistant;
- Validator;
- documentation assistant;
- Git/GitHub workflow assistant.

Example requests included:

~~~text
Explain the LAB_01 requirements for variant 20.
Check Maven and GitHub Actions configuration.
Suggest edge cases for JUnit tests.
Review REPORT.md against the required structure.
Help fix Maven Wrapper and SpotBugs problems.
~~~

Accepted recommendations included:

- explicit UTF-8;
- Path and Files APIs;
- Locale.ROOT for deterministic number formatting;
- Maven Wrapper;
- SpotBugs in verify;
- a three-OS GitHub Actions matrix;
- more edge-case tests.

Corrections made after review:

- the mandatory GitHub repository link was added to the report;
- unresolved merge-conflict text was removed from REPORT.md;
- a duplicated Files.writeString call was removed;
- reading/calculation/formatting responsibilities were separated more clearly;
- validation rejects non-finite prices;
- test coverage was expanded from 2 to 12 tests.

My contribution included configuring the repository, reviewing the implementation, running the program, checking output, executing Git/GitHub steps, reviewing AI suggestions and verifying the final result.

All submitted code and configuration were reviewed and understood before inclusion.

## 11. Control Questions

1. **What is the purpose of pom.xml?**  
   It defines the Maven project, Java version, dependencies, plugins and build settings.

2. **What is the difference between test, verify and package?**  
   test runs tests, verify also runs configured checks such as SpotBugs, and package creates the JAR.

3. **Why is Maven Wrapper needed?**  
   It lets the project use a defined Maven setup without requiring a global Maven installation.

4. **What is the role of main?**  
   It is the application entry point.

5. **How is a primitive type different from String?**  
   A primitive stores a simple value directly, while String is an object type.

6. **Why calculate an average using double?**  
   Integer division would lose the fractional part.

7. **What happens if Integer.parseInt receives abc?**  
   It throws NumberFormatException.

8. **Why must an invalid line not be skipped silently?**  
   The user must see which row was rejected and why.

9. **Why use split(";", -1)?**  
   The negative limit preserves empty trailing fields so they can be validated.

10. **Why use Path.of?**  
    It represents paths in a platform-independent way.

11. **Why specify StandardCharsets.UTF_8?**  
    It prevents platform-default encoding from changing the input or output.

12. **What is the difference between %n and \n?**  
    %n uses the platform line separator.

13. **What is validated in variant 20?**  
    Text fields must be nonblank; months must be positive; visits and price must be non-negative; price must also be finite.

14. **Which test can detect an incorrect average?**  
    A test with several records whose average has a fractional part.

15. **What is the purpose of SpotBugs?**  
    It finds likely defects through static analysis.

16. **What does GitHub Actions do here?**  
    It automatically runs project verification on three operating systems.

17. **What should a defect Issue contain?**  
    The problem, reproduction conditions, expected result and actual result.

18. **What belongs in the academic integrity section?**  
    The AI tool, its roles, example prompts, accepted recommendations, corrections and the student's contribution.

19. **What do the DevOps and Validator roles do?**  
    DevOps covers build/CI infrastructure; Validator checks requirements, boundaries and defects.

20. **Which part is most important to explain?**  
    The statistics calculation and validation loop, because it decides which records affect the final report.

## 12. Conclusion

Laboratory Work No. 1 produced a cross-platform Java console application for gym membership data.

The application reads UTF-8 CSV data, reports invalid rows, calculates all four indicators for variant 20 and writes one deterministic report to the console and file.

The project includes Maven Wrapper, JUnit 5, SpotBugs, executable JAR packaging, GitHub Issues, Pull Requests and three-OS GitHub Actions verification.

After review, the report was corrected with the mandatory GitHub link, leftover merge-conflict markers were removed, the duplicate output write was fixed, processing responsibilities were separated, numeric validation was strengthened and the test suite was expanded from 2 to 12 tests.

This version remains the basis for LAB_02, where raw CSV processing is replaced by domain classes without changing the external report behavior.
