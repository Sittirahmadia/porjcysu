package com.example.novaclient.module.setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String defaultMode, String... modes) {
        super(name, defaultMode);
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultMode);
        if (this.index == -1) this.index = 0;
    }

    public void cycle() {
        index = (index + 1) % modes.size();
        value = modes.get(index);
    }

    public void setMode(String mode) {
        int i = modes.indexOf(mode);
        if (i != -1) {
            index = i;
            value = mode;
        }
    }

    public String getMode() {
        return value;
    }

    public List<String> getModes() {
        return modes;
    }

    public int getIndex() {
        return index;
    }

    public boolean is(String mode) {
        return value.equals(mode);
    }
}