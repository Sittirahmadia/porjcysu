package com.example.novaclient.module.setting;

public class KeybindSetting extends Setting<Integer> {
    private final boolean requiresModifier;

    public KeybindSetting(String name, int defaultKey, boolean requiresModifier) {
        super(name, defaultKey);
        this.requiresModifier = requiresModifier;
    }

    public int getKeyCode() {
        return value;
    }

    public boolean requiresModifier() {
        return requiresModifier;
    }
}