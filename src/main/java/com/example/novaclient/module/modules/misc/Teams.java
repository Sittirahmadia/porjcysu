package com.example.novaclient.module.modules.misc;

import com.example.novaclient.NovaClient;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

public class Teams extends Module {
    public Teams() {
        super("Teams", "Ignores players on your scoreboard team", Category.MISC, GLFW.GLFW_KEY_UNKNOWN);
    }

    public static boolean isTeammate(Entity entity) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return false;
        Module teamsModule = NovaClient.getInstance().getModuleManager().getModule("Teams");
        if (teamsModule == null || !teamsModule.isEnabled()) return false;
        if (!(entity instanceof PlayerEntity player)) return false;
        if (mc.player.getScoreboardTeam() == null) return false;
        return mc.player.isTeammate(player);
    }
}