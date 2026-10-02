# Laboratory Work No. 5 Report

## 1. Topic, Number and Variant

**Topic:** Parameterized Storage, Exceptions and CSV Export in a Java Project  
**Laboratory Work:** No. 5  
**Variant:** 20 — Gym  
**Operating System:** Windows 11

**Repository:**  
https://github.com/OneeTwo/kzp-rybachuk

**Branch:** `LAB_05`  
**Version:** `1.4.0`

The subject area remains the same as in the previous laboratory works: processing gym membership information.

For variant 20 the LAB_05 persistence model is:

```text
MembershipRecord:
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

## 2. Objective

The objective of LAB_05 was to complete data persistence in the existing Java project without creating a new repository or changing the subject area.

The following functionality was added:

- generic `Repository<T>`;
- custom checked `DataStorageException`;
- runtime `@CsvColumn` annotation;
- reflection-based generic CSV exporter;
- deterministic CSV column order;
- CSV escaping;
- UTF-8 reading and writing;
- CSV document parser;
- domain-specific `MembershipRecord` restoration;
- object → CSV → object round-trip;
- positive, boundary and negative tests.

The previous polymorphic model, Stream API queries, Maven configuration and external report output were preserved.

## 3. State Before and After LAB_05

### Before LAB_05

The application already contained:

```text
Membership
├── MonthlyMembership
└── AnnualMembership
```

It also contained:

```text
MembershipKind
MembershipQueries
```

and Stream API processing introduced in LAB_04.

Objects read from the original input file were stored directly in an in-memory list.

There was no universal persistence layer for:

```text
Java object -> CSV -> Java object
```

### After LAB_05

Direct collection storage in `Main` was replaced by:

```java
Repository<Membership>
```

LAB_05 additionally introduced:

```text
Repository<T>
DataStorageException
CsvColumn
CsvExporter
CsvParser
MembershipRecord
MembershipCsvReader
Lab05RoundTrip
```

A separate flat `MembershipRecord` model is used for persistence while the previous polymorphic membership hierarchy remains unchanged.

The application can now perform:

```text
MembershipRecord objects
        ↓
Repository<MembershipRecord>
        ↓
CsvExporter
        ↓
UTF-8 CSV
        ↓
CsvParser
        ↓
MembershipCsvReader
        ↓
restored MembershipRecord objects
        ↓
equals()
```

## 4. Repository<T>

The generic repository is declared as:

```java
public final class Repository<T>
```

The type parameter `T` determines the type of objects that can be stored.

For example:

```java
Repository<Membership> repository =
    new Repository<>();
```

accepts `Membership` objects.

The LAB_05 round-trip demonstration uses:

```java
Repository<MembershipRecord> repository =
    new Repository<>();
```

### add

```java
public void add(T item)
```

adds a non-null object to the repository.

### all

```java
public List<T> all()
```

returns:

```java
List.copyOf(items)
```

Therefore, the caller receives an immutable snapshot rather than access to the internal mutable collection.

A snapshot created before another call to `add()` also remains unchanged.

### find

```java
public List<T> find(
    Predicate<? super T> condition
)
```

uses the predicate to select matching records:

```java
return items.stream()
    .filter(condition)
    .toList();
```

`Predicate<? super T>` allows a predicate that accepts `T` or one of its supertypes.

Raw types are not used.

## 5. Data Model

LAB_05 introduces the flat persistence class:

```java
MembershipRecord
```

Fields:

| Field | Type | Meaning |
|---|---|---|
| `customer` | `String` | customer name |
| `kind` | `MembershipKind` | membership type |
| `startDate` | `LocalDate` | membership start |
| `endDate` | `LocalDate` | membership end |
| `visits` | `int` | visit count |

### Invariants

The implemented validation rules are:

```text
customer != null and not blank
kind != null
startDate != null
endDate != null
endDate >= startDate
visits >= 0
```

An invalid model value results in:

```java
IllegalArgumentException
```

or a null validation exception before the object is created.

### Equality

`equals()` compares:

```text
customer
kind
startDate
endDate
visits
```

`hashCode()` uses the same fields.

This is necessary because the round-trip test compares the complete original and restored object lists:

```java
assertEquals(original, restored);
```

## 6. @CsvColumn

LAB_05 defines:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvColumn {
    String value();
}
```

`RetentionPolicy.RUNTIME` is necessary because `CsvExporter` reads the annotation using reflection while the program is running.

