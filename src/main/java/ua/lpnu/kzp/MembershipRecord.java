package ua.lpnu.kzp;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Flat gym membership record used for CSV persistence.
 */
public final class MembershipRecord {

    @CsvColumn("клієнт")
    private final String customer;

    @CsvColumn("тип")
    private final MembershipKind kind;

    @CsvColumn("початок")
    private final LocalDate startDate;

    @CsvColumn("кінець")
    private final LocalDate endDate;

    @CsvColumn("відвідування")
    private final int visits;

    /**
     * Creates a validated membership record.
     *
     * @param customer customer name
     * @param kind membership kind
     * @param startDate membership start date
     * @param endDate membership end date
     * @param visits number of visits
     */
    public MembershipRecord(
        String customer,
        MembershipKind kind,
        LocalDate startDate,
        LocalDate endDate,
        int visits
    ) {
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException(
                "Customer cannot be empty"
            );
        }

        Objects.requireNonNull(
            kind,
            "Kind cannot be null"
        );

        Objects.requireNonNull(
            startDate,
            "Start date cannot be null"
        );

        Objects.requireNonNull(
            endDate,
            "End date cannot be null"
        );

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                "End date cannot be before start date"
            );
        }

        if (visits < 0) {
            throw new IllegalArgumentException(
                "Visits cannot be negative"
            );
        }

        this.customer = customer;
        this.kind = kind;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visits = visits;
    }

    /**
     * Returns the customer name.
     *
     * @return customer name
     */
    public String getCustomer() {
        return customer;
    }

    /**
     * Returns the membership kind.
     *
     * @return membership kind
     */
    public MembershipKind getKind() {
        return kind;
    }

    /**
     * Returns the start date.
     *
     * @return start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the end date.
     *
     * @return end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the number of visits.
     *
     * @return number of visits
     */
    public int getVisits() {
        return visits;
    }

    /**
     * Restores a membership record from parsed CSV fields.
     *
     * <p>The field order corresponds to the deterministic
     * alphabetical field order used by CsvExporter:
     * customer, endDate, kind, startDate, visits.</p>
     *
     * @param fields parsed CSV fields
     * @return restored membership record
     * @throws DataStorageException if the fields are invalid
     */
    public static MembershipRecord fromCsvFields(
        List<String> fields
    ) throws DataStorageException {

        if (fields == null) {
            throw new DataStorageException(
                "CSV fields cannot be null"
            );
        }

        if (fields.size() != 5) {
            throw new DataStorageException(
                "Expected 5 CSV fields"
            );
        }

        try {
            String customer = fields.get(0);

            LocalDate endDate =
                LocalDate.parse(fields.get(1));

            MembershipKind kind =
                MembershipKind.valueOf(
                    fields.get(2)
                );

            LocalDate startDate =
                LocalDate.parse(fields.get(3));

            int visits =
                Integer.parseInt(
                    fields.get(4)
                );

            return new MembershipRecord(
                customer,
                kind,
                startDate,
                endDate,
                visits
            );

        } catch (IllegalArgumentException exception) {
            throw new DataStorageException(
                "Invalid membership CSV values",
                exception
            );
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof MembershipRecord other)) {
            return false;
        }

        return visits == other.visits
            && customer.equals(other.customer)
            && kind == other.kind
            && startDate.equals(other.startDate)
            && endDate.equals(other.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            customer,
            kind,
            startDate,
            endDate,
            visits
        );
    }

    @Override
    public String toString() {
        return "MembershipRecord{"
            + "customer='" + customer + '\''
            + ", kind=" + kind
            + ", startDate=" + startDate
            + ", endDate=" + endDate
            + ", visits=" + visits
            + '}';
    }
}
