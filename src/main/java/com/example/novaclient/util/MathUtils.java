package com.example.novaclient.util;

import java.util.Random;

public class MathUtils {
    private static final Random random = new Random();

    public static double randomDoubleBetween(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    public static int randomIntBetween(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    public static float randomFloatBetween(float min, float max) {
        return min + (max - min) * random.nextFloat();
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}