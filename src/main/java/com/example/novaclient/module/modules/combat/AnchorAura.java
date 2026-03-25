package com.example.novaclient.module.modules.combat;

import com.example.novaclient.NovaClient;
import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.util.BlockUtil;
import com.example.novaclient.util.DamageUtil;
import com.example.novaclient.util.InventoryUtil;
import com.example.novaclient.util.RotationUtil;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class AnchorAura extends Module {
    private final float range = 5.0f;
    private final float minDamage = 5.0f;
    private final float maxSelfDamage = 8.0f;
    private final boolean antiSuicide = true;
    private final boolean autoSwitch = true;
    private final boolean rotate = true;
    private BlockPos placedAnchor = null;
    private boolean charged = false;
    private int tickCounter = 0;
    private final int delay = 3;

    public AnchorAura() {
        super("AnchorAura", "Automatically uses respawn anchors for PvP in the Overworld/End", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    public void onDisable() {
        placedAnchor = null;
        charged = false;
        tickCounter = 0;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (!mc.world.getDimension().bedWorks() && mc.world.getRegistryKey() != net.minecraft.world.World.NETHER) return;

        tickCounter++;
        if (tickCounter < delay) return;
        tickCounter = 0;

        PlayerEntity target = findTarget();
        if (target == null) return;

        if (placedAnchor != null) {
            var blockState = mc.world.getBlockState(placedAnchor);
            if (blockState.getBlock() == Blocks.RESPAWN_ANCHOR) {
                int charges = blockState.get(RespawnAnchorBlock.CHARGES);
                if (charges == 0) {
                    chargeAnchor(placedAnchor);
                    return;
                }
                if (charges > 0) {
                    useAnchor(placedAnchor);
                    return;
                }
            } else {
                placedAnchor = null;
            }
        }

        int anchorSlot = InventoryUtil.findItemInHotbar(Items.RESPAWN_ANCHOR);
        if (anchorSlot == -1) return;

        BlockPos placePos = findPlacePos(target);
        if (placePos == null) return;

        int prevSlot = mc.player.getInventory().selectedSlot;
        if (autoSwitch) InventoryUtil.switchToSlot(anchorSlot);

        if (rotate) {
            float[] rot = RotationUtil.getRotations(placePos, mc.player.getEyePos());
            NovaClient.getInstance().getRotationManager().setRotation(rot[0], rot[1]);
        }

        Direction side = BlockUtil.getPlaceSide(placePos);
        mc.interactionManager.interactBlock(
            mc.player, Hand.MAIN_HAND,
            new BlockHitResult(Vec3d.ofCenter(placePos), side, placePos, false)
        );

        placedAnchor = placePos;
        if (autoSwitch) InventoryUtil.switchToSlot(prevSlot);
    }

    private void chargeAnchor(BlockPos pos) {
        int glowstoneSlot = InventoryUtil.findItemInHotbar(Items.GLOWSTONE);
        if (glowstoneSlot == -1) return;

        int prevSlot = mc.player.getInventory().selectedSlot;
        if (autoSwitch) InventoryUtil.switchToSlot(glowstoneSlot);

        mc.interactionManager.interactBlock(
            mc.player, Hand.MAIN_HAND,
            new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false)
        );

        if (autoSwitch) InventoryUtil.switchToSlot(prevSlot);
    }

    private void useAnchor(BlockPos pos) {
        if (rotate) {
            float[] rot = RotationUtil.getRotations(pos, mc.player.getEyePos());
            NovaClient.getInstance().getRotationManager().setRotation(rot[0], rot[1]);
        }

        mc.interactionManager.interactBlock(
            mc.player, Hand.MAIN_HAND,
            new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false)
        );

        placedAnchor = null;
    }

    private BlockPos findPlacePos(PlayerEntity target) {
        BlockPos targetPos = BlockPos.ofFloored(target.getPos());
        BlockPos bestPos = null;
        float bestDmg = minDamage - 1;

        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos pos = targetPos.add(x, y, z);
                    if (!mc.world.getBlockState(pos).isReplaceable()) continue;
                    if (!BlockUtil.isInRange(pos, range)) continue;

                    float selfDmg = DamageUtil.calculateCrystalDamage(pos, mc.player);
                    if (antiSuicide && selfDmg >= DamageUtil.getPlayerHealth(mc.player)) continue;
                    if (selfDmg > maxSelfDamage) continue;

                    float targetDmg = DamageUtil.calculateCrystalDamage(pos, target);
                    if (targetDmg > bestDmg) {
                        bestDmg = targetDmg;
                        bestPos = pos;
                    }
                }
            }
        }

        return bestPos;
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