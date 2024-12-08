package org.example.model;

import org.example.data.DataSet;
public class LinearRegression {
    private double k; // gradient
    private double b; // y-intercept
    private double learningRate = 0.01;
    private int epochs = 1000;

    public LinearRegression() {
        this.k = 0;
        this.b = 0;
    }

    public void train(DataSet data) {
        int m = data.getX().length;

        for (int i = 0; i < epochs; i++) {
            double cost_k = 0;
            double cost_b = 0;

            for (int j = 0; j < m; j++) {
                double y_pred = k * data.getX()[j] + b;
                double error = y_pred - data.getY()[j];
                cost_k += error * data.getX()[j];
                cost_b += error;
            }

            // update k and b
            k -= (learningRate / m) * cost_k;
            b -= (learningRate / m) * cost_b;

            // print cost every 100 epochs
            if (i % 100 == 0) {
                double cost = 0;
                for (int j = 0; j < m; j++) {
                    double y_pred = k * data.getX()[j] + b;
                    double error = y_pred - data.getY()[j];
                    cost += error * error;
                }
                cost /= (2 * m);
                System.out.printf("Epoch %d: Cost = %.4f%n", i, cost);
            }
        }
    }

    public double getK() {
        return k;
    }

    public double getB() {
        return b;
    }
}
