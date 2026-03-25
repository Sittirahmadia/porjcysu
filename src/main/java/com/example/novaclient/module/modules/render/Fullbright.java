package com.example.novaclient.module.modules.render;

import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", "Removes darkness", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN);
    }
    
    @Override
    public void onEnable() {
        if (mc.options != null) {
            mc.options.getGamma().setValue(16.0);
        }
    }
    
    @Override
    public void onDisable() {
        if (mc.options != null) {
            mc.options.getGamma().setValue(1.0);
        }
    }
}