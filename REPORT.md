# Laboratory Work No. 4 Report

## 1. Topic, Number and Variant

**Topic:** Stream Data Processing in a Java Project  
**Laboratory Work:** No. 4  
**Variant:** 20 — Gym  
**Domain:** Gym Membership Management  
**Operating System:** Windows 11

**Repository:**  
https://github.com/OneeTwo/kzp-rybachuk

**Version:** 1.3.0  
**Git tag:** v1.3.0 — to be created after the final Pull Request is merged.

## 2. Objective

The objective of LAB_04 was to replace the main manual collection-processing calculations from the previous project state with declarative Java Stream API pipelines while preserving the external behavior of the application.

The implementation now provides:

- filtering through `filter`;
- transformation through `map`;
- grouping through `Collectors.groupingBy`;
- summary statistics through `Collectors.summarizingInt`;
- top-N processing through `sorted` and `limit`;
- a compound comparator using `reversed` and `thenComparing`;
- search through `Optional`;
- Stream API calculations in the main report;
- tests for normal, boundary and empty cases.

The previous polymorphic model, CSV input format and report output remain compatible with LAB_03.

## 3. State Before and After LAB_04

### Before LAB_04

LAB_03 already contained the polymorphic hierarchy:

```text
Membership
├── MonthlyMembership
└── AnnualMembership
```

Statistics in `Main.java` were calculated by manually iterating through the collection.

For example, the previous implementation accumulated:

```text
totalVisits
totalRevenue
maxMonths
```

inside a `for` loop.

### After LAB_04

Collection calculations are expressed with Stream API operations.

Examples:

```java
memberships.stream()
    .mapToDouble(Membership::getPrice)
    .sum();
```

and:

```java
memberships.stream()
    .mapToInt(Membership::getMonths)
    .max()
    .orElse(0);
```

A new class:

```text
MembershipQueries
```

contains separate named methods for the five required queries and Optional search.

The input parsing loop remains imperative because it performs sequential file-line parsing, validation and error reporting. The domain collection queries and report calculations are the parts refactored to Stream API.

## 4. Data Contract

The previous LAB_03 model is preserved.

### Base Type

```java
Membership
```

is the abstract common type.

### Subtypes

```text
MonthlyMembership
AnnualMembership
```

### Enum

```java
MembershipKind
```

contains:

```text
MONTHLY
ANNUAL
```

### Fields

The existing membership model contains:

```text
client
plan
months
visits
price
kind
```

### Validation Rules

The main common rules are:

- client must not be null or blank;
- plan must not be null or blank;
- months must be greater than zero;
- visits must not be negative;
- price must be finite and non-negative;
- membership kind must not be null.

`MonthlyMembership` requires:

```text
months < 12
```

`AnnualMembership` requires:

```text
months >= 12
```

### Input Format

The existing CSV contract remains unchanged:

```text
client;plan;months;visits;price
```

The LAB_04 variant table describes active memberships using start and end dates, but changing the model to those fields would break the data contract inherited from previous laboratories.

Therefore, the previous model is preserved.

For this project, an active membership is defined as a membership with at least one recorded visit:

```text
visits > 0
```

This rule is used consistently in implementation and tests.

## 5. Five Stream API Queries

All queries are located in:

```text
MembershipQueries.java
```

### Query 1 — Filter Active Memberships

Method:

```java
activeMemberships(...)
```

Source:

```text
List<Membership>
```

Pipeline:

```text
memberships
-> stream()
-> filter(visits > 0)
-> toList()
```

Implementation:

```java
return memberships.stream()
    .filter(membership -> membership.getVisits() > 0)
    .toList();
```

Result type:

```text
List<Membership>
```

For the six-record test dataset, five memberships are active.

### Query 2 — Map Client Names

Method:

```java
clientNames(...)
```

Pipeline:

```text
memberships
-> stream()
-> map(client)
-> toList()
```

