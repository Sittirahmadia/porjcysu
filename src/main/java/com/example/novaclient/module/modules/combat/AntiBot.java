package com.example.novaclient.module.modules.combat;

import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AntiBot extends Module {
    private final Set<UUID> botUUIDs = new HashSet<>();

    public AntiBot() {
        super("AntiBot", "Filters bots from target lists", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    public void onDisable() {
        botUUIDs.clear();
    }

    public boolean isBot(PlayerEntity player) {
        if (!isEnabled()) return false;
        if (botUUIDs.contains(player.getUuid())) return true;
        if (isNullUUID(player)) return true;
        if (hasDuplicateName(player)) return true;
        return false;
    }

    private boolean isNullUUID(PlayerEntity player) {
        UUID uuid = player.getUuid();
        return uuid.getMostSignificantBits() == 0 && uuid.getLeastSignificantBits() == 0;
    }

    private boolean hasDuplicateName(PlayerEntity player) {
        if (mc.world == null) return false;
        int count = 0;
        String name = player.getName().getString();
        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p.getName().getString().equals(name)) count++;
        }
        return count > 1;
    }

    public void markBot(UUID uuid) {
        botUUIDs.add(uuid);
    }

    public void unmarkBot(UUID uuid) {
        botUUIDs.remove(uuid);
    }
}