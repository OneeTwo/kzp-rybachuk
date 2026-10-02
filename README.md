# Gym Membership Report — LAB_04

Laboratory Work No. 4 for the Cross-Platform Programming course.

**Variant:** 20 — Gym  
**Version:** 1.3.0

## Description

LAB_04 refactors collection processing from manual loops to Java Stream API.

The project preserves the polymorphic membership model from LAB_03 and keeps the previous external report unchanged.

## Domain Model

The existing hierarchy is preserved:

```text
Membership
├── MonthlyMembership
└── AnnualMembership
```

The project also uses:

```java
MembershipKind.MONTHLY
MembershipKind.ANNUAL
```

The input format remains:

```text
client;plan;months;visits;price
```

## Stream API Queries

Stream queries are implemented in:

```text
MembershipQueries.java
```

### 1. Filter

```java
activeMemberships(...)
```

Selects memberships with:

```text
visits > 0
```

Because the existing LAB_03 model does not contain start and end dates, a membership with at least one recorded visit is treated as active for LAB_04.

Pipeline:

```text
source -> stream -> filter -> toList
```

### 2. Map

```java
clientNames(...)
```

Transforms membership objects into client names.

Pipeline:

```text
source -> stream -> map -> toList
```

### 3. Grouping

```java
visitsByKind(...)
```

Groups and sums visits by:

```java
MembershipKind
```

Pipeline:

```text
source
-> stream
-> groupingBy
-> summingInt
-> Map<MembershipKind, Integer>
```

An `EnumMap` is used because the grouping key is an enum.

### 4. Statistics

```java
visitStatistics(...)
```

Uses:

```java
Collectors.summarizingInt(Membership::getVisits)
```

Result:

```text
IntSummaryStatistics
```

It provides:

- count
- sum
- minimum
- maximum
- average

### 5. Top-N

```java
topMemberships(...)
```

Sorting rules:

```text
1. visits descending
2. client ascending
3. limit N
```

Comparator:

```java
Comparator.comparingInt(Membership::getVisits)
    .reversed()
    .thenComparing(Membership::getClient)
```

Rules for N:

```text
N < 0  -> IllegalArgumentException
N = 0  -> empty list
N > size -> all available elements
```

### Optional Search

```java
findByClient(...)
```

Returns:

```java
Optional<Membership>
```

The first matching membership is returned.

If no client is found:

```text
Optional.empty()
```

No `null` value is returned.

## Test Dataset

The Stream API tests use six memberships.

The dataset contains:

- different membership subtypes;
- duplicate client name `Ivan`;
- zero visits as the filter boundary;
- equal visit counts for comparator tie-breaking;
- more than five records for testing top-5.

The six-record query dataset is kept in tests so the original LAB_03 control input and output remain unchanged.

## Previous Report Compatibility

LAB_03 used manual accumulation for several statistics.

LAB_04 replaces those calculations with Stream API operations.

Examples:

```text
manual total revenue
-> mapToDouble(...).sum()

manual maximum months
-> mapToInt(...).max()

manual average visits
-> IntSummaryStatistics
```

The external output remains:

```text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
```

## Build and Test

Windows:

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
.\mvnw.cmd package
```

Run:

```powershell
java -jar target\lab01-1.3.0.jar
```

Version:

```powershell
java -jar target\lab01-1.3.0.jar --version
```

Expected:

```text
lab01 1.3.0
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
└── VisitsPrice.java

src/test/java/ua/lpnu/kzp/
├── MainTest.java
├── MembershipTest.java
├── MembershipHierarchyTest.java
└── MembershipQueriesTest.java
```

## Technologies

- Java 21
- Stream API
- Maven
- Maven Wrapper
- JUnit 5
- SpotBugs
- Maven Shade Plugin
- GitHub Actions

## CI

GitHub Actions verifies the project on:

- Windows
- Ubuntu
- macOS

The executable JAR is uploaded as a workflow artifact.

## Repository

https://github.com/OneeTwo/kzp-rybachuk