Implementation:

```java
return memberships.stream()
    .map(Membership::getClient)
    .toList();
```

Result type:

```text
List<String>
```

Actual result:

```text
[Ivan, Maria, Oleh, Anna, Taras, Ivan]
```

### Query 3 — Group Visits by Membership Kind

Method:

```java
visitsByKind(...)
```

Pipeline:

```text
memberships
-> stream()
-> groupingBy(kind)
-> summingInt(visits)
-> map
```

Implementation uses:

```java
Collectors.groupingBy(
    Membership::getKind,
    () -> new EnumMap<>(MembershipKind.class),
    Collectors.summingInt(Membership::getVisits)
)
```

Result type:

```text
Map<MembershipKind, Integer>
```

Actual result:

```text
MONTHLY = 47
ANNUAL = 134
```

An `EnumMap` is used because `MembershipKind` is an enum and the keys have a fixed domain.

### Query 4 — Visit Statistics

Method:

```java
visitStatistics(...)
```

Implementation:

```java
return memberships.stream()
    .collect(Collectors.summarizingInt(
        Membership::getVisits
    ));
```

Result type:

```text
IntSummaryStatistics
```

Actual statistics for the six-record test collection:

```text
count = 6
sum = 181
min = 0
max = 110
average = 30.1667
```

### Query 5 — Top-N Memberships

Method:

```java
topMemberships(...)
```

The main criterion is the number of visits in descending order.

The second criterion is the client name in ascending order.

Comparator:

```java
Comparator.comparingInt(Membership::getVisits)
    .reversed()
    .thenComparing(Membership::getClient);
```

Pipeline:

```text
memberships
-> stream()
-> sorted(comparator)
-> limit(N)
-> toList()
```

For `N = 5`, the actual order is:

```text
1. Maria — 110 visits
2. Anna — 24 visits
3. Ivan — 24 visits
4. Ivan — 15 visits
5. Oleh — 8 visits
```

The sixth record:

```text
Taras — 0 visits
```

is removed by `limit(5)`.

The two records with 24 visits verify the second comparator criterion:

```text
Anna
Ivan
```

because client names are sorted in ascending order when visit counts are equal.

Rules for N:

```text
N < 0   -> IllegalArgumentException
N = 0   -> empty result
N > size -> all available elements
```

## 6. Grouping and Statistics

Grouping uses:

```java
MembershipKind
```

as the key.

The downstream collector:

```java
Collectors.summingInt(Membership::getVisits)
```

calculates the total number of visits for every membership kind.

Actual grouped result:

| Membership Kind | Total Visits |
|---|---:|
| MONTHLY | 47 |
| ANNUAL | 134 |

Visit statistics are calculated using:

```java
Collectors.summarizingInt(Membership::getVisits)
```

The result provides count, sum, minimum, maximum and average in one object.

For an empty collection:

```text
count = 0
sum = 0
```

The empty case is tested explicitly.

Numbers printed by the application continue to use:

```java
Locale.ROOT
```

to provide stable formatting independently of the operating system locale.

## 7. Compound Comparator

The comparator used by the top-N query is:

```java
Comparator<Membership> comparator =
    Comparator.comparingInt(Membership::getVisits)
        .reversed()
        .thenComparing(Membership::getClient);
```

The first criterion is:

```text
visits — descending
```

The second criterion is:

```text
client — ascending
```

`reversed()` is applied only to the visits comparator before `thenComparing`.

This preserves the required rule:

```text
visits descending,
client ascending
```

After sorting, the query executes:

```java
.limit(n)
```

Therefore, the collection is sorted before truncation.

This ensures that top-N means the N best elements of the complete input collection.

## 8. Optional Search

The search method is:

```java
findByClient(...)
```

Search key:

```text
client
```

Implementation:

```java
return memberships.stream()
    .filter(membership ->
        membership.getClient().equals(client))
    .findFirst();
```

Result type:

