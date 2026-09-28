# Laboratory Work No. 3 Report

## 1. Topic and Variant

**Topic:** Inheritance, Interfaces and Polymorphism in a Java Project  
**Laboratory Work:** No. 3  
**Variant:** 20 — Gym  
**Operating System:** Windows 11

**Repository:**  
https://github.com/OneeTwo/kzp-rybachuk

**Version:** 1.2.0  
**Git tag:** v1.2.0

## 2. Objective

The objective of this laboratory work was to extend the domain model created in LAB_02 using inheritance and polymorphism.

The new implementation introduces:

- an abstract `Membership` base class;
- `MonthlyMembership` and `AnnualMembership` subclasses;
- the `MembershipKind` enum;
- a polymorphic `costPerVisit()` method;
- consistent `equals()` and `hashCode()`;
- equality verification using `HashSet`;
- new JUnit tests for the hierarchy and polymorphic behavior.

The external report and the original statistics remain unchanged.

## 3. State Before and After LAB_03

### Before LAB_03

LAB_02 contained one concrete domain entity:

```text
Membership
```

It stored:

```text
client
plan
months
visits
price
```

CSV records were converted using:

```java
Membership.fromCsv(line)
```

### After LAB_03

`Membership` became an abstract base class.

The hierarchy is:

```text
Membership
├── MonthlyMembership
└── AnnualMembership
```

Common fields and validation remain in `Membership`.

Different membership behavior is implemented in subclasses through:

```java
costPerVisit()
```

## 4. Base Type

The common base type is:

```java
public abstract class Membership
```

It contains:

```java
private final String client;
private final String plan;
private final int months;
private final int visits;
private final double price;
private final MembershipKind kind;
```

The common validation rules are:

- client must not be null or blank;
- plan must not be null or blank;
- months must be greater than zero;
- visits must not be negative;
- price must be finite and non-negative;
- membership kind must not be null.

The base class declares:

```java
public abstract double costPerVisit();
```

The class is abstract because a generic membership does not define one specific cost-per-visit algorithm.

## 5. Subtypes

### MonthlyMembership

`MonthlyMembership` represents memberships shorter than 12 months.

Rule:

```text
months < 12
```

Its polymorphic method calculates:

```text
price / visits
```

If visits are zero, the method returns `0.0`.

### AnnualMembership

`AnnualMembership` represents memberships of 12 months or more.

Rule:

```text
months >= 12
```

Its cost-per-visit calculation uses a 10% annual benefit:

```text
price * 0.90 / visits
```

If visits are zero, the method returns `0.0`.

The different formulas demonstrate different subtype behavior.

They do not modify the original revenue statistics.

## 6. MembershipKind Enum

The project contains:

```java
public enum MembershipKind {
    MONTHLY("Monthly"),
    ANNUAL("Annual")
}
```

The enum represents a fixed set of membership categories.

The base class stores a `MembershipKind` value and provides access through:

```java
getKind()
```

## 7. equals and hashCode

`Membership` implements consistent `equals()` and `hashCode()` methods.

Logical equality includes:

- concrete subtype;
- client;
- plan;
- months;
- visits;
- price.

The concrete class is included in equality using:

```java
getClass()
```

Therefore, objects of different membership subtypes are not considered equal.

Correct behavior is verified using `HashSet`.

When two logically equal memberships are inserted, only one unique element remains.

## 8. Polymorphism

Objects are stored in:

```java
List<Membership>
```

The processing code calls:

```java
double costPerVisit = membership.costPerVisit();
```

The variable has type `Membership`, but Java calls the implementation belonging to the actual object.

For example:

```text
MonthlyMembership.costPerVisit()
```

or:

```text
AnnualMembership.costPerVisit()
```

The program does not use:

```java
if (membership instanceof MonthlyMembership)
```

to select behavior.

This demonstrates runtime polymorphism.

### Inheritance or Composition

Inheritance was selected because `MonthlyMembership` and `AnnualMembership` are both types of `Membership`.

They share common fields and validation but have different behavior.

`VisitsPrice` remains a helper record and is used through composition rather than inheritance.

## 9. Testing

The previous tests were preserved and adapted because `Membership` became abstract.

Test files:

```text
MainTest.java
MembershipTest.java
MembershipHierarchyTest.java
```

The tests verify:

- valid common fields;
- null and blank values;
- invalid numeric values;
- CSV parsing;
- creation of monthly memberships;
- creation of annual memberships;
- `MembershipKind`;
- monthly cost per visit;
- annual cost per visit;
- polymorphic behavior;
- zero visits;
- equality;
- equal hash codes;
- `HashSet` behavior;
- inequality of different subtypes;
- compatibility with previous statistics.

Commands:

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
```

Result:

```text
BUILD SUCCESS
```

SpotBugs also completed successfully.

During development SpotBugs initially reported:

```text
CT_CONSTRUCTOR_THROW
```

for the abstract `Membership` constructor.

The constructor was corrected by validating arguments before assigning the object state.

After correction:

```text
BUILD SUCCESS
```

## 10. Infrastructure

The project continues to use:

- Java 21;
- Maven;
- Maven Wrapper;
- JUnit 5;
- SpotBugs;
- Maven Shade Plugin;
- GitHub Actions.

Current version:

```text
1.2.0
```

Commands:

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
.\mvnw.cmd package
```

Run JAR:

```powershell
java -jar target\lab01-1.2.0.jar
```

Version check:

```powershell
java -jar target\lab01-1.2.0.jar --version
```

Result:

