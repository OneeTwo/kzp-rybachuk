package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IntSummaryStatistics;
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

        if (args.length > 0
            && "--help".equals(args[0])) {

            System.out.println("""
                Usage:
                  java -jar lab01.jar
                  java -jar lab01.jar --input <file> --output <file>
                  java -jar lab01.jar --version
                  java -jar lab01.jar --csv-demo
                """);

            return;
        }

        if (args.length > 0
            && "--version".equals(args[0])) {

            System.out.println("lab01 1.4.0");
            return;
        }

        if (args.length > 0
            && "--csv-demo".equals(args[0])) {

            try {
                Lab05RoundTrip.run(
                    Path.of(
                        "out",
                        "memberships.csv"
                    )
                );
            } catch (DataStorageException exception) {
                System.err.println(
                    exception.getMessage()
                );
            }

            return;
        }

        Path input =
            Path.of("data", "input.csv");

        Path output =
            Path.of("out", "report.txt");

        for (int i = 0;
             i < args.length;
             i++) {

            switch (args[i]) {

                case "--input" -> {
                    if (i + 1 < args.length) {
                        input =
                            Path.of(args[++i]);
                    }
                }

                case "--output" -> {
                    if (i + 1 < args.length) {
                        output =
                            Path.of(args[++i]);
                    }
                }

                default -> {
                    // Ignore unknown arguments.
                }
            }
        }

        try {
            List<String> lines =
                Files.readAllLines(
                    input,
                    StandardCharsets.UTF_8
                );

            Repository<Membership> repository =
                new Repository<>();

            for (int i = 0;
                 i < lines.size();
                 i++) {

                String line = lines.get(i);

                if (line.isBlank()) {
                    System.out.printf(
                        "Line %d skipped: empty line%n",
                        i + 1
                    );

                    continue;
                }

                try {
                    Membership membership =
                        Membership.fromCsv(line);

                    repository.add(
                        membership
                    );

                } catch (
                    IllegalArgumentException exception
                ) {

                    String message;

                    if (exception.getCause()
                        instanceof NumberFormatException) {

                        message =
                            "invalid number format";

                    } else if (
                        "Expected 5 fields".equals(
                            exception.getMessage()
                        )
                    ) {

                        message =
                            "expected 5 fields";

                    } else if (
                        "Client cannot be empty".equals(
                            exception.getMessage()
                        )
                            || "Plan cannot be empty".equals(
                            exception.getMessage()
                        )
                    ) {

                        message =
                            "client or plan is empty";

                    } else {

                        message =
                            "invalid numeric value";
                    }

                    System.out.printf(
                        "Line %d skipped: %s%n",
                        i + 1,
                        message
                    );
                }
            }

            List<Membership> memberships =
                repository.all();

            int validCount =
                memberships.size();

            IntSummaryStatistics visitStatistics =
                MembershipQueries
                    .visitStatistics(
                        memberships
                    );

            double averageVisits =
                visitStatistics
                    .getAverage();

            double totalRevenue =
                memberships.stream()
                    .mapToDouble(
                        Membership::getPrice
                    )
                    .sum();

            int maxMonths =
                memberships.stream()
                    .mapToInt(
                        Membership::getMonths
                    )
                    .max()
                    .orElse(0);

            boolean invalidCostPerVisit =
                memberships.stream()
                    .mapToDouble(
                        Membership::costPerVisit
                    )
                    .anyMatch(
                        cost ->
                            !Double.isFinite(cost)
                                || cost < 0
                    );

            if (invalidCostPerVisit) {
                throw new IllegalStateException(
                    "Invalid cost per visit"
                );
            }

            String report =
                String.format(
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

            System.out.print(
                report
            );

            Path parent =
                output.getParent();

            if (parent != null) {
                Files.createDirectories(
                    parent
                );
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
