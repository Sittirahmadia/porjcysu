package com.example.novaclient.module.modules.combat;

import com.example.novaclient.NovaClient;
import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.util.InventoryUtil;
import com.example.novaclient.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class MacePVP extends Module {
    private final float range = 5.0f;
    private final float fallVelocityThreshold = -0.3f;
    private final double jumpVelocity = 0.42;
    private final boolean autoJump = true;
    private final boolean autoSwitch = true;
    private final boolean rotateToTarget = true;
    private final boolean smartAttack = true;
    private final float minFallHeight = 3.0f;
    private int tickCounter = 0;
    private final int jumpInterval = 20;
    private double startFallY = 0;
    private boolean falling = false;

    public MacePVP() {
        super("MacePVP", "Optimized mace combat with smart jump attacks", Category.COMBAT, GLFW.GLFW_KEY_M);
    }

    @Override
    public void onEnable() {
        startFallY = 0;
        falling = false;
        tickCounter = 0;
    }

    @Override
    public void onDisable() {
        startFallY = 0;
        falling = false;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        Entity target = findTarget();
        if (target == null) {
            falling = false;
            return;
        }

        if (autoSwitch) {
            int maceSlot = InventoryUtil.findItemInHotbar(Items.MACE);
            if (maceSlot != -1) InventoryUtil.switchToSlot(maceSlot);
        }

        if (rotateToTarget) {
            float[] rot = RotationUtil.getRotations(target, mc.player.getEyePos());
            NovaClient.getInstance().getRotationManager().setRotation(rot[0], rot[1]);
        }

        Vec3d velocity = mc.player.getVelocity();
        boolean onGround = mc.player.isOnGround();

        if (onGround && !falling) {
            tickCounter++;
            if (autoJump && tickCounter >= jumpInterval) {
                mc.player.setVelocity(velocity.x, jumpVelocity, velocity.z);
                startFallY = mc.player.getY();
                tickCounter = 0;
            }
        } else if (!onGround && velocity.y < 0) {
            falling = true;
        }

        if (falling && velocity.y <= fallVelocityThreshold) {
            double fallHeight = startFallY - mc.player.getY();
            if (!smartAttack || fallHeight >= minFallHeight) {
                if (mc.player.distanceTo(target) <= range) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }

        if (onGround && falling) {
            falling = false;
            startFallY = 0;
        }
    }

    private Entity findTarget() {
        Entity closest = null;
        double closestDist = range;
        for (Entity entity : mc.world.getEntities()) {
            if (!isValidTarget(entity)) continue;
            double dist = mc.player.distanceTo(entity);
            if (dist < closestDist) {
                closest = entity;
                closestDist = dist;
            }
        }
        return closest;
    }

    private boolean isValidTarget(Entity entity) {
        if (entity == mc.player) return false;
        if (!entity.isAlive()) return false;
        if (!(entity instanceof LivingEntity)) return false;
        if (NovaClient.getInstance().getFriendManager().isFriend(entity.getName().getString())) return false;
        return entity instanceof PlayerEntity;
    }
}