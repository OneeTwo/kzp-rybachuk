package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class CsvParserTest {

    @Test
    void parsesSimpleRecord()
        throws DataStorageException {

        List<String> result =
            CsvParser.parse(
                "Ivan,MONTHLY,2026-10-01,12"
            );

        assertEquals(
            List.of(
                "Ivan",
                "MONTHLY",
                "2026-10-01",
                "12"
            ),
            result
        );
    }

    @Test
    void parsesQuotedComma()
        throws DataStorageException {

        List<String> result =
            CsvParser.parse(
                "\"Ivan, Client\",MONTHLY"
            );

        assertEquals(
            List.of(
                "Ivan, Client",
                "MONTHLY"
            ),
            result
        );
    }

    @Test
    void parsesEscapedQuote()
        throws DataStorageException {

        List<String> result =
            CsvParser.parse(
                "\"Ivan \"\"Strong\"\"\",MONTHLY"
            );

        assertEquals(
            List.of(
                "Ivan \"Strong\"",
                "MONTHLY"
            ),
            result
        );
    }

    @Test
    void parsesMultilineQuotedField()
        throws DataStorageException {

        String csv =
            "customer,kind"
                + System.lineSeparator()
                + "\"Ivan"
                + System.lineSeparator()
                + "Client\",MONTHLY";

        List<List<String>> result =
            CsvParser.parseDocument(csv);

        assertEquals(2, result.size());

        assertEquals(
            List.of(
                "customer",
                "kind"
            ),
            result.get(0)
        );

        assertEquals(
            List.of(
                "Ivan"
                    + System.lineSeparator()
                    + "Client",
                "MONTHLY"
            ),
            result.get(1)
        );
    }

    @Test
    void parsesWindowsLineSeparator()
        throws DataStorageException {

        String csv =
            "A,B\r\nC,D\r\n";

        List<List<String>> result =
            CsvParser.parseDocument(csv);

        assertEquals(
            List.of(
                List.of("A", "B"),
                List.of("C", "D")
            ),
            result
        );
    }

    @Test
    void emptyDocumentReturnsEmptyList()
        throws DataStorageException {

        assertTrue(
            CsvParser
                .parseDocument("")
                .isEmpty()
        );
    }

    @Test
    void rejectsUnclosedQuote() {

        assertThrows(
            DataStorageException.class,
            () -> CsvParser.parseDocument(
                "\"Ivan,MONTHLY"
            )
        );
    }

    @Test
    void rejectsCharactersAfterClosingQuote() {

        assertThrows(
            DataStorageException.class,
            () -> CsvParser.parseDocument(
                "\"Ivan\"abc,MONTHLY"
            )
        );
    }

    @Test
    void parseRejectsMultipleRecords() {

        assertThrows(
            DataStorageException.class,
            () -> CsvParser.parse(
                "A,B\nC,D"
            )
        );
    }
}
