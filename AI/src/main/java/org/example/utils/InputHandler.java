package org.example.utils;

import java.util.Scanner;

public class InputHandler {
    private static Scanner scanner = new Scanner(System.in);

    public static double[] getInputArray(String prompt) {
        // prompt is the prompt information echoed to the user
        System.out.print(prompt);
        String input = scanner.nextLine();
        String[] tokens = input.split(",");
        double[] array = new double[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            try {
                array[i] = Double.parseDouble(tokens[i].trim());
            } catch (NumberFormatException e) {
                System.out.println("Input error, please make sure you enter numbers separated by commas.");
                System.exit(1);
            }
        }
        return array;
    }

    public static int getChoice() {
        String input = scanner.nextLine();
        try {
            int choice = Integer.parseInt(input.trim());
            if (choice != 1 && choice != 2) {
                throw new NumberFormatException();
            }
            return choice;
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid option (1 or 2).");
            System.exit(1);
            return -1;
        }
    }

    public static String getFilePath(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
