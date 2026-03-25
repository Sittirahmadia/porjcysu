package com.example.novaclient.util;

public class TimerUtil {
    private long lastTime;

    public TimerUtil() {
        this.lastTime = System.currentTimeMillis();
    }

    public void reset() {
        this.lastTime = System.currentTimeMillis();
    }

    public boolean hasElapsedTime(long time) {
        return System.currentTimeMillis() - lastTime >= time;
    }

    public boolean hasElapsedTime(long time, boolean reset) {
        if (hasElapsedTime(time)) {
            if (reset) reset();
            return true;
        }
        return false;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - lastTime;
    }

    public boolean hasElapsedTime(int time) {
        return hasElapsedTime((long) time);
    }

    public boolean hasElapsedTime(double time) {
        return hasElapsedTime((long) time);
    }
}