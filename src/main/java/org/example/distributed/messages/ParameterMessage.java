package org.example.distributed.messages;

import java.io.Serializable;

public class ParameterMessage implements Serializable {
    private double k;
    private double b;

    public ParameterMessage(double k, double b) {
        this.k = k;
        this.b = b;
    }

    public double getK() {
        return k;
    }

    public double getB() {
        return b;
    }
}
