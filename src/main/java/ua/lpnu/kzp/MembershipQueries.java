package ua.lpnu.kzp;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Provides Stream API queries for gym memberships.
 */
public final class MembershipQueries {

    private MembershipQueries() {
    }

    /**
     * Returns memberships considered active.
     *
     * <p>In the current project model, a membership is considered
     * active when it has at least one recorded visit.</p>
     *
     * @param memberships source memberships
     * @return active memberships
     */
    public static List<Membership> activeMemberships(
        List<Membership> memberships
    ) {
        Objects.requireNonNull(
            memberships,
            "Memberships cannot be null"
        );

        return memberships.stream()
            .filter(membership -> membership.getVisits() > 0)
            .toList();
    }

    /**
     * Returns client names from the membership collection.
     *
     * @param memberships source memberships
     * @return client names
     */
    public static List<String> clientNames(
        List<Membership> memberships
    ) {
        Objects.requireNonNull(
            memberships,
            "Memberships cannot be null"
        );

        return memberships.stream()
            .map(Membership::getClient)
            .toList();
    }

    /**
     * Groups total visits by membership kind.
     *
     * @param memberships source memberships
     * @return total visits grouped by membership kind
     */
    public static Map<MembershipKind, Integer> visitsByKind(
        List<Membership> memberships
    ) {
        Objects.requireNonNull(
            memberships,
            "Memberships cannot be null"
        );

        return memberships.stream()
            .collect(Collectors.groupingBy(
                Membership::getKind,
                () -> new EnumMap<>(MembershipKind.class),
                Collectors.summingInt(Membership::getVisits)
            ));
    }

    /**
     * Calculates summary statistics for visit counts.
     *
     * @param memberships source memberships
     * @return visit statistics
     */
    public static IntSummaryStatistics visitStatistics(
        List<Membership> memberships
    ) {
        Objects.requireNonNull(
            memberships,
            "Memberships cannot be null"
        );

        return memberships.stream()
            .collect(Collectors.summarizingInt(
                Membership::getVisits
            ));
    }

    /**
     * Returns the first N memberships sorted by visits descending
     * and client name ascending.
     *
     * @param memberships source memberships
     * @param n maximum number of results
     * @return sorted top memberships
     */
    public static List<Membership> topMemberships(
        List<Membership> memberships,
        int n
    ) {
        Objects.requireNonNull(
            memberships,
            "Memberships cannot be null"
        );

        if (n < 0) {
            throw new IllegalArgumentException(
                "N cannot be negative"
            );
        }

        Comparator<Membership> comparator =
            Comparator.comparingInt(Membership::getVisits)
                .reversed()
                .thenComparing(Membership::getClient);

        return memberships.stream()
            .sorted(comparator)
            .limit(n)
            .toList();
    }

    /**
     * Finds the first membership belonging to the specified client.
     *
     * @param memberships source memberships
     * @param client client name
     * @return matching membership or empty Optional
     */
    public static Optional<Membership> findByClient(
        List<Membership> memberships,
        String client
    ) {
        Objects.requireNonNull(
            memberships,
            "Memberships cannot be null"
        );

        Objects.requireNonNull(
            client,
            "Client cannot be null"
        );

        return memberships.stream()
            .filter(membership ->
                membership.getClient().equals(client))
            .findFirst();
    }
}
