package com.example.novaclient.module.modules.combat;

import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.util.BlockUtil;
import com.example.novaclient.util.InventoryUtil;
import com.example.novaclient.util.RotationUtil;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class BurrowBot extends Module {
    private final boolean autoDisable = true;
    private boolean burrowed = false;

    public BurrowBot() {
        super("BurrowBot", "Automatically places a block inside your hitbox to burrow", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    public void onEnable() {
        burrowed = false;
    }

    @Override
    public void onDisable() {
        burrowed = false;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        BlockPos playerPos = BlockUtil.getPlayerBlockPos();

        if (!mc.world.getBlockState(playerPos).isReplaceable()) {
            if (autoDisable) setEnabled(false);
            return;
        }

        if (burrowed) {
            if (autoDisable) setEnabled(false);
            return;
        }

        int obsSlot = InventoryUtil.findItemInHotbar(Items.OBSIDIAN);
        if (obsSlot == -1) obsSlot = InventoryUtil.findItemInHotbar(Items.NETHERITE_BLOCK);
        if (obsSlot == -1) {
            if (autoDisable) setEnabled(false);
            return;
        }

        int prevSlot = mc.player.getInventory().selectedSlot;
        InventoryUtil.switchToSlot(obsSlot);

        mc.player.setVelocity(0, 0.42, 0);

        Direction side = BlockUtil.getPlaceSide(playerPos);
        mc.interactionManager.interactBlock(
            mc.player, Hand.MAIN_HAND,
            new BlockHitResult(Vec3d.ofCenter(playerPos), side, playerPos, false)
        );

        burrowed = true;
        InventoryUtil.switchToSlot(prevSlot);
    }
}