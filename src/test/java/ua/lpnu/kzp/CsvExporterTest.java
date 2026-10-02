package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvExporterTest {

    @TempDir
    Path tempDirectory;

    @Test
    void writesDeterministicHeader()
        throws DataStorageException, IOException {

        Path file =
            tempDirectory.resolve("memberships.csv");

        MembershipRecord record =
            new MembershipRecord(
                "Ivan",
                MembershipKind.MONTHLY,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                12
            );

        CsvExporter.write(
            file,
            MembershipRecord.class,
            List.of(record)
        );

        String csv = Files.readString(
            file,
            StandardCharsets.UTF_8
        );

        assertTrue(
            csv.startsWith(
                "клієнт,кінець,тип,початок,відвідування"
                    + System.lineSeparator()
            )
        );
    }

    @Test
    void writesMembershipValues()
        throws DataStorageException, IOException {

        Path file =
            tempDirectory.resolve("memberships.csv");

        MembershipRecord record =
            new MembershipRecord(
                "Maria",
                MembershipKind.ANNUAL,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                100
            );

        CsvExporter.write(
            file,
            MembershipRecord.class,
            List.of(record)
        );

        String csv = Files.readString(
            file,
            StandardCharsets.UTF_8
        );

        assertTrue(
            csv.contains(
                "Maria,2026-12-31,ANNUAL,"
                    + "2026-01-01,100"
            )
        );
    }

    @Test
    void escapesCommaQuoteAndNewLine()
        throws DataStorageException, IOException {

        Path file =
            tempDirectory.resolve("escaped.csv");

        MembershipRecord record =
            new MembershipRecord(
                "Ivan, \"Strong\"\nClient",
                MembershipKind.MONTHLY,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                12
            );

        CsvExporter.write(
            file,
            MembershipRecord.class,
            List.of(record)
        );

        String csv = Files.readString(
            file,
            StandardCharsets.UTF_8
        );

        assertTrue(
            csv.contains(
                "\"Ivan, \"\"Strong\"\"\nClient\""
            )
        );
    }

    @Test
    void escapeLeavesSimpleValueUnchanged() {
        assertEquals(
            "Ivan",
            CsvExporter.escape("Ivan")
        );
    }

    @Test
    void escapeDoublesQuotes() {
        assertEquals(
            "\"Ivan \"\"Strong\"\"\"",
            CsvExporter.escape(
                "Ivan \"Strong\""
            )
        );
    }

    @Test
    void writesHeaderForEmptyList()
        throws DataStorageException, IOException {

        Path file =
            tempDirectory.resolve("empty.csv");

        CsvExporter.write(
            file,
            MembershipRecord.class,
            List.of()
        );

        String csv = Files.readString(
            file,
            StandardCharsets.UTF_8
        );

        assertEquals(
            "клієнт,кінець,тип,початок,відвідування"
                + System.lineSeparator(),
            csv
        );
    }

    @Test
    void rejectsClassWithoutCsvColumns() {

        Path file =
            tempDirectory.resolve("invalid.csv");

        assertThrows(
            DataStorageException.class,
            () -> CsvExporter.write(
                file,
                String.class,
                List.of("test")
            )
        );
    }
}
