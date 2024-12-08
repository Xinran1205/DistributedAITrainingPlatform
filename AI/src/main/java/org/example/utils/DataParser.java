package org.example.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.example.data.DataSet;

// DataParser class is used to parse data from a file
public class DataParser {
    public static DataSet parseFromFile(String filePath) {
        ArrayList<Double> xList = new ArrayList<>();
        ArrayList<Double> yList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // ignore empty lines
                String[] tokens = line.split(",");
                if (tokens.length != 2) {
                    System.out.printf("File format error: Line %d does not match the expected format (x,y).%n", lineNumber);
                    return null;
                }
                try {
                    double x = Double.parseDouble(tokens[0].trim());
                    double y = Double.parseDouble(tokens[1].trim());
                    xList.add(x);
                    yList.add(y);
                } catch (NumberFormatException e) {
                    System.out.printf("File format error: Line %d contains non-numeric characters.%n", lineNumber);
                    return null;
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.out.println("Unable to read file, please check if the file path is correct.");
            return null;
        }

        if (xList.isEmpty()) {
            System.out.println("No valid data in the file.");
            return null;
        }

        double[] x = xList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] y = yList.stream().mapToDouble(Double::doubleValue).toArray();

        return new DataSet(x, y);
    }
}
