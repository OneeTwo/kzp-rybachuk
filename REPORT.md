# Laboratory Work No. 2 Report

## 1. Topic and Variant

**Topic:** Classes, Record and Encapsulation in a Java Project  
**Laboratory Work:** No. 2  
**Variant:** 20 — Gym  
**Operating System:** Windows 11

**Repository:**  
https://github.com/OneeTwo/kzp-rybachuk

## 2. Objective

The objective of this laboratory work was to refactor the program from LAB_01 by replacing direct processing of CSV fields with domain objects.

The program now uses:

- a `Membership` entity class;
- private final fields;
- constructor validation;
- a static `fromCsv` factory method;
- a `VisitsPrice` record;
- JUnit 5 tests for valid and invalid data.

The external behavior of the program remains compatible with LAB_01.

## 3. State Before and After Refactoring

### Before LAB_02

In LAB_01, every CSV line was split directly inside `Main.java`.

The program manually extracted:

```text
client
plan
months
visits
price
```

Validation and numeric conversion were also performed directly in the processing loop.

Statistics were stored in separate variables:

```text
validCount
totalVisits
totalRevenue
maxMonths
```

### After LAB_02

CSV data is now converted into `Membership` objects.

```java
Membership membership = Membership.fromCsv(line);
```

Valid objects are stored in:

```java
List<Membership>
```

Validation rules are located inside the `Membership` class instead of being duplicated in `Main`.

A `VisitsPrice` record is used as an immutable helper value during statistics calculation.

The report format and calculated statistics remain unchanged.

## 4. Membership Entity

The main domain class is:

```text
Membership
```

It contains the following private final fields:

```java
private final String client;
private final String plan;
private final int months;
private final int visits;
private final double price;
```

The object is immutable because its fields are `final` and no setters are provided.

### Validation

The constructor checks the following conditions:

- `client` must not be null;
- `client` must not be blank;
- `plan` must not be null;
- `plan` must not be blank;
- `months` must be greater than 0;
- `visits` must be greater than or equal to 0;
- `price` must be finite and greater than or equal to 0.

Example:

```java
Membership membership =
        new Membership("Ivan", "Standard", 3, 24, 1500.0);
```

Invalid objects cannot be created because validation is performed inside the constructor.

The class also provides getter methods for reading its values.

The `toString()` method uses `Locale.ROOT` so that numeric formatting does not depend on the operating system locale.

## 5. Factory Method and Record

### fromCsv

The static factory method:

```java
Membership.fromCsv(line)
```

creates a `Membership` object from one CSV line.

Expected format:

```text
client;plan;months;visits;price
```

Example:

```text
Ivan;Standard;3;24;1500.00
```

The method:

1. checks that the line is not null;
2. splits the line into fields;
3. verifies that exactly five fields exist;
4. converts numeric values;
5. creates a validated `Membership` object.

If a number cannot be parsed, an `IllegalArgumentException` is generated.

### VisitsPrice Record

The project also contains:

```java
public record VisitsPrice(int visits, double price)
```

The record stores two immutable values:

- number of visits;
- membership price.

It validates that:

- visits are non-negative;
- price is finite and non-negative.

A Java record automatically provides component access methods, `equals()`, `hashCode()` and `toString()`.

## 6. Testing

The previous LAB_01 tests were preserved.

A new test class was added:

```text
MembershipTest.java
```

The new tests check:

- creation of a valid `Membership`;
- getters;
- null client;
- empty client;
- empty plan;
- zero months;
- negative visits;
- negative price;
- correct `fromCsv`;
- incorrect number of CSV fields;
- invalid numeric format;
- `VisitsPrice` value equality;
- invalid values in `VisitsPrice`.

Tests were executed with:

```powershell
.\mvnw.cmd test
```

Result:

```text
BUILD SUCCESS
```

The previous tests also continue to pass after the refactoring.

## 7. Infrastructure

The infrastructure from LAB_01 was preserved:

- Java 21;
- Maven;
- Maven Wrapper;
- JUnit 5;
- SpotBugs;
- Maven packaging;
- GitHub Actions.

