package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldCalculateGymStatisticsAndWriteExactReport() throws Exception {
        RunResult result = runWithInput(
            "Іван Петренко;Standard;3;24;1500.00\n"
                + "Марія Коваль;Premium;12;110;6500.00\n"
                + "Олег Бондар;Basic;1;8;700.00\n"
        );

        String expected = "Valid records: 3" + System.lineSeparator()
            + "Average visits: 47.33" + System.lineSeparator()
            + "Total revenue: 8700.00" + System.lineSeparator()
            + "Longest membership: 12 months" + System.lineSeparator();

        assertEquals(expected, result.report());
        assertEquals(expected, result.console());
    }

    @Test
    void shouldSkipEmptyLine() throws Exception {
        RunResult result = runWithInput(
            "Ivan;Standard;3;24;1500.00\n\n"
                + "Maria;Premium;12;110;6500.00\n"
        );

        assertTrue(result.console().contains("Line 2 skipped: empty line"));
        assertTrue(result.report().contains("Valid records: 2"));
    }

    @Test
    void shouldRejectWrongFieldCount() throws Exception {
        RunResult result = runWithInput(
            "Ivan;Standard;3;24\n"
                + "Maria;Premium;12;110;6500.00\n"
        );

        assertTrue(result.console().contains("Line 1 skipped: expected 5 fields"));
        assertTrue(result.report().contains("Valid records: 1"));
    }

    @Test
    void shouldRejectEmptyClientOrPlan() throws Exception {
        RunResult result = runWithInput(
            ";Standard;3;24;1500.00\n"
                + "Ivan;;3;24;1500.00\n"
                + "Maria;Premium;12;110;6500.00\n"
        );

        assertTrue(result.console().contains("Line 1 skipped: client or plan is empty"));
        assertTrue(result.console().contains("Line 2 skipped: client or plan is empty"));
        assertTrue(result.report().contains("Valid records: 1"));
    }

    @Test
    void shouldRejectInvalidNumberFormat() throws Exception {
        RunResult result = runWithInput(
            "Ivan;Standard;3;abc;1500.00\n"
                + "Maria;Premium;12;110;6500.00\n"
        );

        assertTrue(result.console().contains("Line 1 skipped: invalid number format"));
        assertTrue(result.report().contains("Valid records: 1"));
    }

    @Test
    void shouldRejectInvalidNumericBoundaries() throws Exception {
        RunResult result = runWithInput(
            "A;Standard;0;10;100.00\n"
                + "B;Standard;1;-1;100.00\n"
                + "C;Standard;1;10;-0.01\n"
                + "D;Standard;1;10;100.00\n"
        );

        assertTrue(result.console().contains("Line 1 skipped: invalid numeric value"));
        assertTrue(result.console().contains("Line 2 skipped: invalid numeric value"));
        assertTrue(result.console().contains("Line 3 skipped: invalid numeric value"));
        assertTrue(result.report().contains("Valid records: 1"));
    }

    @Test
    void shouldRejectNonFinitePrice() throws Exception {
        RunResult result = runWithInput(
            "A;Standard;1;10;NaN\n"
                + "B;Standard;1;10;Infinity\n"
                + "C;Standard;1;10;100.00\n"
        );

        assertTrue(result.console().contains("Line 1 skipped: invalid numeric value"));
        assertTrue(result.console().contains("Line 2 skipped: invalid numeric value"));
        assertTrue(result.report().contains("Valid records: 1"));
    }

    @Test
    void shouldReportZerosWhenThereAreNoValidRecords() throws Exception {
        RunResult result = runWithInput(
            "A;Standard;-1;10;100.00\n"
                + "B;Standard;1;abc;100.00\n"
        );

        String expected = "Valid records: 0" + System.lineSeparator()
            + "Average visits: 0.00" + System.lineSeparator()
            + "Total revenue: 0.00" + System.lineSeparator()
            + "Longest membership: 0 months" + System.lineSeparator();

        assertEquals(expected, result.report());
    }

    @Test
    void shouldWorkWithUkrainianTextAndFileNames() throws Exception {
        Path input = tempDir.resolve("вхід.csv");
        Path output = tempDir.resolve("звіти").resolve("звіт.txt");

        Files.writeString(
            input,
            "Іван Петренко;Преміум;1;5;700.00\n",
            StandardCharsets.UTF_8
        );

        captureStdOut(() -> Main.main(new String[] {
            "--input", input.toString(),
            "--output", output.toString()
        }));

        String report = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(Files.exists(output));
        assertTrue(report.contains("Valid records: 1"));
        assertTrue(report.contains("Average visits: 5.00"));
    }

    @Test
    void shouldCreateMissingOutputDirectory() throws Exception {
        Path input = tempDir.resolve("input.csv");
        Path output = tempDir.resolve("nested").resolve("reports").resolve("report.txt");

        Files.writeString(
            input,
            "Ivan;Standard;1;5;100.00\n",
            StandardCharsets.UTF_8
        );

        captureStdOut(() -> Main.main(new String[] {
            "--input", input.toString(),
            "--output", output.toString()
        }));

        assertTrue(Files.exists(output));
    }

    @Test
    void shouldPrintHelpForBothSupportedForms() {
        String doubleDash = captureStdOut(() -> Main.main(new String[] {"--help"}));
        String singleDash = captureStdOut(() -> Main.main(new String[] {"-help"}));

        assertTrue(doubleDash.contains("Usage:"));
        assertTrue(singleDash.contains("Usage:"));
        assertTrue(doubleDash.contains("--input <file> --output <file>"));
    }

    @Test
    void shouldPrintVersion() {
        String console = captureStdOut(() -> Main.main(new String[] {"--version"}));
        assertEquals("lab01 1.0.0" + System.lineSeparator(), console);
    }

    private RunResult runWithInput(String content) throws Exception {
        Path input = tempDir.resolve("input.csv");
        Path output = tempDir.resolve("report.txt");

        Files.writeString(input, content, StandardCharsets.UTF_8);

        String console = captureStdOut(() -> Main.main(new String[] {
            "--input", input.toString(),
            "--output", output.toString()
        }));

        String report = Files.readString(output, StandardCharsets.UTF_8);
        return new RunResult(console, report);
    }

    private String captureStdOut(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (PrintStream stream = new PrintStream(
            buffer,
            true,
            StandardCharsets.UTF_8
        )) {
            System.setOut(stream);
            action.run();
        } finally {
            System.setOut(originalOut);
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    private record RunResult(String console, String report) {
    }
}
