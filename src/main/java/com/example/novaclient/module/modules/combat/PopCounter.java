package com.example.novaclient.module.modules.combat;

import com.example.novaclient.NovaClient;
import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class PopCounter extends Module {
    private final Map<String, Integer> popCounts = new HashMap<>();
    private final Map<String, Boolean> hadTotem = new HashMap<>();

    public PopCounter() {
        super("PopCounter", "Counts totem pops per player and announces in chat", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    public void onDisable() {
        popCounts.clear();
        hadTotem.clear();
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;

            String name = player.getName().getString();
            boolean hasTotem = player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING ||
                               player.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING;

            boolean previouslyHad = hadTotem.getOrDefault(name, false);

            if (previouslyHad && !hasTotem) {
                int pops = popCounts.getOrDefault(name, 0) + 1;
                popCounts.put(name, pops);
                mc.player.sendMessage(
                    Text.literal("\u00a7c[PopCounter] \u00a7f" + name + " \u00a7apopped \u00a7c" + pops + " \u00a7a" + (pops == 1 ? "totem" : "totems")),
                    false
                );
            }

            hadTotem.put(name, hasTotem);
        }
    }

    public int getPopCount(String name) {
        return popCounts.getOrDefault(name, 0);
    }

    public Map<String, Integer> getAllPopCounts() {
        return new HashMap<>(popCounts);
    }
}