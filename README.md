# Gym Membership Report — LAB_03

Laboratory Work No. 3 for the Cross-Platform Programming course.

**Variant:** 20 — Gym  
**Version:** 1.2.0

## Description

LAB_03 extends the domain model from LAB_02 using inheritance, polymorphism, enum types, and logical object equality.

The external behavior of the application remains unchanged.

## Domain Model

The membership hierarchy is:

```text
Membership
├── MonthlyMembership
└── AnnualMembership
```

`Membership` is an abstract base class containing the common membership state:

- client
- plan
- months
- visits
- price
- membership kind

The subclasses implement different behavior through:

```java
double costPerVisit()
```

### MonthlyMembership

Used for memberships shorter than 12 months.

Cost per visit:

```text
price / visits
```

### AnnualMembership

Used for memberships of 12 months or more.

The effective annual cost per visit uses a 10% annual benefit:

```text
price * 0.90 / visits
```

If the number of visits is zero, both implementations return `0.0`.

## MembershipKind

The project uses:

```java
MembershipKind.MONTHLY
MembershipKind.ANNUAL
```

The enum provides a readable label for each membership category.

## Input Format

The input format remains unchanged:

```text
client;plan;months;visits;price
```

Example:

```text
Іван Петренко;Standard;3;24;1500.00
Марія Коваль;Premium;12;110;6500.00
Олег Бондар;Basic;1;8;700.00
```

`Membership.fromCsv()` creates:

```text
1–11 months -> MonthlyMembership
12+ months  -> AnnualMembership
```

## Polymorphism

Membership objects are stored as:

```java
List<Membership>
```

The application calls:

```java
membership.costPerVisit()
```

through the common `Membership` type.

Java automatically selects the implementation of either `MonthlyMembership` or `AnnualMembership`.

No `instanceof` or subtype-specific `switch` is required.

## Equality

`Membership` implements:

```java
equals()
hashCode()
```

Logical equality includes:

- concrete subtype
- client
- plan
- months
- visits
- price

Equal memberships therefore behave correctly in `HashSet`.

## Output

The original report remains unchanged:

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
java -jar target\lab01-1.2.0.jar
```

Version:

```powershell
java -jar target\lab01-1.2.0.jar --version
```

Expected:

```text
lab01 1.2.0
```

## Project Structure

```text
src/main/java/ua/lpnu/kzp/
├── Main.java
├── Membership.java
├── MonthlyMembership.java
├── AnnualMembership.java
├── MembershipKind.java
└── VisitsPrice.java

src/test/java/ua/lpnu/kzp/
├── MainTest.java
├── MembershipTest.java
└── MembershipHierarchyTest.java
```

## Technologies

- Java 21
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
