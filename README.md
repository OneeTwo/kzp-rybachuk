# Gym Membership Report — LAB_05

Laboratory Work No. 5 for the Cross-Platform Programming course.

**Variant:** 20 — Gym  
**Version:** 1.4.0

## Description

LAB_05 extends the existing gym membership project with:

- a generic `Repository<T>`;
- a custom checked exception;
- runtime CSV annotations;
- a reflection-based generic CSV exporter;
- a CSV parser with quoted and multiline field support;
- UTF-8 file persistence;
- CSV import for `MembershipRecord`;
- object → CSV → object round-trip verification.

The previous polymorphic model, Stream API queries, tests and external report output are preserved.

The goal of LAB_05 is to add persistence without creating a new repository or changing the project domain. :chatgpt-content-reference{index="0"}

## Variant 20

Domain:

```text
Gym
```

LAB_05 record:

```text
MembershipRecord
```

Fields:

```text
customer   : String
kind       : MembershipKind
startDate  : LocalDate
endDate    : LocalDate
visits     : int
```

CSV column names:

```text
клієнт
тип
початок
кінець
відвідування
```

Round-trip key:

```text
customer + startDate
```

These fields and the round-trip key correspond to variant 20. :chatgpt-content-reference{index="1"}

## Generic Repository

The application uses:

```java
Repository<T>
```

The repository provides:

```java
add(T item)
all()
find(Predicate<? super T> condition)
```

Example:

```java
Repository<Membership> repository =
    new Repository<>();

repository.add(membership);

List<Membership> memberships =
    repository.all();
```

`all()` returns an immutable snapshot, so external code cannot modify the repository's internal list.

The project does not use raw `Repository` types.

## DataStorageException

LAB_05 adds:

```java
DataStorageException
```

It supports:

```java
DataStorageException(String message)
```

and:

```java
DataStorageException(
    String message,
    Throwable cause
)
```

The original technical cause is preserved.

Examples include:

```text
IOException
NumberFormatException
IllegalArgumentException
reflection access errors
```

Errors are not silently replaced with empty results. :chatgpt-content-reference{index="2"}

## CsvColumn Annotation

CSV metadata is declared using:

```java
@CsvColumn
```