`ElementType.FIELD` restricts its use to fields.

`MembershipRecord` contains:

```java
@CsvColumn("клієнт")
private final String customer;

@CsvColumn("тип")
private final MembershipKind kind;

@CsvColumn("початок")
private final LocalDate startDate;

@CsvColumn("кінець")
private final LocalDate endDate;

@CsvColumn("відвідування")
private final int visits;
```

The annotation value defines the text written to the CSV header.

### Deterministic Order

The exporter sorts fields by their Java names.

For `MembershipRecord`, the resulting order is:

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

## 7. Reflection-Based Exporter

The exporter is implemented in:

```text
CsvExporter
```

Its public method is generic:

```java
public static <T> void write(
    Path path,
    Class<T> type,
    List<? extends T> records
)
```

The exporter does not contain conditions such as:

```java
if (type == MembershipRecord.class)
```

and does not depend on:

```text
MonthlyMembership
AnnualMembership
MembershipRecord
```

### Reflection Algorithm

The exporter:

1. receives the class through `Class<T>`;
2. obtains declared fields;
3. selects fields containing `@CsvColumn`;
4. sorts them by Java field name;
5. verifies reflective access;
6. obtains CSV header names from the annotations;
7. obtains each field value using `Field.get`;
8. escapes CSV-special characters;
9. creates the CSV document;
10. writes it using UTF-8.

Reflection therefore allows the exporter to operate on another flat class containing annotated fields without changing the exporter implementation.

### Field Access

The implementation calls:

```java
field.trySetAccessible()
```

If access cannot be provided, export does not silently continue.

Instead:

```java
DataStorageException
```

is thrown.

## 8. CSV Format

The file uses:

```text
UTF-8
```

through:

```java
StandardCharsets.UTF_8
```

### Actual Header

```text
клієнт,кінець,тип,початок,відвідування
```

### Normal Record Example

```text
Ivan Petrenko,2026-10-31,MONTHLY,2026-10-01,12
```

### Escaped Record

One test record intentionally contains:

```text
Taras, "Strong"
Client
```

The exported CSV representation is:

```text
"Taras, ""Strong""
Client",2026-11-05,MONTHLY,2026-10-05,15
```

The comma is protected by the outer quotes.

The original double quotes are doubled:

```text
"
```

becomes:

```text
""
```

The line break remains inside the quoted field and therefore remains part of the customer value.

### Complete Demonstration CSV

The produced file logically contains:

```text
клієнт,кінець,тип,початок,відвідування
Ivan Petrenko,2026-10-31,MONTHLY,2026-10-01,12
Maria Koval,2026-12-31,ANNUAL,2026-01-01,105
Oleh Bondar,2026-09-30,MONTHLY,2026-09-01,8
Anna Melnyk,2027-01-31,ANNUAL,2026-02-01,80
"Taras, ""Strong""
Client",2026-11-05,MONTHLY,2026-10-05,15
```

There are five data records, but the multiline record occupies two physical text lines.

Therefore:

```text
logical data records = 5
physical CSV lines = 7
```

including the header.

## 9. Import and Round-Trip

Round-trip is the sequence:

```text
object
→ CSV
→ parser
→ object
```

The exporter is generic, but conversion from text to:

```text
LocalDate
MembershipKind
int
```

is domain-specific.

For this reason `MembershipRecord` implements:

```java
fromCsvFields(...)
```

### Import Field Order

Because the exporter uses alphabetical Java field order, the parser restores:

```text
0 -> customer
1 -> endDate
2 -> kind
3 -> startDate
4 -> visits
```

Conversions:

```java
LocalDate.parse(...)
MembershipKind.valueOf(...)
Integer.parseInt(...)
```

### Reading the File

`MembershipCsvReader` uses:

```java
Files.readString(
    path,
    StandardCharsets.UTF_8
)
```

The complete document is then passed to:

```java
CsvParser.parseDocument(text)
```

This is important because a physical line break may appear inside a quoted CSV field.

### Actual Round-Trip Result

The command:

```powershell
java -jar target\lab01-1.4.0.jar --csv-demo
```

produced:

```text
CSV file: out\memberships.csv
Original records: 5
Restored records: 5
Round-trip equal: true
```

Therefore:

```text
original.size() = 5
restored.size() = 5
original.equals(restored) = true
```

