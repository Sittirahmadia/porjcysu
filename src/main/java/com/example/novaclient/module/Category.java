package com.example.novaclient.module;

public enum Category {
    COMBAT("Combat", 0xFF5555),
    MOVEMENT("Movement", 0x55FF55),
    RENDER("Render", 0x5555FF),
    MISC("Misc", 0xFFFF55);
    
    private final String name;
    private final int color;
    
    Category(String name, int color) {
        this.name = name;
        this.color = color;
    }
    
    public String getName() {
        return name;
    }
    
    public int getColor() {
        return color;
    }
}