package com.example.novaclient.module.modules.combat;

import com.example.novaclient.NovaClient;
import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.util.BlockUtil;
import com.example.novaclient.util.InventoryUtil;
import com.example.novaclient.util.RotationUtil;
import net.minecraft.block.Blocks;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class AutoCart extends Module {
    private final float range = 5.0f;
    private final boolean useTntCart = true;
    private final boolean placeRail = true;
    private final boolean rotate = true;
    private final int tickDelay = 5;
    private int tickCounter = 0;

    public AutoCart() {
        super("AutoCart", "Automatically places minecarts under enemies for damage", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    public void onDisable() {
        tickCounter = 0;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        tickCounter++;
        if (tickCounter < tickDelay) return;
        tickCounter = 0;

        PlayerEntity target = findTarget();
        if (target == null) return;

        BlockPos targetFloor = BlockPos.ofFloored(target.getPos()).down();

        if (placeRail) {
            int railSlot = InventoryUtil.findItemInHotbar(Items.RAIL);
            if (railSlot != -1 && mc.world.getBlockState(targetFloor.up()).isReplaceable()) {
                int prevSlot = mc.player.getInventory().selectedSlot;
                InventoryUtil.switchToSlot(railSlot);

                if (rotate) {
                    float[] rot = RotationUtil.getRotations(targetFloor, mc.player.getEyePos());
                    NovaClient.getInstance().getRotationManager().setRotation(rot[0], rot[1]);
                }

                Direction side = BlockUtil.getPlaceSide(targetFloor.up());
                mc.interactionManager.interactBlock(
                    mc.player, Hand.MAIN_HAND,
                    new BlockHitResult(Vec3d.ofCenter(targetFloor.up()), side, targetFloor.up(), false)
                );

                InventoryUtil.switchToSlot(prevSlot);
                return;
            }
        }

        if (mc.world.getBlockState(targetFloor.up()).getBlock() instanceof RailBlock) {
            int cartSlot = useTntCart
                ? InventoryUtil.findItemInHotbar(Items.TNT_MINECART)
                : InventoryUtil.findItemInHotbar(Items.MINECART);

            if (cartSlot == -1) return;

            int prevSlot = mc.player.getInventory().selectedSlot;
            InventoryUtil.switchToSlot(cartSlot);

            if (rotate) {
                float[] rot = RotationUtil.getRotations(targetFloor.up(), mc.player.getEyePos());
                NovaClient.getInstance().getRotationManager().setRotation(rot[0], rot[1]);
            }

            mc.interactionManager.interactBlock(
                mc.player, Hand.MAIN_HAND,
                new BlockHitResult(Vec3d.ofCenter(targetFloor.up()), Direction.UP, targetFloor.up(), false)
            );

            InventoryUtil.switchToSlot(prevSlot);
        }
    }

    private PlayerEntity findTarget() {
        if (mc.world == null) return null;
        PlayerEntity best = null;
        double bestDist = range;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (NovaClient.getInstance().getFriendManager().isFriend(player.getName().getString())) continue;
            double dist = mc.player.distanceTo(player);
            if (dist < bestDist) {
                bestDist = dist;
                best = player;
            }
        }
        return best;
    }
}