package org.example.serial;


import org.example.serial.data.DataSet;
import org.example.serial.model.LinearRegression;
import org.example.serial.utils.InputHandler;
import org.example.serial.utils.DataParser;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the One-dimensional Regression Gradient Descent Platform!");

        // Select input method
        System.out.println("Please choose the input method:");
        System.out.println("1. Command line input");
        System.out.println("2. Read from txt file");
        System.out.print("Please enter an option (1 or 2):");
        int choice = InputHandler.getChoice();

        double[] x = null;
        double[] y = null;

        switch (choice) {
            case 1:
                // Command line input
                x = InputHandler.getInputArray("Please enter the values of x, separated by commas:");
                y = InputHandler.getInputArray("Please enter the values of y, separated by commas:");
                break;
            case 2:
                // Read from file
                String filePath = InputHandler.getFilePath("Please enter the path of the training data file (e.g., data.txt):");

                // 这里打印了main函数的当前工作目录看了一下，很神奇，是在DistributedAITrainingPlatform这个目录下
                // 所以我把data.txt也放到了DistributedAITrainingPlatform这个目录下
                //System.out.println("Current working directory: " + System.getProperty("user.dir"));
                DataSet dataSetFromFile = DataParser.parseFromFile(filePath);
                if (dataSetFromFile == null) {
                    System.out.println("File reading failed or file format is incorrect!");
                    return;
                }
                x = dataSetFromFile.getX();
                y = dataSetFromFile.getY();
                break;
            default:
                System.out.println("Invalid option!");
                return;
        }

        // Validate data length
        if (x.length != y.length) {
            System.out.println("Error: The lengths of x and y do not match!");
            return;
        }

        // Create dataset
        DataSet dataSet = new DataSet(x, y);

        // Initialize model
        LinearRegression model = new LinearRegression();

        // Train model
        model.train(dataSet);

        // Output results
        System.out.printf("Training completed! Model parameters: k = %.4f, b = %.4f%n", model.getK(), model.getB());
    }
}
