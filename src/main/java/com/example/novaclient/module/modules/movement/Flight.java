package com.example.novaclient.module.modules.movement;

import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class Flight extends Module {
    private float speed = 0.5f;
    
    public Flight() {
        super("Flight", "Allows you to fly", Category.MOVEMENT, GLFW.GLFW_KEY_F);
    }
    
    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null) return;
        
        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().setFlySpeed(speed / 10.0f);
    }
    
    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.getAbilities().flying = false;
            mc.player.getAbilities().setFlySpeed(0.05f);
        }
    }
}