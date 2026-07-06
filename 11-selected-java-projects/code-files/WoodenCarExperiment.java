import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

public class WoodenCarExperiment {

    // Car iteration data structure
    static class CarIteration {
        int iteration;
        String description;
        double time; // in seconds
        double velocity; // in ft/s

        public CarIteration(int iteration, String description, double time) {
            this.iteration = iteration;
            this.description = description;
            this.time = time;
            this.velocity = 10.0 / time; // 10 feet divided by time
        }
    }

    // Statistics class to hold analysis results
    static class ExperimentStats {
        double averageTime;
        double averageVelocity;
        double bestTime;
        double worstTime;
        double timeStdDev;
        double velocityStdDev;
        double bestFitSlope;
        double bestFitIntercept;
        double correlationCoefficient;

        public void printStats() {
            DecimalFormat df = new DecimalFormat("#.###");
            System.out.println("\n=== EXPERIMENT STATISTICS ===");
            System.out.println("Average Time: " + df.format(averageTime) + " seconds");
            System.out.println("Average Velocity: " + df.format(averageVelocity) + " ft/s");
            System.out.println("Best (Fastest) Time: " + df.format(bestTime) + " seconds");
            System.out.println("Worst (Slowest) Time: " + df.format(worstTime) + " seconds");
            System.out.println("Time Standard Deviation: " + df.format(timeStdDev) + " seconds");
            System.out.println("Velocity Standard Deviation: " + df.format(velocityStdDev) + " ft/s");
            System.out.println("Correlation Coefficient (time vs iteration): " + df.format(correlationCoefficient));
            System.out.println("Best Fit Line: Time = " + df.format(bestFitSlope) + " * Iteration + " + df.format(bestFitIntercept));
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== WOODEN CAR EXPERIMENT ANALYSIS ===");
        System.out.println("Car traverses 10 feet distance");
        System.out.println("=====================================");

        // Get number of iterations
        System.out.print("Enter number of car iterations to test: ");
        int numIterations = scanner.nextInt();
        scanner.nextLine(); // consume newline

        List<CarIteration> iterations = new ArrayList<>();

        // Collect data for each iteration
        for (int i = 1; i <= numIterations; i++) {
            System.out.println("\n--- Iteration " + i + " ---");

            System.out.print("Enter description of car modifications: ");
            String description = scanner.nextLine();

            System.out.print("Enter time taken to traverse 10 feet (in seconds): ");
            double time = scanner.nextDouble();
            scanner.nextLine(); // consume newline

            // Validate time
            while (time <= 0) {
                System.out.print("Time must be positive. Please re-enter: ");
                time = scanner.nextDouble();
                scanner.nextLine();
            }

            iterations.add(new CarIteration(i, description, time));
        }

        // Add first and final iterations as specified
        if (iterations.size() > 0) {
            CarIteration first = iterations.get(0);
            CarIteration last = iterations.get(iterations.size() - 1);

            System.out.println("\n=== KEY ITERATIONS ===");
            System.out.println("First Iteration: " + first.description);
            System.out.println("  Time: " + first.time + " seconds, Velocity: " +
                    String.format("%.3f", first.velocity) + " ft/s");
            System.out.println("Final Iteration: " + last.description);
            System.out.println("  Time: " + last.time + " seconds, Velocity: " +
                    String.format("%.3f", last.velocity) + " ft/s");

            double improvement = ((first.time - last.time) / first.time) * 100;
            System.out.printf("Improvement: %.2f%% faster%n", improvement);
        }

        // Analyze the data
        ExperimentStats stats = analyzeData(iterations);

        // Print the table
        printResultsTable(iterations);

        // Print statistics
        stats.printStats();

        // Generate chart data for plotting
        generateChartData(iterations);

        // Export data to CSV
        exportToCSV(iterations, stats);

        scanner.close();

        System.out.println("\n=== ANALYSIS COMPLETE ===");
        System.out.println("Data has been exported to 'car_experiment_data.csv'");
        System.out.println("Chart data has been generated in 'chart_data.txt'");
    }

