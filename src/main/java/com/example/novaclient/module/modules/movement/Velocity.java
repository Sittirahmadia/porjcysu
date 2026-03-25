package com.example.novaclient.module.modules.movement;

import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class Velocity extends Module {
    private float horizontal = 0.0f;
    private float vertical = 0.0f;
    
    public Velocity() {
        super("Velocity", "Reduces knockback", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
    }
    
    public float getHorizontal() {
        return horizontal;
    }
    
    public float getVertical() {
        return vertical;
    }
}