```text
Optional<Membership>
```

### Existing Result

The dataset contains two records with client:

```text
Ivan
```

`findFirst()` returns the first one in encounter order.

Actual first result:

```text
client = Ivan
plan = Standard
visits = 24
```

### Missing Result

Searching for:

```text
Unknown
```

returns:

```text
Optional.empty()
```

The method never returns `null`.

## 9. Equivalence: Loop → Stream API → Result

| Previous implementation | Stream API implementation | Result |
|---|---|---|
| manual visit accumulation and division | `visitStatistics().getAverage()` | `47.33` |
| `totalRevenue += price` | `mapToDouble(Membership::getPrice).sum()` | `8700.00` |
| `Math.max(maxMonths, months)` | `mapToInt(Membership::getMonths).max()` | `12` |
| loop over `costPerVisit()` | `mapToDouble(...).anyMatch(...)` | no invalid values |
| manual conditional selection | `filter(...)` | 5 active records in query dataset |
| manual extraction | `map(...)` | client-name list |
| manually updated map | `groupingBy(..., summingInt(...))` | MONTHLY 47, ANNUAL 134 |
| manual statistical variables | `summarizingInt(...)` | count 6, sum 181, min 0, max 110 |
| manual sorting/selection | `sorted(...).limit(5)` | correct top-5 |

### LAB_03 Output

```text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
```

### LAB_04 Output

```text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
```

The external results are identical.

Therefore, changing the implementation from manual accumulation to Stream API did not change the observable behavior of the application.

## 10. Testing

The previous tests from LAB_01, LAB_02 and LAB_03 remain in the project.

A new test class was added:

```text
MembershipQueriesTest.java
```

The new tests cover:

- filter query;
- filter boundary with zero visits;
- map query;
- grouping result;
- summary statistics;
- top-5;
- comparator tie-breaking;
- `N = 0`;
- N greater than collection size;
- negative N;
- duplicated client key;
- Optional existing result;
- Optional missing result;
- empty collection;
- absence of input collection mutation.

### Six-Record Dataset

The Stream tests contain six records:

```text
Ivan  — 24 visits
Maria — 110 visits
Oleh  — 8 visits
Anna  — 24 visits
Taras — 0 visits
Ivan  — 15 visits
```

This dataset verifies:

- a duplicated search key (`Ivan`);
- filter boundary (`Taras`, 0 visits);
- comparator tie (`Anna` and `Ivan`, both 24);
- top-5 truncation;
- different membership kinds.

### Local Commands

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
```

Result:

```text
BUILD SUCCESS
```

SpotBugs also completes successfully.

### Input Immutability

A dedicated test creates a copy of the original collection, executes all queries and then checks:

```java
assertEquals(original, source);
```

Therefore, the Stream API queries do not mutate the source collection.

## 11. Infrastructure

The existing infrastructure remains:

- Java 21;
- Maven;
- Maven Wrapper;
- JUnit 5;
- SpotBugs;
- Maven Shade Plugin;
- GitHub Actions.

Project version:

```text
1.3.0
```

Commands:

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
.\mvnw.cmd package
```

Executable JAR:

```powershell
java -jar target\lab01-1.3.0.jar
```

Version check:

```powershell
java -jar target\lab01-1.3.0.jar --version
```

Result:

```text
lab01 1.3.0
```

GitHub Actions is configured to execute verification on:

```text
Ubuntu
Windows
macOS
```

The workflow uploads:

```text
target/lab01-1.3.0.jar
```

as a `lab04` artifact.

**Final CI:** to be added after the LAB_04 Pull Request finishes successfully.  
**JAR artifact:** available from the final LAB_04 Actions run.  
**Git tag:** v1.3.0 after the final Pull Request is merged.

## 12. GitHub Issues and Pull Request

LAB_04 was developed in the branch:

```text
LAB_04
```

The following Issues were used:

