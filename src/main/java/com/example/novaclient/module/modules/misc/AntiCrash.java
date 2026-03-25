package com.example.novaclient.module.modules.misc;

import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class AntiCrash extends Module {
    public AntiCrash() {
        super("AntiCrash", "Prevents game crashes", Category.MISC, GLFW.GLFW_KEY_UNKNOWN);
    }
}