The values of the records, including the enum, dates, visit count and escaped customer string, survived the complete round-trip.

The variant round-trip key is:

```text
customer + startDate
```

## 10. Error Handling

LAB_05 distinguishes model, CSV format, CSV value, filesystem and reflection errors.

| Error type | Example | Result | Cause |
|---|---|---|---|
| Model validation | negative visits | `IllegalArgumentException` | model error itself |
| Field count | fewer than 5 fields | `DataStorageException` | none required |
| Number conversion | `visits = abc` | `DataStorageException` | `NumberFormatException` |
| Enum conversion | `kind = UNKNOWN` | `DataStorageException` | `IllegalArgumentException` |
| CSV syntax | unclosed quotes | `DataStorageException` | parser error |
| CSV header | unexpected header | `DataStorageException` | none required |
| File read | missing file | `DataStorageException` | `IOException` |
| File write | filesystem error | `DataStorageException` | `IOException` |
| Reflection | inaccessible field | `DataStorageException` | reflective/runtime cause where available |
| No annotated fields | exporting unsupported class | `DataStorageException` | none required |

Example conversion handling:

```java
try {
    int visits =
        Integer.parseInt(fields.get(4));
} catch (IllegalArgumentException exception) {
    throw new DataStorageException(
        "Invalid membership CSV values",
        exception
    );
}
```

The original cause remains available through:

```java
exception.getCause()
```

Errors are not replaced by an empty collection.

## 11. Testing

All tests from the previous laboratory works remain in the project.

LAB_05 adds tests for:

```text
Repository<T>
MembershipRecord
CsvExporter
CsvParser
Membership round-trip
```

### Repository Tests

Checked:

- adding values;
- retrieving stored records;
- immutable `all()` result;
- snapshot independence;
- predicate search;
- null item rejection;
- null predicate rejection.

### Annotation and Model Tests

Checked:

- valid `MembershipRecord`;
- field values;
- equality;
- hash code;
- negative visits;
- invalid date interval;
- presence of `@CsvColumn`;
- correct annotation values.

### Export Tests

Checked:

- deterministic header;
- normal record;
- comma escaping;
- quote escaping;
- multiline field;
- empty record list;
- class without CSV columns;
- UTF-8 output.

### Parser Tests

Checked:

- normal CSV;
- quoted comma;
- doubled quote;
- multiline quoted field;
- CRLF;
- empty CSV;
- unclosed quote;
- invalid characters after a closing quote;
- multiple records passed to the single-record method.

### Round-Trip Tests

Checked:

```text
5 original records
5 restored records
equal lists
```

The test data include:

```text
comma
quotes
line break
MONTHLY enum
ANNUAL enum
LocalDate values
integer visits
```

### Negative Tests

Checked:

```text
invalid field count
invalid number
invalid enum
missing file
```

`getCause()` is explicitly verified for conversion and filesystem failures.

### Empty Input

Two empty states are tested.

An exporter called with:

```java
List.of()
```

still creates a header because `MembershipRecord.class` is supplied separately.

An entirely empty CSV document returns:

```text
empty list
```

### Local Verification

