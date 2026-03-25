package com.example.novaclient.module.modules.combat;

import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class AutoInventoryTotem extends Module {
    private final int minTotems = 3;

    public AutoInventoryTotem() {
        super("AutoInventoryTotem", "Moves totems from inventory to hotbar to keep a minimum supply", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.interactionManager == null) return;

        int totemsInHotbar = countTotemsInHotbar();
        if (totemsInHotbar >= minTotems) return;

        int totemSlotInInventory = findTotemInMainInventory();
        if (totemSlotInInventory == -1) return;

        int emptyHotbarSlot = findEmptyHotbarSlot();
        if (emptyHotbarSlot == -1) return;

        int syncId = mc.player.currentScreenHandler.syncId;
        mc.interactionManager.clickSlot(syncId, totemSlotInInventory, emptyHotbarSlot, SlotActionType.SWAP, mc.player);
    }

    private int countTotemsInHotbar() {
        if (mc.player == null) return 0;
        int count = 0;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) count++;
        }
        return count;
    }

    private int findTotemInMainInventory() {
        if (mc.player == null) return -1;
        for (int i = 9; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                return i + 27;
            }
        }
        return -1;
    }

    private int findEmptyHotbarSlot() {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isEmpty()) return i;
        }
        return -1;
    }
}