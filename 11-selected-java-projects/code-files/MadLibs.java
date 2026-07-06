import java.util.Scanner;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;

public class MadLibs {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String[] stringFileNames = new String[4];
        String[] stringFileTypes = {"adjectives", "animals", "objects", "answers"};

        for (int i = 0; i < 4; i++) {
            stringFileNames[i] = getValidFileName(scanner, stringFileTypes[i]);
        }

        String[][] wordArrays = new String[4][];
        for (int i = 0; i < 4; i++) {
            wordArrays[i] = readFiles(stringFileNames[i]);
        }

        int seed = getSeed(scanner);
        Random random = new Random(seed);
        int AmtOfSentences = getValidSentenceAmt(scanner);
        printSentences(wordArrays, random, AmtOfSentences);
    }

    public static String getValidFileName(Scanner scanner, String stringFileType) {
        while (true) {
            System.out.print("Enter file name for " + stringFileType + ": ");
            String stringFileName = scanner.nextLine();
            File file = new File(stringFileName);
            if (file.exists()) {
                System.out.println(stringFileName + " successfully found.");  // FIX: added space
                return stringFileName;
            } else {
                System.out.print("File does not exist, try again: ");
            }
        }
    }

    public static String[] readFiles(String stringFileName) throws FileNotFoundException {
        File file = new File(stringFileName);
        Scanner fileScanner = new Scanner(file);
        int count = Integer.parseInt(fileScanner.nextLine());
        String[] words = new String[count];
        for (int i = 0; i < count; i++) {
            words[i] = fileScanner.nextLine();
        }
        fileScanner.close();  // DON'T FORGET THIS!
        return words;
    }

    public static int getSeed(Scanner scanner) {
        while (true) {
            System.out.print("Enter a seed: ");  // FIX: changed to print
            int seed = Integer.parseInt(scanner.nextLine());
            if (seed > 0) {
                return seed;
            } else {
                System.out.print("Not a positive number, try again: ");  // FIX: consistent print
            }
        }
    }

    public static int getValidSentenceAmt(Scanner scanner) {
        while (true) {
            System.out.print("Enter number of sentences to produce: ");
            int count = Integer.parseInt(scanner.nextLine());
            if (count >= 1 && count <= 50) {
                return count;
            } else {
                System.out.print("Not between 1 and 50, try again: ");  // FIX: changed to print
            }
        }
    }

    public static void printSentences(String[][] wordArrays, Random random, int count) {
        if (count == 1) {
            System.out.println("\nHere is the sentence:\n");
        } else {
            System.out.println("\nHere are the sentences:\n");
        }

        for (int i = 0; i < count; i++) {
            String adjective = wordArrays[0][random.nextInt(wordArrays[0].length)];
            String animal = wordArrays[1][random.nextInt(wordArrays[1].length)];
            String object = wordArrays[2][random.nextInt(wordArrays[2].length)];
            String answer = wordArrays[3][random.nextInt(wordArrays[3].length)];
            System.out.printf("Sentence %2d: Six %s %s sit in the %s. Do they fit? %s%n",
                    i + 1, adjective, animal, object, answer);
        }
    }
}