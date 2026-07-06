import java.util.Random;
import java.util.Scanner;

public class MatchingNumbers {
    public static final int NUM_DICE = 5;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Random random = new Random();
        int seed = getInt(input, "Enter a seed: ");
        random.setSeed(seed);
        int totalAmtRolls = 0;
        int[] statisticsFinal = new int[4];
        boolean runAgain;

        do {
            int numRolls = getInt(input, "Enter number of rolls: ");
            // REMOVED: System.out.println(); - this was causing extra blank line
            totalAmtRolls += numRolls;

            int[] experimentResults = performExperiment(random, numRolls);
            displayResults(experimentResults, numRolls);

            for (int i = 0; i < statisticsFinal.length; i++) {
                statisticsFinal[i] += experimentResults[i];
            }

            System.out.print("Would you like to run experiment again (Y/N)? ");
            String response = input.next();
            runAgain = response.equalsIgnoreCase("y");

        } while (runAgain);

        // REMOVED: System.out.println(); - this was causing extra blank line at end
        displayResults(statisticsFinal, totalAmtRolls);
    }

    public static int getInt(Scanner input, String scannerQuestion) {
        int value;
        System.out.print(scannerQuestion);
        while (true) {
            value = input.nextInt();
            if (value > 0) {
                return value;
            }
            System.out.print("Not a positive number, try again: ");
        }
    }

    public static int[] performExperiment(Random random, int numRolls) {
        int[] matches = new int[4];
        for (int roll = 0; roll < numRolls; roll++) {
            int[] dice = rollDice(random, NUM_DICE);
            updateMatchCounts(dice, matches);
        }
        return matches;
    }

    public static int[] rollDice(Random random, int numDice) {
        int[] dice = new int[numDice];
        for (int i = 0; i < numDice; i++) {
            dice[i] = random.nextInt(6) + 1;
        }
        return dice;
    }

    public static void updateMatchCounts(int[] dice, int[] stats) {
        int[] counts = new int[7];
        for (int die : dice) {
            counts[die]++;
        }
        boolean twoCount = false;
        boolean threeCount = false;
        boolean fourCount = false;  // FIXED: fourCountt → fourCount
        boolean fiveCount = false;

        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 2) twoCount = true;
            if (counts[i] == 3) threeCount = true;
            if (counts[i] == 4) fourCount = true;  // FIXED: fourCountt → fourCount
            if (counts[i] == 5) fiveCount = true;
        }
        if (threeCount) stats[0]++;
        if (fourCount) stats[1]++;  // FIXED: fourCountt → fourCount
        if (fiveCount) stats[2]++;
        if (threeCount && twoCount) stats[3]++;
    }

    public static void displayResults(int[] pairs, int totalRolls) {
        String rollsOrRoll = totalRolls == 1 ? "roll" : "rolls";
        System.out.printf("After %d %s:\n", totalRolls, rollsOrRoll);
        String[] types = {
                "Three dice matching", "Four dice matching", "Five dice matching",
                "Three and two dice matching"
        };
        for (int i = 0; i < pairs.length; i++) {
            double percentage = totalRolls > 0 ?
                    (double) pairs[i] / totalRolls * 100 : 0.0;
            String times = pairs[i] == 1 ? "time" : "times";
            System.out.printf("- %s: %d %s (%.1f%%)\n",
                    types[i], pairs[i], times, percentage);
        }
        // KEEP this println for spacing between experiments, but removed the extra ones
    }
}