The annotation uses:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
```

Example:

```java
@CsvColumn("клієнт")
private final String customer;
```

`RUNTIME` allows reflection to read the annotation while the program is running.

`FIELD` restricts the annotation to fields.

## MembershipRecord

`MembershipRecord` is a flat persistence model used by LAB_05.

Example:

```java
new MembershipRecord(
    "Ivan Petrenko",
    MembershipKind.MONTHLY,
    LocalDate.of(2026, 10, 1),
    LocalDate.of(2026, 10, 31),
    12
);
```

Validation includes:

```text
customer must not be blank
kind must not be null
startDate must not be null
endDate must not be null
endDate must not be before startDate
visits must not be negative
```

`equals()` and `hashCode()` include all record fields so restored records can be compared with the original records.

## Deterministic CSV Column Order

The generic exporter sorts fields by their Java field names.

For `MembershipRecord`, the Java names are ordered as:

```text
customer
endDate
kind
startDate
visits
```

Therefore, the actual CSV header is:

```text
клієнт,кінець,тип,початок,відвідування
```

The displayed CSV column text comes only from `@CsvColumn`. The ordering comes from the Java field names. :chatgpt-content-reference{index="3"}

## Generic CsvExporter

`CsvExporter` is generic and does not contain conditions for:

```text
MembershipRecord
MonthlyMembership
AnnualMembership
```

Its public operation is:

```java
CsvExporter.write(
    path,
    type,
    records
);
```

The exporter:

1. gets fields using reflection;
2. keeps fields annotated with `@CsvColumn`;
3. sorts them by Java field name;
4. checks field accessibility;
5. creates the header from annotation values;
6. reads values from each object;
7. applies CSV escaping;
8. writes the result using UTF-8.

This allows the same exporter to work with another flat annotated class without modifying `CsvExporter`. :chatgpt-content-reference{index="4"}

## CSV Escaping

A value is surrounded by double quotes when it contains:

```text
comma
double quote
carriage return
line feed
```

A quote inside a quoted value is doubled.

Example source value:

```text
Taras, "Strong"
Client
```

CSV representation:

```text
"Taras, ""Strong""
Client"
```

Therefore, a normal `split(",")` is not sufficient for parsing the generated CSV. :chatgpt-content-reference{index="5"}

## CsvParser

`CsvParser` implements document-level CSV parsing.

It supports:

```text
simple fields
quoted commas
escaped quotes
Windows CRLF
Unix LF
multiline quoted fields
```

The complete document is parsed using:

```java
CsvParser.parseDocument(...)
```

Quoted commas and line breaks are treated as data instead of record separators.

## CSV Import

LAB_05 uses:

```java
MembershipCsvReader
```

to restore `MembershipRecord` objects.

The file is read using:

```java
Files.readString(
    path,
    StandardCharsets.UTF_8
);
```

The parser then reconstructs the document into fields.

`MembershipRecord.fromCsvFields(...)` converts:

```text
customer -> String
endDate  -> LocalDate
kind     -> MembershipKind
startDate -> LocalDate
visits   -> int
```

The generic exporter remains domain-independent, while the import conversion is domain-specific.

## Round-Trip

Round-trip means:

```text
Java objects
-> CSV
-> CSV parser
-> Java objects
-> equality comparison
```

The demonstration uses five `MembershipRecord` objects.

Command:

```powershell
java -jar target\lab01-1.4.0.jar --csv-demo
```

Actual result:

```text
CSV file: out\memberships.csv
Original records: 5
Restored records: 5
Round-trip equal: true
```

This confirms that all five exported objects are restored without changing their values.

Round-trip equality is a required part of LAB_05. :chatgpt-content-reference{index="6"}

## CSV Test Data

The demonstration contains five membership records.

One record intentionally contains:

```text
Taras, "Strong"
Client
```

This verifies:

- comma escaping;
- quote escaping;
- multiline field parsing.

The methodology requires at least five records and at least one record containing a comma, quote or line break. :chatgpt-content-reference{index="7"}

## Empty Input

The exporter can receive:

```java
List.of()
```

and still generate the CSV header because the record class is supplied separately:

```java
CsvExporter.write(
    path,
    MembershipRecord.class,
    List.of()
);
```

An entirely empty CSV document is restored as an empty collection.

## Error Handling

The implementation distinguishes several error levels:

```text
Model validation
CSV format
CSV value conversion
File system
Reflection
```

Examples:

```text
wrong number of fields
invalid integer
invalid enum
unclosed quote
missing file
class without annotated fields
```

Where an underlying technical exception exists, it is preserved as `cause`.

## Previous Project Compatibility

The previous gym report still produces:

```text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
```

LAB_05 changes the storage mechanism from a direct list to:

```java
Repository<Membership>
```

but preserves the previous report values and Stream API processing.

## Commands

Run all tests:

```powershell
.\mvnw.cmd test
```

Run tests and SpotBugs:

```powershell
.\mvnw.cmd verify
```

Create executable JAR:

```powershell
.\mvnw.cmd package
```

Run the original report:

```powershell
java -jar target\lab01-1.4.0.jar
```

Check version:

```powershell
java -jar target\lab01-1.4.0.jar --version
```

Expected:

```text
lab01 1.4.0
```

Run LAB_05 CSV demonstration:

```powershell
java -jar target\lab01-1.4.0.jar --csv-demo
```

Expected:

```text
CSV file: out\memberships.csv
Original records: 5
Restored records: 5
Round-trip equal: true
```

## Project Structure

```text
src/main/java/ua/lpnu/kzp/
├── Main.java
├── Membership.java
├── MonthlyMembership.java
├── AnnualMembership.java
├── MembershipKind.java
├── MembershipQueries.java
├── MembershipRecord.java
├── MembershipCsvReader.java
├── Repository.java
├── CsvColumn.java
├── CsvExporter.java
├── CsvParser.java
├── DataStorageException.java
├── Lab05RoundTrip.java
└── VisitsPrice.java
```

New LAB_05 tests include:

```text
RepositoryTest
MembershipRecordTest
CsvExporterTest
CsvParserTest
MembershipRoundTripTest
```

All previous tests remain in the project.

## Technologies

- Java 21
- Java Generics
- Reflection API
- Custom annotations
- Path and Files
- UTF-8
- Java Stream API
- JUnit 5
- Maven
- Maven Wrapper
- SpotBugs
- Maven Shade Plugin
- GitHub Actions

## Continuous Integration

GitHub Actions is configured for:

```text
Ubuntu
Windows
macOS
```

The LAB_05 workflow runs Maven verification and uploads:

```text
target/lab01-1.4.0.jar
```

as an artifact named:

```text
lab05-<OS>-<run number>
```

Final CI status will be verified on the LAB_05 Pull Request.

## Repository

```text
https://github.com/OneeTwo/kzp-rybachuk
```
