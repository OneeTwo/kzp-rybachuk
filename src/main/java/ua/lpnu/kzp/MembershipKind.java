package ua.lpnu.kzp;

/**
 * Defines available gym membership kinds.
 */
public enum MembershipKind {

    MONTHLY("Monthly"),
    ANNUAL("Annual");

    private final String label;

    /**
     * Creates a membership kind.
     *
     * @param label readable membership kind label
     */
    MembershipKind(String label) {
        this.label = label;
    }

    /**
     * Returns the readable label.
     *
     * @return membership kind label
     */
    public String label() {
        return label;
    }
}
