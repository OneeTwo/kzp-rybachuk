package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MembershipRoundTripTest {

    @TempDir
    Path tempDirectory;

    @Test
    void roundTripRestoresEqualRecords()
        throws DataStorageException {

        List<MembershipRecord> original =
            List.of(
                new MembershipRecord(
                    "Ivan",
                    MembershipKind.MONTHLY,
                    LocalDate.of(2026, 10, 1),
                    LocalDate.of(2026, 10, 31),
                    12
                ),
                new MembershipRecord(
                    "Maria",
                    MembershipKind.ANNUAL,
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 12, 31),
                    105
                ),
                new MembershipRecord(
                    "Oleh",
                    MembershipKind.MONTHLY,
                    LocalDate.of(2026, 9, 1),
                    LocalDate.of(2026, 9, 30),
                    8
                ),
                new MembershipRecord(
                    "Anna",
                    MembershipKind.ANNUAL,
                    LocalDate.of(2026, 2, 1),
                    LocalDate.of(2027, 1, 31),
                    80
                ),
                new MembershipRecord(
                    "Taras, \"Strong\"\nClient",
                    MembershipKind.MONTHLY,
                    LocalDate.of(2026, 10, 5),
                    LocalDate.of(2026, 11, 5),
                    15
                )
            );

        Path file =
            tempDirectory.resolve(
                "memberships.csv"
            );

        CsvExporter.write(
            file,
            MembershipRecord.class,
            original
        );

        List<MembershipRecord> restored =
            MembershipCsvReader.read(file);

        assertEquals(original, restored);
    }

    @Test
    void emptyFileReturnsEmptyList()
        throws DataStorageException {

        Path file =
            tempDirectory.resolve("empty.csv");

        try {
            java.nio.file.Files.writeString(
                file,
                ""
            );
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        assertTrue(
            MembershipCsvReader
                .read(file)
                .isEmpty()
        );
    }

    @Test
    void invalidFieldCountIsRejected() {

        DataStorageException exception =
            assertThrows(
                DataStorageException.class,
                () ->
                    MembershipRecord.fromCsvFields(
                        List.of(
                            "Ivan",
                            "2026-10-31"
                        )
                    )
            );

        assertEquals(
            "Expected 5 CSV fields",
            exception.getMessage()
        );
    }

    @Test
    void invalidNumberPreservesCause() {

        DataStorageException exception =
            assertThrows(
                DataStorageException.class,
                () ->
                    MembershipRecord.fromCsvFields(
                        List.of(
                            "Ivan",
                            "2026-10-31",
                            "MONTHLY",
                            "2026-10-01",
                            "abc"
                        )
                    )
            );

        assertInstanceOf(
            NumberFormatException.class,
            exception.getCause()
        );
    }

    @Test
    void invalidEnumPreservesCause() {

        DataStorageException exception =
            assertThrows(
                DataStorageException.class,
                () ->
                    MembershipRecord.fromCsvFields(
                        List.of(
                            "Ivan",
                            "2026-10-31",
                            "UNKNOWN",
                            "2026-10-01",
                            "10"
                        )
                    )
            );

        assertInstanceOf(
            IllegalArgumentException.class,
            exception.getCause()
        );
    }

    @Test
    void fileErrorPreservesIOException() {

        Path missingFile =
            tempDirectory.resolve(
                "missing.csv"
            );

        DataStorageException exception =
            assertThrows(
                DataStorageException.class,
                () ->
                    MembershipCsvReader.read(
                        missingFile
                    )
            );

        assertInstanceOf(
            IOException.class,
            exception.getCause()
        );
    }
}
