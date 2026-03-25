package com.example.novaclient.module.modules.combat;

import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.util.BlockUtil;
import com.example.novaclient.util.InventoryUtil;
import com.example.novaclient.util.RotationUtil;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Surround extends Module {
    private final boolean autoDisable = true;
    private final boolean rotate = true;
    private final int blocksPerTick = 4;

    public Surround() {
        super("Surround", "Surrounds you with obsidian blocks", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        int obsSlot = InventoryUtil.findItemInHotbar(Items.OBSIDIAN);
        if (obsSlot == -1) {
            if (autoDisable) setEnabled(false);
            return;
        }

        int prevSlot = mc.player.getInventory().selectedSlot;
        InventoryUtil.switchToSlot(obsSlot);

        BlockPos playerPos = BlockUtil.getPlayerBlockPos();
        List<BlockPos> positions = getSurroundPositions(playerPos);
        int placed = 0;

        for (BlockPos pos : positions) {
            if (placed >= blocksPerTick) break;
            if (!mc.world.getBlockState(pos).isReplaceable()) continue;

            Direction side = BlockUtil.getPlaceSide(pos);
            if (side == null) continue;

            if (rotate) {
                float[] rot = RotationUtil.getRotations(pos, mc.player.getEyePos());
                mc.player.setYaw(rot[0]);
                mc.player.setPitch(rot[1]);
            }

            mc.interactionManager.interactBlock(
                mc.player, Hand.MAIN_HAND,
                new BlockHitResult(Vec3d.ofCenter(pos), side, pos, false)
            );
            placed++;
        }

        InventoryUtil.switchToSlot(prevSlot);

        if (placed == 0 && autoDisable) {
            setEnabled(false);
        }
    }

    private List<BlockPos> getSurroundPositions(BlockPos center) {
        List<BlockPos> positions = new ArrayList<>();
        positions.add(center.north());
        positions.add(center.south());
        positions.add(center.east());
        positions.add(center.west());
        positions.add(center.north().west());
        positions.add(center.north().east());
        positions.add(center.south().west());
        positions.add(center.south().east());
        return positions;
    }
}