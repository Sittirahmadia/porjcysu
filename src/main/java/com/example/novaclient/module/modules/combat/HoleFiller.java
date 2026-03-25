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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HoleFiller extends Module {
    private final float range = 5.0f;
    private final int blocksPerTick = 3;
    private final boolean rotate = true;

    public HoleFiller() {
        super("HoleFiller", "Fills nearby holes to prevent enemies from hiding", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        int obsSlot = InventoryUtil.findItemInHotbar(Items.OBSIDIAN);
        if (obsSlot == -1) obsSlot = InventoryUtil.findItemInHotbar(Items.COBBLESTONE);
        if (obsSlot == -1) return;

        int prevSlot = mc.player.getInventory().selectedSlot;
        InventoryUtil.switchToSlot(obsSlot);

        List<BlockPos> holes = findNearbyHoles();
        int filled = 0;

        for (BlockPos hole : holes) {
            if (filled >= blocksPerTick) break;

            Direction side = BlockUtil.getPlaceSide(hole);
            if (side == null) continue;

            if (rotate) {
                float[] rot = RotationUtil.getRotations(hole, mc.player.getEyePos());
                mc.player.setYaw(rot[0]);
                mc.player.setPitch(rot[1]);
            }

            mc.interactionManager.interactBlock(
                mc.player, Hand.MAIN_HAND,
                new BlockHitResult(Vec3d.ofCenter(hole), side, hole, false)
            );
            filled++;
        }

        InventoryUtil.switchToSlot(prevSlot);
    }

    private List<BlockPos> findNearbyHoles() {
        List<BlockPos> holes = new ArrayList<>();
        BlockPos playerPos = BlockUtil.getPlayerBlockPos();

        for (int x = -(int) range; x <= (int) range; x++) {
            for (int z = -(int) range; z <= (int) range; z++) {
                BlockPos pos = playerPos.add(x, -1, z);
                if (!BlockUtil.isInRange(pos, range)) continue;
                if (!BlockUtil.isHole(pos)) continue;
                if (mc.player.getPos().squaredDistanceTo(Vec3d.ofCenter(pos)) < 4.0) continue;
                holes.add(pos);
            }
        }

        holes.sort(Comparator.comparingDouble(p -> mc.player.squaredDistanceTo(Vec3d.ofCenter(p))));
        return holes;
    }
}