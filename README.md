# Gym Membership Report — LAB_02

Laboratory Work No. 2 for the Cross-Platform Programming course.

**Variant:** 20 — Gym

## Description

LAB_02 refactors the program from LAB_01 using classes, encapsulation, validation, and Java records.

The external behavior of the application remains unchanged.

## Domain Model

### Membership

`Membership` represents one gym membership.

Fields:

- `client` — client name
- `plan` — membership plan
- `months` — membership duration
- `visits` — number of visits
- `price` — membership price

Validation:

- client must not be null or blank;
- plan must not be null or blank;
- months must be greater than 0;
- visits must be greater than or equal to 0;
- price must be finite and greater than or equal to 0.

CSV records are converted to objects using:

```java
Membership.fromCsv(line)
```

### VisitsPrice

`VisitsPrice` is an immutable Java record containing:

```text
visits
price
```

It is used as a helper value during statistics calculation.

## Input Format

```text
client;plan;months;visits;price
```

Example:

```text
Іван Петренко;Standard;3;24;1500.00
Марія Коваль;Premium;12;110;6500.00
Олег Бондар;Basic;1;8;700.00
```

## Output

The application calculates:

- number of valid records;
- average number of visits;
- total revenue;
- longest membership duration.

Example:

```text
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

**Version:** 1.1.0  
**Git tag:** v1.1.0

## Run

```powershell
java -jar target\lab01-1.1.0.jar
```

Custom input and output:

```powershell
java -jar target\lab01-1.1.0.jar --input data\input.csv --output out\report.txt
```

## Project Structure

```text
src/main/java/ua/lpnu/kzp/
├── Main.java
├── Membership.java
└── VisitsPrice.java

src/test/java/ua/lpnu/kzp/
├── MainTest.java
└── MembershipTest.java
```

## Technologies

- Java 21
- Maven
- JUnit 5
- SpotBugs
- GitHub Actions

## Repository

https://github.com/OneeTwo/kzp-rybachuk
