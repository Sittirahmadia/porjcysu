package com.example.novaclient.module.modules.combat;

import com.example.novaclient.NovaClient;
import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import org.lwjgl.glfw.GLFW;

public class AimAssist extends Module {
    private final float range = 6.0f;
    private final float speed = 0.12f;
    private final float fov = 90.0f;
    private final boolean onlyWeapon = true;
    private final boolean players = true;
    private final boolean mobs = false;
    private final boolean animals = false;
    private final boolean requireLooking = true;

    public AimAssist() {
        super("AimAssist", "Smoothly assists aiming towards the nearest target", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.currentScreen != null) return;
        if (onlyWeapon && !isHoldingWeapon()) return;

        Entity target = findClosestInFov();
        if (target == null) return;

        float[] targetRot = RotationUtil.getRotations(target, mc.player.getEyePos());
        float[] smoothed = RotationUtil.smoothRotation(
            mc.player.getYaw(),
            mc.player.getPitch(),
            targetRot[0],
            targetRot[1],
            speed
        );

        NovaClient.getInstance().getRotationManager().setRotation(smoothed[0], smoothed[1]);
    }

    private Entity findClosestInFov() {
        Entity best = null;
        double bestAngle = fov;

        for (Entity entity : mc.world.getEntities()) {
            if (!isValidTarget(entity)) continue;
            if (mc.player.distanceTo(entity) > range) continue;

            float[] rot = RotationUtil.getRotations(entity, mc.player.getEyePos());
            double angle = RotationUtil.getAngleDifference(
                mc.player.getYaw(), mc.player.getPitch(),
                rot[0], rot[1]
            );

            if (requireLooking && angle > fov / 2.0) continue;

            if (angle < bestAngle) {
                bestAngle = angle;
                best = entity;
            }
        }

        return best;
    }

    private boolean isValidTarget(Entity entity) {
        if (entity == mc.player) return false;
        if (!entity.isAlive()) return false;
        if (!(entity instanceof LivingEntity)) return false;
        if (NovaClient.getInstance().getFriendManager().isFriend(entity.getName().getString())) return false;
        if (entity instanceof PlayerEntity) return players;
        if (entity instanceof HostileEntity) return mobs;
        if (entity instanceof PassiveEntity) return animals;
        return false;
    }

    private boolean isHoldingWeapon() {
        return mc.player.getMainHandStack().getItem() instanceof SwordItem ||
               mc.player.getMainHandStack().getItem() instanceof AxeItem;
    }
}