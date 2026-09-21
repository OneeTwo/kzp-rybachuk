package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Processes gym membership records from a CSV file
 * and generates a statistical report.
 */
public class Main {

    /**
     * Entry point of the application.
     * Supports custom input and output paths, help, and version arguments.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0 && "--help".equals(args[0])) {
            System.out.println("""
            Usage:
              java -jar lab01.jar
              java -jar lab01.jar --input <file> --output <file>
              java -jar lab01.jar --version
            """);
            return;
        }

        if (args.length > 0 && "--version".equals(args[0])) {
            System.out.println("lab01 1.0.0");
            return;
        }

        Path input = Path.of("data", "input.csv");
        Path output = Path.of("out", "report.txt");

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--input" -> {
                    if (i + 1 < args.length) {
                        input = Path.of(args[++i]);
                    }
                }
                case "--output" -> {
                    if (i + 1 < args.length) {
                        output = Path.of(args[++i]);
                    }
                }
                default -> {
                    // Ignore unknown arguments
                }
            }
        }


        try {
            List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);

            int validCount = 0;
            int totalVisits = 0;
            double totalRevenue = 0;
            int maxMonths = 0;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.isBlank()) {
                    System.out.printf("Line %d skipped: empty line%n", i + 1);
                    continue;
                }

                String[] fields = line.split(";", -1);

                if (fields.length != 5) {
                    System.out.printf(
                        "Line %d skipped: expected 5 fields%n",
                        i + 1
                    );
                    continue;
                }

                String client = fields[0];
                String plan = fields[1];

                if (client.isBlank() || plan.isBlank()) {
                    System.out.printf(
                        "Line %d skipped: client or plan is empty%n",
                        i + 1
                    );
                    continue;
                }

                try {
                    int months = Integer.parseInt(fields[2]);
                    int visits = Integer.parseInt(fields[3]);
                    double price = Double.parseDouble(fields[4]);

                    if (months <= 0 || visits < 0 || price < 0) {
                        System.out.printf(
                            "Line %d skipped: invalid numeric value%n",
                            i + 1
                        );
                        continue;
                    }

                    validCount++;
                    totalVisits += visits;
                    totalRevenue += price;
                    maxMonths = Math.max(maxMonths, months);

                } catch (NumberFormatException e) {
                    System.out.printf(
                        "Line %d skipped: invalid number format%n",
                        i + 1
                    );
                }
            }

            double averageVisits = validCount == 0
                ? 0
                : (double) totalVisits / validCount;

            String report = String.format(
                Locale.ROOT,
                "Valid records: %d%n" +
                    "Average visits: %.2f%n" +
                    "Total revenue: %.2f%n" +
                    "Longest membership: %d months%n",
                validCount,
                averageVisits,
                totalRevenue,
                maxMonths
            );

            System.out.print(report);

            Path parent = output.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(output, report, StandardCharsets.UTF_8);            Files.writeString(output, report, StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.err.println("Failed to read input file: " + e.getMessage());
        }
    }
}
