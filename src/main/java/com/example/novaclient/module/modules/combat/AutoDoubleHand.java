package com.example.novaclient.module.modules.combat;

import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class AutoDoubleHand extends Module {
    private Item primary = Items.TOTEM_OF_UNDYING;
    private Item secondary = Items.GOLDEN_APPLE;
    private boolean preferTotem = true;

    public AutoDoubleHand() {
        super("AutoDoubleHand", "Automatically keeps preferred item in offhand, swapping to secondary when needed", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.interactionManager == null) return;

        Item desired = preferTotem ? primary : secondary;

        if (mc.player.getOffHandStack().getItem() == desired) return;

        int slot = findInInventory(desired);
        if (slot == -1 && desired == primary) {
            desired = secondary;
            slot = findInInventory(desired);
        }

        if (slot == -1) return;

        int syncId = mc.player.currentScreenHandler.syncId;
        mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, 45, 0, SlotActionType.PICKUP, mc.player);

        if (!mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
            mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
        }
    }

    private int findInInventory(Item item) {
        if (mc.player == null) return -1;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) {
                return i < 9 ? i + 36 : i;
            }
        }
        return -1;
    }
}