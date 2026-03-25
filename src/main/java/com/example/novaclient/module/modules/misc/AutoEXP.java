package com.example.novaclient.module.modules.misc;

import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class AutoEXP extends Module {
    public AutoEXP() {
        super("AutoEXP", "Automatically throws XP bottles", Category.MISC, GLFW.GLFW_KEY_UNKNOWN);
    }
}