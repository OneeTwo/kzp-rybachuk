package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Main application class for processing gym membership records.
 */
public class Main {

    /**
     * Application entry point.
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
            System.out.println("lab01 1.2.0");
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
                    // Ignore unknown arguments.
                }
            }
        }

        try {
            List<String> lines = Files.readAllLines(
                input,
                StandardCharsets.UTF_8
            );

            List<Membership> memberships = new ArrayList<>();

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.isBlank()) {
                    System.out.printf(
                        "Line %d skipped: empty line%n",
                        i + 1
                    );
                    continue;
                }

                try {
                    Membership membership = Membership.fromCsv(line);
                    memberships.add(membership);

                } catch (IllegalArgumentException exception) {
                    String message;

                    if (exception.getCause() instanceof NumberFormatException) {
                        message = "invalid number format";
                    } else if ("Expected 5 fields".equals(exception.getMessage())) {
                        message = "expected 5 fields";
                    } else if ("Client cannot be empty".equals(exception.getMessage())
                        || "Plan cannot be empty".equals(exception.getMessage())) {
                        message = "client or plan is empty";
                    } else {
                        message = "invalid numeric value";
                    }

                    System.out.printf(
                        "Line %d skipped: %s%n",
                        i + 1,
                        message
                    );
                }
            }

            int validCount = memberships.size();
            int totalVisits = 0;
            double totalRevenue = 0;
            int maxMonths = 0;

            for (Membership membership : memberships) {
                VisitsPrice visitsPrice = new VisitsPrice(
                    membership.getVisits(),
                    membership.getPrice()
                );

                double costPerVisit = membership.costPerVisit();

                if (!Double.isFinite(costPerVisit) || costPerVisit < 0) {
                    throw new IllegalStateException(
                        "Invalid cost per visit"
                    );
                }

                totalVisits += visitsPrice.visits();
                totalRevenue += visitsPrice.price();
                maxMonths = Math.max(
                    maxMonths,
                    membership.getMonths()
                );
            }

            double averageVisits = validCount == 0
                ? 0
                : (double) totalVisits / validCount;

            String report = String.format(
                Locale.ROOT,
                "Valid records: %d%n"
                    + "Average visits: %.2f%n"
                    + "Total revenue: %.2f%n"
                    + "Longest membership: %d months%n",
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

            Files.writeString(
                output,
                report,
                StandardCharsets.UTF_8
            );

        } catch (IOException exception) {
            System.err.println(
                "Failed to read input file: "
                    + exception.getMessage()
            );
        }
    }
}
