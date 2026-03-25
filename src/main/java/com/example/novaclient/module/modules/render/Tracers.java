package com.example.novaclient.module.modules.render;

import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class Tracers extends Module {
    public Tracers() {
        super("Tracers", "Draws lines to entities", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN);
    }
}