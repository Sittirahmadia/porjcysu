package com.example.novaclient.module.modules.combat;

import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.util.InventoryUtil;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class AutoTotem extends Module {
    private final float healthThreshold = 20.0f;
    private final boolean force = true;

    public AutoTotem() {
        super("AutoTotem", "Automatically keeps totem in offhand", Category.COMBAT, GLFW.GLFW_KEY_T);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (!force && mc.player.getHealth() + mc.player.getAbsorptionAmount() > healthThreshold) return;
        if (mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        int totemSlot = findTotem();
        if (totemSlot == -1) return;

        int syncId = mc.player.currentScreenHandler.syncId;

        mc.interactionManager.clickSlot(syncId, totemSlot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, 45, 0, SlotActionType.PICKUP, mc.player);

        if (!mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
            mc.interactionManager.clickSlot(syncId, totemSlot, 0, SlotActionType.PICKUP, mc.player);
        }
    }

    private int findTotem() {
        if (mc.player == null) return -1;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                int slot = i;
                if (slot < 9) slot += 36;
                return slot;
            }
        }
        return -1;
    }
}