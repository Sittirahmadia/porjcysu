package com.example.novaclient.module.setting;

import java.awt.Color;

public class ColorSetting extends Setting<Color> {
    private boolean rainbow;

    public ColorSetting(String name, Color defaultColor) {
        super(name, defaultColor);
        this.rainbow = false;
    }

    public int getRed() {
        return value.getRed();
    }

    public int getGreen() {
        return value.getGreen();
    }

    public int getBlue() {
        return value.getBlue();
    }

    public int getAlpha() {
        return value.getAlpha();
    }

    public int getRGB() {
        return value.getRGB();
    }

    public boolean isRainbow() {
        return rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public void setColor(int r, int g, int b, int a) {
        value = new Color(
            Math.max(0, Math.min(255, r)),
            Math.max(0, Math.min(255, g)),
            Math.max(0, Math.min(255, b)),
            Math.max(0, Math.min(255, a))
        );
    }
}