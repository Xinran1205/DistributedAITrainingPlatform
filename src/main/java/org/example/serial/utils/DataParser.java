package org.example.serial.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.example.serial.data.DataSet;

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

    // 用于分布式文件解码的
    public static DataSet parseFromFile(String filePath, int startLine, int endLine) {
        ArrayList<Double> xList = new ArrayList<>();
        ArrayList<Double> yList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                if (lineNumber >= startLine && lineNumber <= endLine) {
                    String[] tokens = line.split(",");
                    if (tokens.length != 2) {
                        System.out.printf("File format error: Line %d does not match the expected format (x,y).%n", lineNumber);
                        return null;
                    }
                    double x = Double.parseDouble(tokens[0].trim());
                    double y = Double.parseDouble(tokens[1].trim());
                    xList.add(x);
                    yList.add(y);
                }
                lineNumber++;
                if (lineNumber > endLine) break; // 超出需要的行数范围直接退出
            }
        } catch (IOException e) {
            System.out.println("Unable to read file: " + e.getMessage());
            return null;
        }

        double[] x = xList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] y = yList.stream().mapToDouble(Double::doubleValue).toArray();
        return new DataSet(x, y);
    }

    public static int countLines(String filePath) {
        int lines = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while (br.readLine() != null) {
                lines++;
            }
        } catch (IOException e) {
            System.err.println("Error counting lines in file: " + e.getMessage());
        }
        return lines;
    }


}
