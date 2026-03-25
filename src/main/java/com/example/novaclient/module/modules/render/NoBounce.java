package com.example.novaclient.module.modules.render;

import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class NoBounce extends Module {
    public NoBounce() {
        super("NoBounce", "Removes view bobbing", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN);
    }
}