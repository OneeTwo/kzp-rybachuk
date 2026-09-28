package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MembershipTest {

    @Test
    void constructorCreatesValidMembership() {
        Membership membership =
            new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                24,
                1500.0
            );

        assertEquals("Ivan", membership.getClient());
        assertEquals("Standard", membership.getPlan());
        assertEquals(3, membership.getMonths());
        assertEquals(24, membership.getVisits());
        assertEquals(1500.0, membership.getPrice(), 0.0001);
    }

    @Test
    void constructorRejectsNullClient() {
        assertThrows(
            NullPointerException.class,
            () -> new MonthlyMembership(
                null,
                "Standard",
                3,
                24,
                1500.0
            )
        );
    }

    @Test
    void constructorRejectsEmptyClient() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new MonthlyMembership(
                "",
                "Standard",
                3,
                24,
                1500.0
            )
        );
    }

    @Test
    void constructorRejectsEmptyPlan() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new MonthlyMembership(
                "Ivan",
                "",
                3,
                24,
                1500.0
            )
        );
    }

    @Test
    void constructorRejectsZeroMonths() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new MonthlyMembership(
                "Ivan",
                "Standard",
                0,
                24,
                1500.0
            )
        );
    }

    @Test
    void constructorRejectsNegativeVisits() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                -1,
                1500.0
            )
        );
    }

    @Test
    void constructorRejectsNegativePrice() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                24,
                -1.0
            )
        );
    }

    @Test
    void fromCsvCreatesMonthlyMembership() {
        Membership membership =
            Membership.fromCsv(
                "Ivan;Standard;3;24;1500.00"
            );

        assertInstanceOf(
            MonthlyMembership.class,
            membership
        );

        assertEquals("Ivan", membership.getClient());
        assertEquals("Standard", membership.getPlan());
        assertEquals(3, membership.getMonths());
        assertEquals(24, membership.getVisits());
        assertEquals(1500.0, membership.getPrice(), 0.0001);
    }

    @Test
    void fromCsvCreatesAnnualMembership() {
        Membership membership =
            Membership.fromCsv(
                "Maria;Premium;12;110;6500.00"
            );

        assertInstanceOf(
            AnnualMembership.class,
            membership
        );

        assertEquals(
            MembershipKind.ANNUAL,
            membership.getKind()
        );
    }

    @Test
    void fromCsvRejectsWrongFieldCount() {
        assertThrows(
            IllegalArgumentException.class,
            () -> Membership.fromCsv(
                "Ivan;Standard;3;24"
            )
        );
    }

    @Test
    void fromCsvRejectsInvalidNumber() {
        assertThrows(
            IllegalArgumentException.class,
            () -> Membership.fromCsv(
                "Ivan;Standard;abc;24;1500.00"
            )
        );
    }

    @Test
    void visitsPriceUsesValueEquality() {
        VisitsPrice first =
            new VisitsPrice(24, 1500.0);

        VisitsPrice second =
            new VisitsPrice(24, 1500.0);

        assertEquals(first, second);
        assertEquals(24, first.visits());
        assertEquals(
            1500.0,
            first.price(),
            0.0001
        );
    }

    @Test
    void visitsPriceRejectsNegativeValues() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new VisitsPrice(-1, 100.0)
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new VisitsPrice(10, -1.0)
        );
    }
}
