package com.example.novaclient.module.modules.movement;

import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class Speed extends Module {
    private float speed = 1.5f;

    public Speed() {
        super("Speed", "Increases movement speed", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null) return;

        if (mc.player.isOnGround() && mc.player.input.movementForward != 0) {
            mc.player.setVelocity(
                mc.player.getVelocity().x * speed,
                mc.player.getVelocity().y,
                mc.player.getVelocity().z * speed
            );
        }
    }
}
