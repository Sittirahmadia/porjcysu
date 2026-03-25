package com.example.novaclient.module.modules.render;

import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class NoRender extends Module {
    public NoRender() {
        super("NoRender", "Stops rendering certain elements", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN);
    }
}