| Issue | Change | Verification |
|---|---|---|
| #33 | Implement LAB_04 active membership filter | filter tests and boundary case |
| #34 | Implement LAB_04 client mapping | map result test |
| #35 | Implement LAB_04 visits grouping | grouping result test |
| #36 | Implement LAB_04 visit statistics | summary-statistics test |
| #37 | Implement LAB_04 top memberships query | top-N and comparator tests |
| #38 | Implement LAB_04 Optional search | existing and missing search tests |
| #39 | Add LAB_04 stream query tests | Maven test and verify |
| #40 | Update LAB_04 documentation | README, REPORT and Javadoc |
| #41 | Verify LAB_04 CI and release | verify, JAR, CI, version and tag |

Duplicate Issues #42–#50 were accidentally created during command-line Issue creation and were closed as duplicates of #33–#41.

The final Pull Request will merge:

```text
LAB_04 -> main
```

The Pull Request will contain:

```text
Closes #33
Closes #34
Closes #35
Closes #36
Closes #37
Closes #38
Closes #39
Closes #40
Closes #41
```

## 13. Comparison of Implementations

### Loop-Based Implementation

The previous implementation explicitly described every processing step:

```text
create accumulator
iterate
check condition
update accumulator
continue
```

This style is straightforward for simple algorithms but combines traversal and calculation in one block.

### Stream-Based Implementation

The Stream API version describes the requested result:

```text
source
-> filter/map
-> grouping/statistics/sorting
-> result
```

For example:

```java
memberships.stream()
    .mapToDouble(Membership::getPrice)
    .sum();
```

directly describes the operation “sum all membership prices”.

Similarly:

```java
memberships.stream()
    .sorted(comparator)
    .limit(n)
    .toList();
```

directly describes the top-N operation.

### Readability

Stream API improved readability for:

- filtering;
- transformation;
- grouping;
- aggregation;
- sorting;
- Optional search.

However, readability should not be evaluated only by counting lines of code.

A shorter stream pipeline can still be difficult to understand if:

- too many unrelated operations are placed in one chain;
- ordering is incorrect;
- side effects are introduced;
- comparator rules are unclear.

For this reason, the LAB_04 queries are implemented as separate named methods.

## 14. Academic Integrity

ChatGPT was used as a generative AI assistant during LAB_04.

### AI Roles

The assistant was used as:

- requirements consultant;
- development assistant;
- test-case reviewer;
- validator of boundary cases;
- DevOps assistant;
- documentation assistant;
- Git/GitHub workflow assistant.

### Example Requests

The requests included:

- analysis of LAB_04 requirements;
- identification of the requirements for variant 20;
- design of Stream API methods;
- preparation of query tests;
- verification of top-N boundary conditions;
- comparison of loop and Stream API implementations;
- Maven and JAR verification guidance;
- README and REPORT preparation.

### Accepted Recommendations

The following recommendations were accepted:

- creating `MembershipQueries`;
- keeping every query in a separate named method;
- using `filter` for active memberships;
- using `map` for client names;
- using `groupingBy` with `summingInt`;
- using `summarizingInt`;
- using `reversed().thenComparing(...)`;
- using `Optional` and `findFirst`;
- testing negative, zero and oversized N;
- testing an empty collection;
- testing a duplicated client;
- checking that the input collection is not modified.

### Rejected or Adapted Recommendations

The LAB_04 variant description contains start and end date fields for gym memberships.

Changing the existing LAB_03 model to these fields was rejected because the laboratory methodology also requires preserving the previous domain model and input format.

Instead, the existing model was retained and the active-membership rule was adapted to:

```text
visits > 0
```

This adaptation is explicitly documented in the report and tested.

The application report was also not expanded with the five query results because this would unnecessarily change the external behavior preserved from previous laboratory works. Query results are verified by tests and documented separately.

### Corrected Issues

Duplicate GitHub Issues were accidentally created while testing command-line Issue creation.

