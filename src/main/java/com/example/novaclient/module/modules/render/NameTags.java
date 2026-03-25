package com.example.novaclient.module.modules.render;

import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class NameTags extends Module {
    public NameTags() {
        super("NameTags", "Enhanced name tags", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN);
    }
}