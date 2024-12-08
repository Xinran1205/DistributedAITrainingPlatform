package org.example.serial.data;

public class DataSet {
    private double[] x;
    private double[] y;

    public DataSet(double[] x, double[] y) {
        this.x = x;
        this.y = y;
    }

    public double[] getX() {
        return x;
    }

    public double[] getY() {
        return y;
    }
}