    private static ExperimentStats analyzeData(List<CarIteration> iterations) {
        ExperimentStats stats = new ExperimentStats();
        int n = iterations.size();

        if (n == 0) return stats;

        // Calculate basic statistics
        double totalTime = 0;
        double totalVelocity = 0;
        double bestTime = Double.MAX_VALUE;
        double worstTime = Double.MIN_VALUE;

        for (CarIteration car : iterations) {
            totalTime += car.time;
            totalVelocity += car.velocity;
            if (car.time < bestTime) bestTime = car.time;
            if (car.time > worstTime) worstTime = car.time;
        }

        stats.averageTime = totalTime / n;
        stats.averageVelocity = totalVelocity / n;
        stats.bestTime = bestTime;
        stats.worstTime = worstTime;

        // Calculate standard deviations
        double timeVariance = 0;
        double velocityVariance = 0;

        for (CarIteration car : iterations) {
            timeVariance += Math.pow(car.time - stats.averageTime, 2);
            velocityVariance += Math.pow(car.velocity - stats.averageVelocity, 2);
        }

        stats.timeStdDev = Math.sqrt(timeVariance / n);
        stats.velocityStdDev = Math.sqrt(velocityVariance / n);

        // Calculate linear regression (best fit line) for time vs iteration
        if (n > 1) {
            double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;

            for (CarIteration car : iterations) {
                double x = car.iteration;
                double y = car.time;
                sumX += x;
                sumY += y;
                sumXY += x * y;
                sumX2 += x * x;
                sumY2 += y * y;
            }

            // Slope (m) for best fit line: y = mx + b
            stats.bestFitSlope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);

            // Intercept (b)
            stats.bestFitIntercept = (sumY - stats.bestFitSlope * sumX) / n;

            // Correlation coefficient (r)
            stats.correlationCoefficient = (n * sumXY - sumX * sumY) /
                    Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));
        }

        return stats;
    }

    private static void printResultsTable(List<CarIteration> iterations) {
        System.out.println("\n=== EXPERIMENT RESULTS TABLE ===");
        System.out.println("==================================================================");
        System.out.printf("%-10s %-25s %-12s %-12s%n",
                "Iteration", "Description", "Time (s)", "Velocity (ft/s)");
        System.out.println("==================================================================");

        DecimalFormat df = new DecimalFormat("#.###");

        for (CarIteration car : iterations) {
            System.out.printf("%-10d %-25s %-12s %-12s%n",
                    car.iteration,
                    car.description.length() > 24 ? car.description.substring(0, 24) + "..." : car.description,
                    df.format(car.time),
                    df.format(car.velocity));
        }
        System.out.println("==================================================================");
    }

    private static void generateChartData(List<CarIteration> iterations) {
        try (PrintWriter writer = new PrintWriter("chart_data.txt")) {
            writer.println("Iteration,Time(s),Velocity(ft/s),BestFitTime");

            // Calculate best fit line values for each iteration
            double slope = 0, intercept = 0;
            if (iterations.size() > 1) {
                int n = iterations.size();
                double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

                for (CarIteration car : iterations) {
                    double x = car.iteration;
                    double y = car.time;
                    sumX += x;
                    sumY += y;
                    sumXY += x * y;
                    sumX2 += x * x;
                }

                slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
                intercept = (sumY - slope * sumX) / n;
            }

            // Write data points
            for (CarIteration car : iterations) {
                double bestFitTime = slope * car.iteration + intercept;
                writer.printf("%d,%.3f,%.3f,%.3f%n",
                        car.iteration, car.time, car.velocity, bestFitTime);
            }

            writer.println("\n=== CHART INSTRUCTIONS ===");
            writer.println("1. Copy the data above into Excel or Google Sheets");
            writer.println("2. Create a scatter plot for Iteration vs Time");
            writer.println("3. Add a trendline (linear) to see the best fit line");
            writer.println("4. For velocity, create a separate line chart");

        } catch (IOException e) {
            System.out.println("Error generating chart data: " + e.getMessage());
        }
    }

    private static void exportToCSV(List<CarIteration> iterations, ExperimentStats stats) {
        try (PrintWriter writer = new PrintWriter("car_experiment_data.csv")) {
            // Write header
            writer.println("Iteration,Description,Time(s),Velocity(ft/s)");

            // Write iteration data
            for (CarIteration car : iterations) {
                writer.printf("%d,\"%s\",%.3f,%.3f%n",
                        car.iteration, car.description, car.time, car.velocity);
            }

            // Write statistics section
            writer.println("\nSTATISTICS");
            writer.println("Average Time," + stats.averageTime);
            writer.println("Average Velocity," + stats.averageVelocity);
            writer.println("Best Time," + stats.bestTime);
            writer.println("Worst Time," + stats.worstTime);
            writer.println("Time Standard Deviation," + stats.timeStdDev);
            writer.println("Velocity Standard Deviation," + stats.velocityStdDev);
            writer.println("Best Fit Slope," + stats.bestFitSlope);
            writer.println("Best Fit Intercept," + stats.bestFitIntercept);
            writer.println("Correlation Coefficient," + stats.correlationCoefficient);

            // Write regression equation
            writer.println("\nREGRESSION EQUATION");
            writer.println("Time = " + stats.bestFitSlope + " * Iteration + " + stats.bestFitIntercept);

            // Write predictions for next iterations
            if (iterations.size() > 0) {
                writer.println("\nPREDICTIONS FOR NEXT ITERATIONS");
                int lastIteration = iterations.get(iterations.size() - 1).iteration;
                for (int i = 1; i <= 3; i++) {
                    double predictedTime = stats.bestFitSlope * (lastIteration + i) + stats.bestFitIntercept;
                    writer.println("Iteration " + (lastIteration + i) + " predicted time," +
                            String.format("%.3f", predictedTime) + " seconds");
                }
            }

        } catch (IOException e) {
            System.out.println("Error exporting to CSV: " + e.getMessage());
        }
    }
}