The following commands were executed successfully:

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
.\mvnw.cmd package
```

The executable JAR also ran successfully.

## 12. Infrastructure

The project uses:

```text
Java 21
Maven
Maven Wrapper
JUnit 5
SpotBugs
Maven Shade Plugin
GitHub Actions
```

Current version:

```text
1.4.0
```

### Test

```powershell
.\mvnw.cmd test
```

### Static Analysis

```powershell
.\mvnw.cmd verify
```

This executes the tests and SpotBugs verification.

### Package

```powershell
.\mvnw.cmd package
```

Executable JAR:

```text
target/lab01-1.4.0.jar
```

### Normal Application

```powershell
java -jar target\lab01-1.4.0.jar
```

### Version

```powershell
java -jar target\lab01-1.4.0.jar --version
```

Actual result:

```text
lab01 1.4.0
```

### CSV Demonstration

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

### Continuous Integration

GitHub Actions is configured for:

```text
ubuntu-latest
windows-latest
macos-latest
```

The workflow uploads:

```text
target/lab01-1.4.0.jar
```

using an artifact name beginning with:

```text
lab05-
```

The final remote CI result will be recorded after the LAB_05 Pull Request is created and all three matrix jobs finish.

**Git tag:** `v1.4.0` will be created on `main` after the final Pull Request is merged.

## 13. GitHub Issues and Pull Request

LAB_05 was implemented in:

```text
LAB_05
```

The following Issues were created:

| Issue | Change | Verification |
|---|---|---|
| #52 | Implement LAB_05 generic repository | `RepositoryTest` |
| #53 | Implement LAB_05 CSV annotation | reflection/annotation tests |
| #54 | Implement LAB_05 CSV exporter | header and escaping tests |
| #55 | Implement LAB_05 CSV parser | parser and multiline tests |
| #56 | Implement LAB_05 storage exceptions | cause/error tests |
| #57 | Implement LAB_05 MembershipRecord round-trip | round-trip test |
| #58 | Add LAB_05 tests | `test` and `verify` |
| #59 | Update LAB_05 documentation | README and REPORT |
| #60 | Verify LAB_05 CI and release | JAR, CI, version and tag |

The final Pull Request will merge:

```text
LAB_05 -> main
```

and will contain:

```text
Closes #52
Closes #53
Closes #54
Closes #55
Closes #56
Closes #57
Closes #58
Closes #59
Closes #60
```

The Pull Request will also include:

- CSV example;
- local verification result;
- round-trip result;
- error handling summary;
- CI result.

## 14. Comparison with the Previous State

LAB_05 does not replace the existing subject domain.

The previous membership hierarchy remains available:

```text
Membership
├── MonthlyMembership
└── AnnualMembership
```

The Stream API query layer from LAB_04 also remains.

The primary storage change in `Main` is:

### Before

```java
List<Membership> memberships =
    new ArrayList<>();
```

### After

```java
Repository<Membership> repository =
    new Repository<>();
```

and after parsing:

```java
List<Membership> memberships =
    repository.all();
