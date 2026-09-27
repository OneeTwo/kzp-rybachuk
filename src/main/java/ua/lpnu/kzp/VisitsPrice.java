package ua.lpnu.kzp;

/**
 * Stores visits and price as an immutable helper value.
 *
 * @param visits number of visits
 * @param price membership price
 */
public record VisitsPrice(int visits, double price) {

    /**
     * Validates record values.
     */
    public VisitsPrice {
        if (visits < 0) {
            throw new IllegalArgumentException("Visits cannot be negative");
        }

        if (price < 0 || !Double.isFinite(price)) {
            throw new IllegalArgumentException(
                "Price must be finite and non-negative"
            );
        }
    }
}
