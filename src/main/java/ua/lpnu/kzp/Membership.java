package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents the common base type for gym memberships.
 */
public abstract class Membership {

    private final String client;
    private final String plan;
    private final int months;
    private final int visits;
    private final double price;
    private final MembershipKind kind;

    /**
     * Creates a validated membership.
     *
     * @param client client name
     * @param plan membership plan
     * @param months membership duration in months
     * @param visits number of visits
     * @param price membership price
     * @param kind membership kind
     */
    protected Membership(
        String client,
        String plan,
        int months,
        int visits,
        double price,
        MembershipKind kind
    ) {
        this(
            validateClient(client),
            validatePlan(plan),
            validateMonths(months),
            validateVisits(visits),
            validatePrice(price),
            validateKind(kind),
            true
        );
    }

    private Membership(
        String client,
        String plan,
        int months,
        int visits,
        double price,
        MembershipKind kind,
        boolean validated
    ) {
        this.client = client;
        this.plan = plan;
        this.months = months;
        this.visits = visits;
        this.price = price;
        this.kind = kind;
    }

    private static String validateClient(String client) {
        if (client == null) {
            throw new NullPointerException(
                "Client cannot be null"
            );
        }

        if (client.isBlank()) {
            throw new IllegalArgumentException(
                "Client cannot be empty"
            );
        }

        return client;
    }

    private static String validatePlan(String plan) {
        if (plan == null) {
            throw new NullPointerException(
                "Plan cannot be null"
            );
        }

        if (plan.isBlank()) {
            throw new IllegalArgumentException(
                "Plan cannot be empty"
            );
        }

        return plan;
    }

    private static int validateMonths(int months) {
        if (months <= 0) {
            throw new IllegalArgumentException(
                "Months must be positive"
            );
        }

        return months;
    }

    private static int validateVisits(int visits) {
        if (visits < 0) {
            throw new IllegalArgumentException(
                "Visits cannot be negative"
            );
        }

        return visits;
    }

    private static double validatePrice(double price) {
        if (price < 0 || !Double.isFinite(price)) {
            throw new IllegalArgumentException(
                "Price must be finite and non-negative"
            );
        }

        return price;
    }

    private static MembershipKind validateKind(
        MembershipKind kind
    ) {
        return Objects.requireNonNull(
            kind,
            "Membership kind cannot be null"
        );
    }

    /**
     * Creates a membership from a CSV line.
     *
     * @param line CSV line
     * @return parsed membership
     */
    public static Membership fromCsv(String line) {
        Objects.requireNonNull(
            line,
            "Line cannot be null"
        );

        String[] fields = line.split(";", -1);

        if (fields.length != 5) {
            throw new IllegalArgumentException(
                "Expected 5 fields"
            );
        }

        try {
            String client = fields[0].trim();
            String plan = fields[1].trim();
            int months = Integer.parseInt(fields[2].trim());
            int visits = Integer.parseInt(fields[3].trim());
            double price = Double.parseDouble(fields[4].trim());

            if (months >= 12) {
                return new AnnualMembership(
                    client,
                    plan,
                    months,
                    visits,
                    price
                );
            }

            return new MonthlyMembership(
                client,
                plan,
                months,
                visits,
                price
            );

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                "Invalid numeric value",
                exception
            );
        }
    }

    /**
     * Calculates the effective cost of one visit.
     *
     * @return cost per visit
     */
    public abstract double costPerVisit();

    /**
     * Returns the client name.
     *
     * @return client name
     */
    public final String getClient() {
        return client;
    }

    /**
     * Returns the membership plan.
     *
     * @return membership plan
     */
    public final String getPlan() {
        return plan;
    }

    /**
     * Returns the membership duration.
     *
     * @return duration in months
     */
    public final int getMonths() {
        return months;
    }

    /**
     * Returns the number of visits.
     *
     * @return number of visits
     */
    public final int getVisits() {
        return visits;
    }

    /**
     * Returns the membership price.
     *
     * @return membership price
     */
    public final double getPrice() {
        return price;
    }

    /**
     * Returns the membership kind.
     *
     * @return membership kind
     */
    public final MembershipKind getKind() {
        return kind;
    }

    /**
     * Compares memberships by concrete subtype and stored values.
     *
     * @param other object to compare
     * @return true if memberships are logically equal
     */
    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Membership membership = (Membership) other;

        return months == membership.months
            && visits == membership.visits
            && Double.compare(price, membership.price) == 0
            && client.equals(membership.client)
            && plan.equals(membership.plan);
    }

    /**
     * Returns a hash code consistent with equals.
     *
     * @return membership hash code
     */
    @Override
    public final int hashCode() {
        return Objects.hash(
            getClass(),
            client,
            plan,
            months,
            visits,
            price
        );
    }

    /**
     * Returns a readable representation of the membership.
     *
     * @return formatted membership information
     */
    @Override
    public String toString() {
        return String.format(
            Locale.ROOT,
            "%s; %s; %d months; %d visits; %.2f",
            client,
            plan,
            months,
            visits,
            price
        );
    }
}