The duplicate Issues #42–#50 were closed and the original #33–#41 remained as the LAB_04 work items.

### My Contribution

My contribution included:

- creating the `LAB_04` branch;
- creating and managing GitHub Issues;
- integrating the Stream API query class;
- reviewing Stream pipelines;
- running tests;
- running SpotBugs;
- executing the packaged JAR;
- comparing old and new output;
- updating the Maven version;
- updating GitHub Actions;
- reviewing and updating documentation;
- committing and pushing the implementation.

All submitted code was reviewed and understood before inclusion in the project.

## 15. Control Questions

1. **How is a stream different from a collection?**  
   A collection stores data, while a stream describes operations that process data from a source.

2. **What parts does a typical Stream API pipeline contain?**  
   A source, zero or more intermediate operations and a terminal operation.

3. **Why are intermediate operations called lazy?**  
   They do not process the elements until a terminal operation starts the pipeline.

4. **What does filter do and what type does its predicate have?**  
   `filter` keeps elements for which a `Predicate<T>` returns `true`.

5. **How is map different from mapToDouble?**  
   `map` returns an object stream, while `mapToDouble` produces a primitive `DoubleStream`.

6. **What is distinct used for and what determines its result?**  
   It removes duplicate elements according to `equals()` and `hashCode()`.

7. **Why is sorted().limit(N) different from limit(N).sorted()?**  
   The first sorts the complete stream and then selects N elements. The second selects N input elements first and only sorts that subset.

8. **What is the role of a terminal operation?**  
   It starts stream processing and produces the final result or side effect.

9. **When is Collectors.joining used?**  
   It combines stream elements into one string with optional separators, prefix and suffix.

10. **How does groupingBy build groups?**  
    It calculates a key for every element and places elements or aggregated values into groups associated with those keys.

11. **How can a sum be calculated inside each group?**  
    By using a downstream collector such as `Collectors.summingInt` or `Collectors.summingDouble`.

12. **What values does DoubleSummaryStatistics contain?**  
    Count, sum, minimum, maximum and average.

13. **What happens to statistics for an empty stream?**  
    Count and sum are zero. Minimum and maximum have special infinity values for `DoubleSummaryStatistics`, so empty input should be handled explicitly when those values are displayed.

14. **How is a compound comparator created?**  
    A comparator is created for the main key and then extended with `thenComparing` for the secondary key.

15. **Why should reversed() be applied before thenComparing in this project?**  
    Only the primary visits criterion must be descending. The client criterion must remain ascending.

16. **What problem does Optional solve in search methods?**  
    It explicitly represents that a result may be absent instead of returning `null`.

17. **What is the difference between orElse, orElseThrow and ifPresent?**  
    `orElse` returns a fallback value, `orElseThrow` throws an exception when empty, and `ifPresent` performs an action only when a value exists.

18. **Why is accumulating results in an external mutable list inside forEach undesirable?**  
    It introduces side effects and makes the stream pipeline harder to reason about and safely parallelize.

19. **What must be checked when refactoring a loop into a stream?**  
    The resulting values, order where relevant, duplicates, empty input, boundary conditions, lack of input mutation and compatibility with previous tests.

20. **When can a parallel stream be justified?**  
    When the dataset is sufficiently large, operations are suitable for parallel execution, shared mutable state is avoided and measurements show an actual benefit.

## 16. Conclusion

LAB_04 replaced the main collection-processing calculations with declarative Stream API pipelines.

The project now contains separate methods for filtering, mapping, grouping, summary statistics, top-N processing and Optional search.

The compound comparator provides deterministic ordering, while Optional explicitly represents an absent search result.

Tests cover normal input, empty collections, duplicates, filter boundaries, comparator tie-breaking, top-N boundaries and source immutability.

The previous polymorphic model, tests and external report remain compatible.

The Stream API query layer created in LAB_04 provides a reusable foundation for subsequent laboratory works.
