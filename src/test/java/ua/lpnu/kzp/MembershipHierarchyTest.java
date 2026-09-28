package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class MembershipHierarchyTest {

    @Test
    void monthlyMembershipHasMonthlyKind() {
        Membership membership =
            new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                24,
                1500.0
            );

        assertEquals(
            MembershipKind.MONTHLY,
            membership.getKind()
        );

        assertEquals(
            "Monthly",
            membership.getKind().label()
        );
    }

    @Test
    void annualMembershipHasAnnualKind() {
        Membership membership =
            new AnnualMembership(
                "Maria",
                "Premium",
                12,
                110,
                6500.0
            );

        assertEquals(
            MembershipKind.ANNUAL,
            membership.getKind()
        );

        assertEquals(
            "Annual",
            membership.getKind().label()
        );
    }

    @Test
    void monthlyMembershipRejectsTwelveMonths() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new MonthlyMembership(
                "Ivan",
                "Standard",
                12,
                24,
                1500.0
            )
        );
    }

    @Test
    void annualMembershipRejectsLessThanTwelveMonths() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new AnnualMembership(
                "Maria",
                "Premium",
                11,
                100,
                6000.0
            )
        );
    }

    @Test
    void monthlyCalculatesCostPerVisit() {
        Membership membership =
            new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                20,
                1000.0
            );

        assertEquals(
            50.0,
            membership.costPerVisit(),
            0.0001
        );
    }

    @Test
    void annualCalculatesCostPerVisitDifferently() {
        Membership membership =
            new AnnualMembership(
                "Maria",
                "Premium",
                12,
                100,
                5000.0
            );

        assertEquals(
            45.0,
            membership.costPerVisit(),
            0.0001
        );
    }

    @Test
    void polymorphicSubtypesHaveDifferentBehavior() {
        Membership monthly =
            new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                20,
                1000.0
            );

        Membership annual =
            new AnnualMembership(
                "Maria",
                "Premium",
                12,
                20,
                1000.0
            );

        assertNotEquals(
            monthly.costPerVisit(),
            annual.costPerVisit()
        );
    }

    @Test
    void zeroVisitsReturnZeroCostPerVisit() {
        Membership monthly =
            new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                0,
                1000.0
            );

        Membership annual =
            new AnnualMembership(
                "Maria",
                "Premium",
                12,
                0,
                5000.0
            );

        assertEquals(
            0.0,
            monthly.costPerVisit(),
            0.0001
        );

        assertEquals(
            0.0,
            annual.costPerVisit(),
            0.0001
        );
    }

    @Test
    void equalMembershipsHaveSameHashCode() {
        Membership first =
            new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                24,
                1500.0
            );

        Membership second =
            new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                24,
                1500.0
            );

        assertEquals(first, second);
        assertEquals(
            first.hashCode(),
            second.hashCode()
        );
    }

    @Test
    void hashSetDoesNotDuplicateEqualMemberships() {
        Set<Membership> memberships =
            new HashSet<>();

        memberships.add(
            new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                24,
                1500.0
            )
        );

        memberships.add(
            new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                24,
                1500.0
            )
        );

        assertEquals(
            1,
            memberships.size()
        );
    }

    @Test
    void differentSubtypesAreNotEqual() {
        Membership monthly =
            new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                24,
                1500.0
            );

        Membership annual =
            new AnnualMembership(
                "Ivan",
                "Standard",
                12,
                24,
                1500.0
            );

        assertNotEquals(
            monthly,
            annual
        );
    }
}
