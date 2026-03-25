package com.example.novaclient.module.setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Boolean getValue() {
        return value;
    }
}