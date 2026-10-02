package ua.lpnu.kzp;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Reads gym membership records from UTF-8 CSV files.
 */
public final class MembershipCsvReader {

    private MembershipCsvReader() {
    }

    /**
     * Reads membership records from a CSV file.
     *
     * @param path CSV file path
     * @return restored records
     * @throws DataStorageException if reading or parsing fails
     */
    public static List<MembershipRecord> read(
        Path path
    ) throws DataStorageException {

        final String text;

        try {
            text = Files.readString(
                path,
                StandardCharsets.UTF_8
            );
        } catch (IOException exception) {
            throw new DataStorageException(
                "Cannot read CSV: " + path,
                exception
            );
        }

        List<List<String>> rows =
            CsvParser.parseDocument(text);

        if (rows.isEmpty()) {
            return List.of();
        }

        List<String> expectedHeader =
            expectedHeader();

        if (!rows.get(0).equals(expectedHeader)) {
            throw new DataStorageException(
                "Invalid CSV header"
            );
        }

        List<MembershipRecord> records =
            new ArrayList<>();

        for (int index = 1;
             index < rows.size();
             index++) {

            try {
                records.add(
                    MembershipRecord.fromCsvFields(
                        rows.get(index)
                    )
                );
            } catch (DataStorageException exception) {
                throw new DataStorageException(
                    "Invalid CSV record "
                        + index,
                    exception
                );
            }
        }

        return List.copyOf(records);
    }

    private static List<String> expectedHeader() {
        return Arrays.stream(
                MembershipRecord.class
                    .getDeclaredFields()
            )
            .filter(field ->
                field.isAnnotationPresent(
                    CsvColumn.class
                )
            )
            .sorted(
                Comparator.comparing(
                    Field::getName
                )
            )
            .map(field ->
                field.getAnnotation(
                    CsvColumn.class
                ).value()
            )
            .toList();
    }
}
