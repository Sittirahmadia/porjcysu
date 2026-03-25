package com.example.novaclient.util;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeyUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean isKeyPressed(int keyCode) {
        if (mc.getWindow() == null) return false;
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) return false;
        
        long handle = mc.getWindow().getHandle();
        int state = GLFW.glfwGetKey(handle, keyCode);
        return state == GLFW.GLFW_PRESS || state == GLFW.GLFW_REPEAT;
    }

    public static boolean isMouseButtonPressed(int button) {
        if (mc.getWindow() == null) return false;
        
        long handle = mc.getWindow().getHandle();
        int state = GLFW.glfwGetMouseButton(handle, button);
        return state == GLFW.GLFW_PRESS;
    }
}