package ua.lpnu.kzp;

/**
 * Represents a monthly gym membership.
 */
public final class MonthlyMembership extends Membership {

    /**
     * Creates a monthly membership.
     *
     * @param client client name
     * @param plan membership plan
     * @param months membership duration
     * @param visits number of visits
     * @param price membership price
     */
    public MonthlyMembership(
        String client,
        String plan,
        int months,
        int visits,
        double price
    ) {
        super(
            client,
            plan,
            months,
            visits,
            price,
            MembershipKind.MONTHLY
        );

        if (months >= 12) {
            throw new IllegalArgumentException(
                "Monthly membership must be shorter than 12 months"
            );
        }
    }

    /**
     * Calculates the cost of one actual visit.
     *
     * @return cost per visit
     */
    @Override
    public double costPerVisit() {
        if (getVisits() == 0) {
            return 0.0;
        }

        return getPrice() / getVisits();
    }
}
