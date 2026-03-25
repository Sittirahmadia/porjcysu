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
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class AutoCrystal extends Module {
    private final float placeRange = 5.0f;
    private final float breakRange = 5.0f;
    private final float minDamage = 6.0f;
    private final float maxSelfDamage = 8.0f;
    private final int placeDelay = 2;
    private final int breakDelay = 1;
    private final boolean autoSwitch = true;
    private final boolean antiSuicide = true;
    private final boolean rotate = true;
    private int placeCounter = 0;
    private int breakCounter = 0;
    private BlockPos lastPlaced = null;

    public AutoCrystal() {
        super("AutoCrystal", "Automatically places and explodes end crystals", Category.COMBAT, GLFW.GLFW_KEY_C);
    }

    @Override
    public void onDisable() {
        lastPlaced = null;
        placeCounter = 0;
        breakCounter = 0;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        breakCounter++;
        if (breakCounter >= breakDelay) {
            breakCrystals();
            breakCounter = 0;
        }

        placeCounter++;
        if (placeCounter >= placeDelay) {
            placeCrystal();
            placeCounter = 0;
        }
    }

    private void breakCrystals() {
        EndCrystalEntity bestCrystal = null;
        float bestDamage = 0;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof EndCrystalEntity crystal)) continue;
            if (mc.player.distanceTo(crystal) > breakRange) continue;

            float selfDmg = DamageUtil.calculateCrystalDamage(BlockPos.ofFloored(crystal.getPos()).down(), mc.player);
            if (antiSuicide && selfDmg >= DamageUtil.getPlayerHealth(mc.player)) continue;

            for (Entity e : mc.world.getEntities()) {
                if (!(e instanceof PlayerEntity target) || target == mc.player) continue;
                if (NovaClient.getInstance().getFriendManager().isFriend(target.getName().getString())) continue;

                float targetDmg = DamageUtil.calculateCrystalDamage(BlockPos.ofFloored(crystal.getPos()).down(), target);
                if (targetDmg > bestDamage && targetDmg >= minDamage) {
                    bestDamage = targetDmg;
                    bestCrystal = crystal;
                }
            }
        }

        if (bestCrystal == null) return;

        if (rotate) {
            float[] rot = RotationUtil.getRotations(bestCrystal, mc.player.getEyePos());
            NovaClient.getInstance().getRotationManager().setRotation(rot[0], rot[1]);
        }

        mc.interactionManager.attackEntity(mc.player, bestCrystal);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private void placeCrystal() {
        int crystalSlot = InventoryUtil.findItemInHotbar(Items.END_CRYSTAL);
        if (crystalSlot == -1) return;

        BlockPos bestPos = findBestPlacePos();
        if (bestPos == null) return;

        int prevSlot = mc.player.getInventory().selectedSlot;
        if (autoSwitch) InventoryUtil.switchToSlot(crystalSlot);

        if (rotate) {
            float[] rot = RotationUtil.getRotations(bestPos, mc.player.getEyePos());
            NovaClient.getInstance().getRotationManager().setRotation(rot[0], rot[1]);
        }

        mc.interactionManager.interactBlock(
            mc.player,
            Hand.MAIN_HAND,
            new BlockHitResult(Vec3d.ofCenter(bestPos).add(0, 0.5, 0), Direction.UP, bestPos, false)
        );

        lastPlaced = bestPos;

        if (autoSwitch) InventoryUtil.switchToSlot(prevSlot);
    }

    private BlockPos findBestPlacePos() {
        BlockPos playerPos = BlockUtil.getPlayerBlockPos();
        BlockPos bestPos = null;
        float bestDamage = 0;

        for (int x = -5; x <= 5; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -5; z <= 5; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (!canPlaceCrystal(pos)) continue;
                    if (!BlockUtil.isInRange(pos, placeRange)) continue;

                    float selfDamage = DamageUtil.calculateCrystalDamage(pos, mc.player);
                    if (antiSuicide && selfDamage >= DamageUtil.getPlayerHealth(mc.player)) continue;
                    if (selfDamage > maxSelfDamage) continue;

                    float targetDamage = 0;
                    for (Entity entity : mc.world.getEntities()) {
                        if (!(entity instanceof PlayerEntity target) || target == mc.player) continue;
                        if (NovaClient.getInstance().getFriendManager().isFriend(target.getName().getString())) continue;
                        float dmg = DamageUtil.calculateCrystalDamage(pos, target);
                        if (dmg > targetDamage) targetDamage = dmg;
                    }

                    if (targetDamage > bestDamage && targetDamage >= minDamage) {
                        bestDamage = targetDamage;
                        bestPos = pos;
                    }
                }
            }
        }

        return bestPos;
    }

    private boolean canPlaceCrystal(BlockPos pos) {
        if (mc.world.getBlockState(pos).getBlock() != Blocks.OBSIDIAN &&
            mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK) return false;

        BlockPos up = pos.up();
        if (!mc.world.getBlockState(up).isReplaceable()) return false;
        if (!mc.world.getBlockState(up.up()).isReplaceable()) return false;
        return mc.world.getOtherEntities(null, new Box(up)).isEmpty();
    }
}