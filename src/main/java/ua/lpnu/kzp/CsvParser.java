package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses CSV text including quoted and multiline fields.
 */
public final class CsvParser {

    private CsvParser() {
    }

    /**
     * Parses exactly one CSV record.
     *
     * @param line CSV record text
     * @return parsed fields
     * @throws DataStorageException if the text contains
     *         zero or more than one record
     */
    public static List<String> parse(
        String line
    ) throws DataStorageException {

        List<List<String>> records =
            parseDocument(line);

        if (records.size() != 1) {
            throw new DataStorageException(
                "Expected exactly one CSV record"
            );
        }

        return records.get(0);
    }

    /**
     * Parses a complete CSV document.
     *
     * @param text complete CSV text
     * @return parsed records
     * @throws DataStorageException if CSV quoting is invalid
     */
    public static List<List<String>> parseDocument(
        String text
    ) throws DataStorageException {

        if (text == null) {
            throw new DataStorageException(
                "CSV document cannot be null"
            );
        }

        if (text.isEmpty()) {
            return List.of();
        }

        List<List<String>> records =
            new ArrayList<>();

        List<String> fields =
            new ArrayList<>();

        StringBuilder current =
            new StringBuilder();

        boolean quoted = false;
        boolean quoteClosed = false;

        for (int index = 0;
             index < text.length();
             index++) {

            char symbol = text.charAt(index);

            if (symbol == '"'
                && !quoted
                && !quoteClosed
                && current.length() == 0) {

                quoted = true;

            } else if (symbol == '"'
                && quoted
                && index + 1 < text.length()
                && text.charAt(index + 1) == '"') {

                current.append('"');
                index++;

            } else if (symbol == '"'
                && quoted) {

                quoted = false;
                quoteClosed = true;

            } else if (symbol == ','
                && !quoted) {

                fields.add(current.toString());

                current.setLength(0);
                quoteClosed = false;

            } else if ((symbol == '\r'
                || symbol == '\n')
                && !quoted) {

                fields.add(current.toString());

                records.add(
                    List.copyOf(fields)
                );

                fields = new ArrayList<>();

                current.setLength(0);
                quoteClosed = false;

                if (symbol == '\r'
                    && index + 1 < text.length()
                    && text.charAt(index + 1)
                    == '\n') {

                    index++;
                }

            } else {
                if (quoteClosed) {
                    throw new DataStorageException(
                        "Unexpected character "
                            + "after closing CSV quote"
                    );
                }

                current.append(symbol);
            }
        }

        if (quoted) {
            throw new DataStorageException(
                "Unclosed CSV quote"
            );
        }

        if (!fields.isEmpty()
            || current.length() > 0) {

            fields.add(current.toString());

            records.add(
                List.copyOf(fields)
            );
        }

        return List.copyOf(records);
    }
}
