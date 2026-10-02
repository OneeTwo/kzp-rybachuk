package ua.lpnu.kzp;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Exports objects with {@link CsvColumn} fields to CSV.
 */
public final class CsvExporter {

    private CsvExporter() {
    }

    /**
     * Writes annotated object fields to a UTF-8 CSV file.
     *
     * @param path output CSV path
     * @param type exported object class
     * @param records objects to export
     * @param <T> exported object type
     * @throws DataStorageException if reflection or file writing fails
     */
    public static <T> void write(
        Path path,
        Class<T> type,
        List<? extends T> records
    ) throws DataStorageException {

        Objects.requireNonNull(path, "Path cannot be null");
        Objects.requireNonNull(type, "Type cannot be null");
        Objects.requireNonNull(records, "Records cannot be null");

        List<Field> fields = annotatedFields(type);

        if (fields.isEmpty()) {
            throw new DataStorageException(
                "Class has no CSV fields"
            );
        }

        for (Field field : fields) {
            try {
                if (!field.trySetAccessible()) {
                    throw new DataStorageException(
                        "Cannot access field: "
                            + field.getName()
                    );
                }
            } catch (RuntimeException exception) {
                throw new DataStorageException(
                    "Cannot access field: "
                        + field.getName(),
                    exception
                );
            }
        }

        String header = fields.stream()
            .map(field ->
                escape(
                    field.getAnnotation(
                        CsvColumn.class
                    ).value()
                )
            )
            .collect(Collectors.joining(","));

        StringBuilder csv = new StringBuilder();

        csv.append(header)
            .append(System.lineSeparator());

        for (T record : records) {
            if (record == null) {
                throw new DataStorageException(
                    "CSV record cannot be null"
                );
            }

            List<String> values = new ArrayList<>();

            for (Field field : fields) {
                try {
                    values.add(
                        escape(field.get(record))
                    );
                } catch (IllegalAccessException exception) {
                    throw new DataStorageException(
                        "Cannot read field: "
                            + field.getName(),
                        exception
                    );
                }
            }

            csv.append(
                String.join(",", values)
            );

            csv.append(
                System.lineSeparator()
            );
        }

        try {
            Path parent =
                path.toAbsolutePath().getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(
                path,
                csv.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            );

        } catch (IOException exception) {
            throw new DataStorageException(
                "Cannot write CSV: " + path,
                exception
            );
        }
    }

    /**
     * Returns annotated fields in deterministic order.
     *
     * @param type object type
     * @return annotated fields
     */
    private static List<Field> annotatedFields(
        Class<?> type
    ) {
        List<Field> fields = new ArrayList<>();

        Class<?> current = type;

        while (current != null
            && current != Object.class) {

            for (Field field :
                current.getDeclaredFields()) {

                if (field.isAnnotationPresent(
                    CsvColumn.class
                )) {
                    fields.add(field);
                }
            }

            current = current.getSuperclass();
        }

        fields.sort(
            Comparator.comparing(Field::getName)
        );

        return List.copyOf(fields);
    }

    /**
     * Escapes a value according to CSV rules.
     *
     * @param value field value
     * @return escaped CSV value
     */
    static String escape(Object value) {
        String text =
            Objects.toString(value, "");

        boolean quoted =
            text.contains(",")
                || text.contains("\"")
                || text.contains("\r")
                || text.contains("\n");

        if (!quoted) {
            return text;
        }

        return "\""
            + text.replace("\"", "\"\"")
            + "\"";
    }
}
