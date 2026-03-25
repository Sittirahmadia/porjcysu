package com.example.novaclient.module.setting;

public class NumberSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double increment;

    public NumberSetting(String name, double defaultValue, double min, double max, double increment) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getIncrement() {
        return increment;
    }

    public float getValueFloat() {
        return value.floatValue();
    }

    public int getValueInt() {
        return value.intValue();
    }

    @Override
    public void setValue(Double value) {
        double clamped = Math.max(min, Math.min(max, value));
        double rounded = Math.round(clamped / increment) * increment;
        this.value = Math.round(rounded * 100.0) / 100.0;
    }
}