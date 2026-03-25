package com.example.novaclient.module.modules.misc;

import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class NoBreakDelay extends Module {
    public NoBreakDelay() {
        super("NoBreakDelay", "Removes block break delay", Category.MISC, GLFW.GLFW_KEY_UNKNOWN);
    }
}