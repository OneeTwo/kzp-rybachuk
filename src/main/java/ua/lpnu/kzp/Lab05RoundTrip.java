package ua.lpnu.kzp;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * Demonstrates LAB_05 CSV export and import round-trip.
 */
public final class Lab05RoundTrip {

    private Lab05RoundTrip() {
    }

    /**
     * Creates sample records, exports them to CSV and reads them back.
     *
     * @param path output CSV path
     * @throws DataStorageException if export or import fails
     */
    public static void run(
        Path path
    ) throws DataStorageException {

        Repository<MembershipRecord> repository =
            new Repository<>();

        repository.add(
            new MembershipRecord(
                "Ivan Petrenko",
                MembershipKind.MONTHLY,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                12
            )
        );

        repository.add(
            new MembershipRecord(
                "Maria Koval",
                MembershipKind.ANNUAL,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                105
            )
        );

        repository.add(
            new MembershipRecord(
                "Oleh Bondar",
                MembershipKind.MONTHLY,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                8
            )
        );

        repository.add(
            new MembershipRecord(
                "Anna Melnyk",
                MembershipKind.ANNUAL,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2027, 1, 31),
                80
            )
        );

        repository.add(
            new MembershipRecord(
                "Taras, \"Strong\"\nClient",
                MembershipKind.MONTHLY,
                LocalDate.of(2026, 10, 5),
                LocalDate.of(2026, 11, 5),
                15
            )
        );

        List<MembershipRecord> original =
            repository.all();

        CsvExporter.write(
            path,
            MembershipRecord.class,
            original
        );

        List<MembershipRecord> restored =
            MembershipCsvReader.read(path);

        boolean equal =
            original.equals(restored);

        System.out.println(
            "CSV file: " + path
        );

        System.out.println(
            "Original records: "
                + original.size()
        );

        System.out.println(
            "Restored records: "
                + restored.size()
        );

        System.out.println(
            "Round-trip equal: "
                + equal
        );

        if (!equal) {
            throw new DataStorageException(
                "Round-trip comparison failed"
            );
        }
    }
}
