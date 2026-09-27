package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents a gym membership record.
 */
public final class Membership {

    private final String client;
    private final String plan;
    private final int months;
    private final int visits;
    private final double price;

    /**
     * Creates a valid membership.
     *
     * @param client client name
     * @param plan membership plan
     * @param months membership duration in months
     * @param visits number of visits
     * @param price membership price
     */
    public Membership(String client, String plan, int months, int visits, double price) {
        this.client = Objects.requireNonNull(client, "Client cannot be null");
        this.plan = Objects.requireNonNull(plan, "Plan cannot be null");

        if (client.isBlank()) {
            throw new IllegalArgumentException("Client cannot be empty");
        }

        if (plan.isBlank()) {
            throw new IllegalArgumentException("Plan cannot be empty");
        }

        if (months <= 0) {
            throw new IllegalArgumentException("Months must be positive");
        }

        if (visits < 0) {
            throw new IllegalArgumentException("Visits cannot be negative");
        }

        if (price < 0 || !Double.isFinite(price)) {
            throw new IllegalArgumentException("Price must be finite and non-negative");
        }

        this.months = months;
        this.visits = visits;
        this.price = price;
    }

    /**
     * Creates a membership from a CSV line.
     *
     * @param line CSV line
     * @return parsed membership
     */
    public static Membership fromCsv(String line) {
        Objects.requireNonNull(line, "Line cannot be null");

        String[] fields = line.split(";", -1);

        if (fields.length != 5) {
            throw new IllegalArgumentException("Expected 5 fields");
        }

        try {
            String client = fields[0].trim();
            String plan = fields[1].trim();
            int months = Integer.parseInt(fields[2].trim());
            int visits = Integer.parseInt(fields[3].trim());
            double price = Double.parseDouble(fields[4].trim());

            return new Membership(client, plan, months, visits, price);

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                "Invalid numeric value", exception
            );
        }
    }

    public String getClient() {
        return client;
    }

    public String getPlan() {
        return plan;
    }

    public int getMonths() {
        return months;
    }

    public int getVisits() {
        return visits;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format(
            Locale.ROOT,
            "%s; %s; %d months; %d visits; %.2f",
            client, plan, months, visits, price
        );
    }
}
