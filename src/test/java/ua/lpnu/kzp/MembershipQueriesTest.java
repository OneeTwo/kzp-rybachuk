package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class MembershipQueriesTest {

    private List<Membership> createMemberships() {
        return List.of(
            new MonthlyMembership(
                "Ivan",
                "Standard",
                3,
                24,
                1500.0
            ),
            new AnnualMembership(
                "Maria",
                "Premium",
                12,
                110,
                6500.0
            ),
            new MonthlyMembership(
                "Oleh",
                "Basic",
                1,
                8,
                700.0
            ),
            new AnnualMembership(
                "Anna",
                "Premium",
                12,
                24,
                5000.0
            ),
            new MonthlyMembership(
                "Taras",
                "Standard",
                6,
                0,
                2800.0
            ),
            new MonthlyMembership(
                "Ivan",
                "Basic",
                2,
                15,
                900.0
            )
        );
    }

    @Test
    void activeMembershipsFiltersByVisits() {
        List<Membership> result =
            MembershipQueries.activeMemberships(
                createMemberships()
            );

        assertEquals(5, result.size());

        assertTrue(
            result.stream()
                .allMatch(membership ->
                    membership.getVisits() > 0)
        );
    }

    @Test
    void activeMembershipsExcludesBoundaryZeroVisits() {
        List<Membership> result =
            MembershipQueries.activeMemberships(
                createMemberships()
            );

        assertFalse(
            result.stream()
                .anyMatch(membership ->
                    membership.getClient().equals("Taras"))
        );
    }

    @Test
    void clientNamesMapsClients() {
        List<String> result =
            MembershipQueries.clientNames(
                createMemberships()
            );

        assertEquals(
            List.of(
                "Ivan",
                "Maria",
                "Oleh",
                "Anna",
                "Taras",
                "Ivan"
            ),
            result
        );
    }

    @Test
    void visitsByKindGroupsAndSumsVisits() {
        Map<MembershipKind, Integer> result =
            MembershipQueries.visitsByKind(
                createMemberships()
            );

        assertEquals(
            47,
            result.get(MembershipKind.MONTHLY)
        );

        assertEquals(
            134,
            result.get(MembershipKind.ANNUAL)
        );
    }

    @Test
    void visitStatisticsCalculatesSummary() {
        IntSummaryStatistics statistics =
            MembershipQueries.visitStatistics(
                createMemberships()
            );

        assertEquals(6, statistics.getCount());
        assertEquals(181, statistics.getSum());
        assertEquals(0, statistics.getMin());
        assertEquals(110, statistics.getMax());

        assertEquals(
            30.1667,
            statistics.getAverage(),
            0.0001
        );
    }

    @Test
    void topMembershipsReturnsTopFive() {
        List<Membership> result =
            MembershipQueries.topMemberships(
                createMemberships(),
                5
            );

        assertEquals(5, result.size());

        assertEquals(
            "Maria",
            result.get(0).getClient()
        );

        assertEquals(
            110,
            result.get(0).getVisits()
        );
    }

    @Test
    void topMembershipsUsesClientAsSecondCriterion() {
        List<Membership> result =
            MembershipQueries.topMemberships(
                createMemberships(),
                6
            );

        assertEquals(
            "Anna",
            result.get(1).getClient()
        );

        assertEquals(
            "Ivan",
            result.get(2).getClient()
        );

        assertEquals(
            24,
            result.get(1).getVisits()
        );

        assertEquals(
            24,
            result.get(2).getVisits()
        );
    }

    @Test
    void topMembershipsWithZeroReturnsEmptyList() {
        List<Membership> result =
            MembershipQueries.topMemberships(
                createMemberships(),
                0
            );

        assertTrue(result.isEmpty());
    }

    @Test
    void topMembershipsWithLargeNReturnsAllElements() {
        List<Membership> result =
            MembershipQueries.topMemberships(
                createMemberships(),
                100
            );

        assertEquals(6, result.size());
    }

    @Test
    void topMembershipsRejectsNegativeN() {
        assertThrows(
            IllegalArgumentException.class,
            () -> MembershipQueries.topMemberships(
                createMemberships(),
                -1
            )
        );
    }

    @Test
    void findByClientReturnsFirstDuplicate() {
        Optional<Membership> result =
            MembershipQueries.findByClient(
                createMemberships(),
                "Ivan"
            );

        assertTrue(result.isPresent());

        assertEquals(
            "Standard",
            result.orElseThrow().getPlan()
        );
    }

    @Test
    void findByClientReturnsEmptyOptional() {
        Optional<Membership> result =
            MembershipQueries.findByClient(
                createMemberships(),
                "Unknown"
            );

        assertTrue(result.isEmpty());
    }

    @Test
    void queriesHandleEmptyCollection() {
        List<Membership> empty = List.of();

        assertTrue(
            MembershipQueries
                .activeMemberships(empty)
                .isEmpty()
        );

        assertTrue(
            MembershipQueries
                .clientNames(empty)
                .isEmpty()
        );

        assertTrue(
            MembershipQueries
                .visitsByKind(empty)
                .isEmpty()
        );

        IntSummaryStatistics statistics =
            MembershipQueries
                .visitStatistics(empty);

        assertEquals(0, statistics.getCount());
        assertEquals(0, statistics.getSum());

        assertTrue(
            MembershipQueries
                .topMemberships(empty, 5)
                .isEmpty()
        );

        assertTrue(
            MembershipQueries
                .findByClient(empty, "Ivan")
                .isEmpty()
        );
    }

    @Test
    void queriesDoNotModifySourceCollection() {
        List<Membership> source =
            new ArrayList<>(createMemberships());

        List<Membership> original =
            new ArrayList<>(source);

        MembershipQueries.activeMemberships(source);
        MembershipQueries.clientNames(source);
        MembershipQueries.visitsByKind(source);
        MembershipQueries.visitStatistics(source);
        MembershipQueries.topMemberships(source, 5);
        MembershipQueries.findByClient(source, "Ivan");

        assertEquals(original, source);
    }
}
