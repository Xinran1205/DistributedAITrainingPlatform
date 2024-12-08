package org.example.distributed.messages;

import java.io.Serializable;

public class GradientMessage implements Serializable {
    private double gradientK;
    private double gradientB;

    public GradientMessage(double gradientK, double gradientB) {
        this.gradientK = gradientK;
        this.gradientB = gradientB;
    }

    public double getGradientK() {
        return gradientK;
    }

    public double getGradientB() {
        return gradientB;
    }
}
