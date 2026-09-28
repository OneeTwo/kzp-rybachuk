package ua.lpnu.kzp;

/**
 * Represents an annual gym membership.
 */
public final class AnnualMembership extends Membership {

    private static final double ANNUAL_BENEFIT = 0.90;

    /**
     * Creates an annual membership.
     *
     * @param client client name
     * @param plan membership plan
     * @param months membership duration
     * @param visits number of visits
     * @param price membership price
     */
    public AnnualMembership(
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
            MembershipKind.ANNUAL
        );

        if (months < 12) {
            throw new IllegalArgumentException(
                "Annual membership must be at least 12 months"
            );
        }
    }

    /**
     * Calculates the effective cost of one visit
     * with the annual membership benefit.
     *
     * @return effective cost per visit
     */
    @Override
    public double costPerVisit() {
        if (getVisits() == 0) {
            return 0.0;
        }

        return getPrice()
            * ANNUAL_BENEFIT
            / getVisits();
    }
}
