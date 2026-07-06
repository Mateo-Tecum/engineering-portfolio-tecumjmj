import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Harshad {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        File file = getValidFile(input); //getting file from user
        System.out.println(file.getName() + " successfully found.\n");

        //method for processing files
        processFile(file);}
    public static File getValidFile(Scanner input) {
        File file;

        //This while loops keeps working until a valid file is in and then it breaks
        while (true) {
            System.out.print("Enter file name: ");
            String fileName = input.nextLine();
            file = new File(fileName);

            if (file.exists()) {
                break;
            } else {
                System.out.print("File does not exist, try again: ");
            }
        }
        return file;
    }

    public static void processFile(File file) {
        int harshadCount =0;
        int notHarshadCount =0;
        int badTokensAmt = 0;

        //reading file
        try {
            Scanner fileScanner = new Scanner(file);
            System.out.println("Scanning " + file.getName() + "...");

            //checks to read each token that the file has
            while (fileScanner.hasNext()) {
                String token = fileScanner.next();
                if (isValidThreeDigitInteger(token))  {
                    int number = Integer.parseInt(token);
                    //trying out parseInt method/tool
                    if(isHarshadNumber(number)) {
                        System.out.println(number + " is a Harshad number.");
                        harshadCount++;
                    } else {
                        notHarshadCount++;
                    }
                }else {
                    badTokensAmt++;
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
            return;
        }
        printResults(file.getName(),harshadCount,notHarshadCount,badTokensAmt);
    }
    public static boolean isValidThreeDigitInteger (String token) {
        try {
            //this part of the code will convert or try to convert tokens to integers
            int number = Integer.parseInt(token);
            return number >=100 && number <= 999;
        } catch (NumberFormatException e) {
            return false;
            //this means the token is not a number
        }
    }
    public static boolean isHarshadNumber(int number ) {
        int sumOfDigits = calculateDigitSum(number);
        return sumOfDigits > 0 && number % sumOfDigits ==0;
        //not including 0 to prevent error from divide by 0 erorr
    }
    public static int calculateDigitSum(int number) {
        int sum=0;
        int temp= number;

        while(temp>0) {
            sum += temp %10;
            temp /=10;
        }
        return sum;
    }
    public static void printResults (String fileName, int harshadCount,
                                     int notHarshadCount, int badTokensAmt) {
        System.out.println("\nResults for " + fileName +"...");
        System.out.println("- Total Harshad numbers: " + harshadCount);
        System.out.println("- Total non-Harshad numbers: " + notHarshadCount);
        System.out.println("- Total bad tokens: " + badTokensAmt);
    }
}