```text
lab01 1.2.0
```

GitHub Actions runs on:

- Windows;
- Ubuntu;
- macOS.

The generated JAR is uploaded as a GitHub Actions artifact.

**Final CI:** will be added after the final Pull Request run.  
**JAR artifact:** will be available from the final GitHub Actions run.  
**Git tag:** v1.2.0.

## 11. GitHub Issues and Pull Request

The work was performed in:

```text
LAB_03
```

| Issue | Change | Verification |
|---|---|---|
| #26 | Implement LAB_03 Membership hierarchy | Compilation and hierarchy tests |
| #27 | Implement LAB_03 membership subtypes | Subtype and polymorphism tests |
| #28 | Add MembershipKind enum | Enum tests |
| #29 | Implement Membership equality | HashSet and hash-code tests |
| #30 | Add LAB_03 tests | Maven test and verify |
| #31 | Update LAB_03 documentation | Documentation and build verification |

The final Pull Request merges:

```text
LAB_03 -> main
```

## 12. Behavior Comparison

### LAB_02 result

```text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
```

### LAB_03 result

```text
Line 4 skipped: invalid numeric value
Line 5 skipped: invalid number format
Valid records: 3
Average visits: 47.33
Total revenue: 8700.00
Longest membership: 12 months
```

The results are identical.

LAB_02 used one concrete `Membership` class.

LAB_03 uses:

```text
Membership
├── MonthlyMembership
└── AnnualMembership
```

The internal model changed while the external report remained compatible.

## 13. Academic Integrity

ChatGPT was used as an AI assistant during LAB_03.

### AI contribution

ChatGPT was used for:

- analysis of LAB_03 requirements;
- planning the class hierarchy;
- recommendations for inheritance and polymorphism;
- creation of enum and equality logic;
- preparation of JUnit tests;
- analysis of the SpotBugs error;
- Git and GitHub workflow guidance;
- documentation preparation.

### Accepted recommendations

The following recommendations were used:

- making `Membership` abstract;
- creating `MonthlyMembership`;
- creating `AnnualMembership`;
- creating `MembershipKind`;
- using `costPerVisit()` as the polymorphic method;
- implementing `equals()` and `hashCode()`;
- testing equality using `HashSet`;
- preserving the previous output.

### Corrected recommendations

The first constructor implementation generated the SpotBugs `CT_CONSTRUCTOR_THROW` warning.

The implementation was changed so validation is performed before the object state is assigned.

The exact subtype classification and cost-per-visit formulas were project decisions because the methodology defines the required hierarchy and polymorphic operation but does not provide an exact formula.

### My contribution

My contribution included:

- creating the LAB_03 branch;
- creating GitHub Issues;
- adding and reviewing source files;
- running tests;
- running SpotBugs;
- debugging errors;
- verifying the JAR;
- comparing LAB_02 and LAB_03 output;
- updating Maven and CI configuration;
- committing and pushing the implementation.

All submitted code was reviewed and understood before inclusion in the project.

## 14. Control Questions

1. **What common problem does the base type solve?**  
   It stores shared membership fields and validation rules in one place.

2. **What is the difference between extends and implements?**  
   `extends` inherits from a class, while `implements` provides the behavior required by an interface.

3. **Why can a base class be abstract?**  
   It represents common behavior while preventing creation of an incomplete generic object.

4. **What does super(...) do?**  
   It calls the constructor of the parent class.

5. **What does @Override do?**  
   It verifies that a method overrides a method from the parent type.

6. **What is polymorphism?**  
   It allows the same base-type method call to execute different implementations depending on the actual object.

7. **When should an interface or abstract class be used?**  
   An abstract class is useful when objects share state and implementation. An interface is useful mainly for defining a common capability.

8. **What is an is-a relationship?**  
   It means that one type is a specialized form of another type.

9. **Why use enum?**  
   It limits a value to a fixed set of valid categories.

10. **Why can enum values be compared with ==?**  
    Each enum constant is represented by one fixed instance.

11. **What rule connects equals and hashCode?**  
    Equal objects must have the same hash code.

12. **Why are mutable HashMap keys dangerous?**  
    Changing a field used by `hashCode()` can make the key impossible to find correctly.

13. **Why can different subtypes be unequal?**  
    Their concrete type can be part of their logical identity.

14. **How does a polymorphic collection work?**  
    Objects of different subtypes are stored through their common base type.

15. **How do tests prove different subtype behavior?**  
    They call `costPerVisit()` for monthly and annual memberships and verify different results.

16. **How is equality tested in HashSet?**  
    Two equal memberships are added and the set is checked to contain only one element.

17. **What do sealed and permits do?**  
    They restrict which classes are allowed to extend a type.

18. **Why should switch not replace polymorphism?**  
    Different behavior should remain inside each subtype instead of being duplicated in external code.

19. **What previous behavior must remain?**  
    Previous tests, CSV format, calculated statistics and report output must remain compatible.

20. **What best demonstrates polymorphism in this project?**  
    Calling `membership.costPerVisit()` through a `Membership` reference while subclasses provide different implementations.

## 15. Conclusion

In LAB_03, the gym membership model was converted into a polymorphic hierarchy.

The abstract `Membership` class stores common state and validation.

`MonthlyMembership` and `AnnualMembership` provide different implementations of `costPerVisit()`.

`MembershipKind` represents fixed membership categories.

`equals()` and `hashCode()` allow membership objects to work correctly in hash-based collections.

The previous tests and external output remain compatible.

The resulting model provides the foundation for the next laboratory work.