The following commands are used:

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
.\mvnw.cmd package
```

The packaged application was also started successfully:

```powershell
java -jar target\lab01-1.0.0.jar
```

Program output:

```text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
```

The JAR therefore preserves the behavior of the previous laboratory work.

GitHub Actions is configured to run the project checks on:

- Windows;
- Ubuntu;
- macOS.

Final CI verification is performed on the LAB_02 Pull Request.

## 8. GitHub Issues and Pull Request

The work was performed in the separate branch:

```text
LAB_02
```

The following GitHub Issues were created:

| Issue | Change | Verification |
|---|---|---|
| Implement Membership entity | Added `Membership.java` | Constructor and getter tests |
| Add Membership validation and CSV parsing | Added validation and `fromCsv()` | Invalid and valid CSV tests |
| Implement VisitsPrice record | Added `VisitsPrice.java` | Record equality and validation tests |
| Add LAB_02 tests | Added `MembershipTest.java` | Maven test |
| Update LAB_02 documentation | Updated README and REPORT | Documentation review |

The final Pull Request merges:

```text
LAB_02 → main
```

The Pull Request is created after the implementation and documentation are completed.

## 9. Behavior Comparison

The purpose of the refactoring was to change the internal structure without changing the external result.

### LAB_01 result

```text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
```

### LAB_02 result

```text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
```

The results are identical.

Therefore, the internal model was refactored successfully without changing the external behavior of the application.

## 10. Academic Integrity

ChatGPT was used as an AI assistant during this laboratory work.

### AI contribution

ChatGPT was used for:

- clarification of LAB_02 requirements;
- planning the refactoring;
- recommendations for the `Membership` entity;
- recommendations for constructor validation;
- implementation guidance for `fromCsv`;
- recommendations for the `VisitsPrice` record;
- preparation of JUnit test cases;
- Git and GitHub workflow guidance;
- documentation structure;
- debugging and verification recommendations.

### Accepted recommendations

The following recommendations were used:

- creating a separate `Membership` class;
- using private final fields;
- moving validation into the constructor;
- using `fromCsv` for parsing;
- creating the `VisitsPrice` record;
- adding tests for positive and negative cases;
- preserving the external report format.

### Corrected or reviewed recommendations

All generated code was reviewed before use.

The validation of `months` was adjusted to preserve the behavior of LAB_01, where zero months is considered invalid.

The existing program output was also compared before and after the refactoring.

### My contribution

My contribution included:

- creating and managing the Git branch;
- creating GitHub Issues;
- adding the source files to the project;
- reviewing and integrating the code;
- running Maven tests;
- running the packaged JAR;
- checking program output;
- committing and pushing the implementation;
- reviewing the documentation.

All submitted code was reviewed and understood before being included in the project.

## 11. Control Questions

1. **What problem does an entity class solve compared with strings and parallel variables?**  
   It groups related data and validation rules into one object instead of spreading them across the program.

2. **What do private and final mean for class fields?**  
   `private` prevents direct access from outside the class, while `final` prevents reassignment after construction.

3. **What is encapsulation?**  
   Encapsulation hides the internal state of an object and allows access only through controlled methods.

4. **Why are invariants checked in the constructor?**  
   It prevents the creation of an object with an invalid state.

5. **What is the difference between Objects.requireNonNull and isBlank?**  
   `requireNonNull` checks whether the reference is null, while `isBlank` checks whether a string is empty or contains only whitespace.

6. **Why can the absence of setters help preserve a valid state?**  
   Values cannot be replaced with unchecked values after the object has been created.

7. **What does a record automatically provide?**  
   A constructor, component access methods, `equals`, `hashCode` and `toString`.

8. **How is a record different from a normal entity class?**  
   A record is mainly intended as a compact immutable data carrier, while a normal class gives more control over behavior and internal structure.

9. **Why does a record not guarantee deep immutability?**  
   If a record contains a mutable object such as a list, the contents of that object can still be changed.

10. **Why is the static fromCsv method needed?**  
    It provides one clear place for converting a CSV line into a domain object.

11. **Where should an error from parsing one line be handled?**  
    In the loop that processes the input lines so one invalid line does not stop the whole program.

12. **Why must an invalid record not be added before validation finishes?**  
    Otherwise invalid values could affect the statistics.

13. **What is the purpose of toString and why does it not replace CSV formatting?**  
    `toString` gives a readable representation of an object, while CSV has a specific data-exchange format.

14. **Why is Locale.ROOT used?**  
    It keeps numeric formatting consistent on different operating systems and locales.

15. **What does AAA mean in testing?**  
    Arrange, Act, Assert: prepare the data, perform the operation, and verify the result.

16. **Which negative cases should be tested for the constructor?**  
    Null or blank text, invalid numeric boundaries, negative values, NaN and infinite values where applicable.

17. **How can we prove that refactoring did not change external behavior?**  
    By running the same input before and after the refactoring and comparing the output.

18. **Why must the previous tests continue to pass?**  
    They verify that functionality implemented in LAB_01 was not broken by the refactoring.

19. **Which infrastructure files are reused from LAB_01?**  
    `pom.xml`, Maven Wrapper files, GitHub Actions configuration, project structure and existing tests.

20. **Which line best demonstrates encapsulation in this implementation?**  
    A declaration such as `private final String client;` because the field cannot be accessed directly from outside the class and cannot be reassigned after construction.

## 12. Conclusion

In Laboratory Work No. 2, the gym membership application from LAB_01 was refactored using object-oriented programming principles.

A `Membership` entity with private final fields and constructor validation was created. CSV parsing was moved into the `fromCsv` factory method, and the immutable `VisitsPrice` record was added.

New JUnit tests verify correct and incorrect object creation, parsing, validation and record behavior.

The original program output remained unchanged after the refactoring, confirming that the internal implementation was improved without changing external behavior.

This project state provides the basis for Laboratory Work No. 3, where the domain objects can be extended into a type hierarchy and processed polymorphically.
