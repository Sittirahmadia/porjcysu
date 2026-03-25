package com.example.novaclient.module.setting;

public class RangeSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double minValue;
    private final double maxValue;
    private final double increment;
    private double currentMinValue;
    private double currentMaxValue;

    public RangeSetting(String name, double min, double max, double minValue, double maxValue, double increment) {
        super(name, (minValue + maxValue) / 2);
        this.min = min;
        this.max = max;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.increment = increment;
        this.currentMinValue = minValue;
        this.currentMaxValue = maxValue;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMinValue() {
        return currentMinValue;
    }

    public double getMaxValue() {
        return currentMaxValue;
    }

    public void setMinValue(double minValue) {
        this.currentMinValue = Math.max(min, Math.min(max, minValue));
    }

    public void setMaxValue(double maxValue) {
        this.currentMaxValue = Math.max(min, Math.min(max, maxValue));
    }

    public double getIncrement() {
        return increment;
    }
}