```

The external output before LAB_05 was:

```text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
```

After introducing `Repository<T>` and CSV functionality, the result remains:

```text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
```

Therefore, LAB_05 added persistence without losing the previous calculations, model or external behavior.

## 15. Academic Integrity

ChatGPT was used as a generative AI assistant during LAB_05.

### Tool

```text
ChatGPT
```

### Roles

The AI assistant was used as:

- requirements consultant;
- component design assistant;
- test-case reviewer;
- CSV validation assistant;
- DevOps assistant;
- documentation assistant;
- Git/GitHub workflow assistant.

### Example Requests

Examples of requests made to the assistant:

```text
Analyse LAB_05 for variant 20.
Create Repository<T>.
Implement @CsvColumn.
Implement a reflection-based CsvExporter.
Add proper CSV escaping.
Implement a CSV parser without split(",").
Create MembershipRecord for variant 20.
Implement object -> CSV -> object round-trip.
Add tests for invalid numbers, enum and file errors.
Prepare README and REPORT.
```

### Accepted Suggestions

Accepted suggestions included:

- using a generic `Repository<T>`;
- returning `List.copyOf()` from `all()`;
- using `Predicate<? super T>`;
- implementing a checked `DataStorageException`;
- preserving the original exception as `cause`;
- using `@Retention(RUNTIME)`;
- using `@Target(FIELD)`;
- sorting reflected fields by Java field name;
- using a domain-independent exporter;
- quoting CSV fields containing commas, quotes or line breaks;
- doubling embedded quotes;
- parsing the complete CSV document;
- keeping import conversion domain-specific;
- testing round-trip equality.

### Adapted Decisions

The existing LAB_03/LAB_04 polymorphic model uses:

```text
client
plan
months
visits
price
```

while LAB_05 variant 20 specifies:

```text
customer
kind
startDate
endDate
visits
```

The previous model was not removed or rewritten.

Instead, a separate flat:

```text
MembershipRecord
```

was introduced for persistence.

This preserves the previous product while also implementing the exact persistence fields required for LAB_05.

### Corrected Problems

During implementation, special attention was given to:

- avoiding `split(",")`;
- matching parser field order to reflection field order;
- retaining exception causes;
- preserving the original LAB_04 output;
- keeping the CSV exporter independent of the gym domain.

### My Contribution

My contribution included:

- creating and maintaining the `LAB_05` branch;
- creating Issues;
- integrating each LAB_05 class into the project;
- reviewing the code;
- running all tests;
- running SpotBugs;
- testing the executable JAR;
- running the CSV demonstration;
- checking round-trip equality;
- verifying the original application output;
- updating version and CI configuration;
- preparing and reviewing the documentation.

I reviewed the submitted implementation and understand the purpose of the generic repository, annotation, reflection, CSV escaping, parser, checked exception and round-trip process.

## 16. Control Questions

### 1. What problem does the type parameter solve in Repository<T>?

It makes the repository type-safe. The compiler knows what type of objects the repository stores, so casts and raw `Object` values are not required.

### 2. How is a parameterized class different from a raw type?

A parameterized type such as:

```java
Repository<MembershipRecord>
```

preserves compile-time type checking. A raw `Repository` loses part of this protection.

### 3. Why does find use Predicate<? super T>?

It allows the repository to accept a predicate for `T` or a compatible supertype of `T`, while still maintaining type safety.

### 4. Why should all() return an immutable snapshot?

The caller should not be able to modify the repository's internal collection directly. A snapshot also preserves its state even if the repository changes later.

### 5. What is a checked exception and when is it appropriate here?

A checked exception must be handled or declared by the caller. `DataStorageException` is suitable because export, import, filesystem and CSV processing are operations that can fail and the caller must decide how to handle the failure.

### 6. Why preserve the cause of a custom exception?

It keeps the original technical reason for the failure. For example, a high-level message can say that CSV reading failed while `getCause()` still contains the original `IOException`.

### 7. Why are Path and Files better than constructing paths as strings?

`Path` represents filesystem paths independently of the operating system, while `Files` provides standard operations for reading, writing and creating directories.

### 8. Why must encoding be specified for both writing and reading?

If different or platform-default encodings are used, characters can be corrupted. Using `StandardCharsets.UTF_8` on both sides makes the result deterministic.

### 9. What do @Retention(RUNTIME) and @Target(FIELD) do?

`RUNTIME` keeps the annotation available to reflection during execution. `FIELD` permits the annotation on fields.

### 10. Why does an annotation not perform export itself?

An annotation stores metadata only. Another component, in this project `CsvExporter`, reads that metadata and implements the actual behavior.

### 11. How does getDeclaredFields() differ from obtaining only public fields?

`getDeclaredFields()` returns fields declared directly by a class regardless of their access modifier. Public-field APIs do not provide the same access to private declared fields.

### 12. Why check trySetAccessible()?

The exporter must know whether it can actually read the private field. If access cannot be enabled, it should report an error rather than silently skip the value.

### 13. Why must CSV field order be deterministic?

Export and import must agree on the position of every value. Stable ordering also makes generated files reproducible on different runs and systems.

### 14. Why is split(",") unsuitable for general CSV?

A comma inside a quoted field is data rather than a separator. `split(",")` cannot distinguish between those cases.

### 15. How are commas, quotes and line breaks escaped in CSV?

If the field contains one of these characters, the complete field is enclosed in double quotes. A double quote inside the field is written twice.

### 16. Why does an empty list require Class<T>?

There is no first object from which the exporter could determine the class and its annotated fields. `Class<T>` allows the exporter to create the header even when there are zero records.

### 17. How does the generic exporter differ from domain-specific fromCsv logic?

The exporter only reads annotated fields through reflection and does not know their domain meaning. Import must know how to convert strings into types such as `LocalDate`, enum and `int`, so this part is domain-specific.

### 18. What error levels does the program distinguish?

The implementation distinguishes model validation, CSV syntax, CSV value conversion, filesystem errors and reflection errors.

### 19. What does the round-trip test prove?

It proves that an object can be exported to CSV and restored without changing the values used by `equals()`.

### 20. Why should an exception not be swallowed by an empty catch block?

The caller would lose information about the failure and could incorrectly treat an error as a successful empty result. Preserving or handling the exception keeps the program state understandable.

## 17. Conclusion

LAB_05 extended the existing gym application with a type-safe generic storage layer and CSV persistence.

`Repository<T>` separates collection storage from application logic.

`@CsvColumn` stores CSV metadata directly on the persistence model.

Reflection allows `CsvExporter` to operate without knowing the specific gym class in advance.

The CSV implementation correctly supports commas, quotes and multiline fields, and UTF-8 is explicitly used for both writing and reading.

`DataStorageException` adds application-level context while preserving the original cause.

The completed round-trip produced:

```text
Original records: 5
Restored records: 5
Round-trip equal: true
```

All previous report values were also preserved.

The generated CSV can now be used as the input text format for the next software block of the project.
