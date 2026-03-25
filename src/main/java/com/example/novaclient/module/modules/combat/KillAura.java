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
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KillAura extends Module {
    private final float range = 4.5f;
    private final float rotationSpeed = 0.35f;
    private final boolean players = true;
    private final boolean mobs = false;
    private final boolean animals = false;
    private final boolean smoothRotation = true;
    private final boolean requireWeapon = false;
    private final boolean autoSwitch = false;
    private final boolean multiTarget = false;
    private final float cooldownThreshold = 0.9f;
    private int tickCounter = 0;
    private final int delay = 0;
    private Entity currentTarget = null;

    public KillAura() {
        super("KillAura", "Automatically attacks entities", Category.COMBAT, GLFW.GLFW_KEY_R);
    }

    @Override
    public void onDisable() {
        currentTarget = null;
        tickCounter = 0;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.currentScreen != null) return;

        if (requireWeapon && !isHoldingWeapon()) return;

        tickCounter++;
        if (tickCounter < delay) return;

        List<Entity> targets = findTargets();
        if (targets.isEmpty()) {
            currentTarget = null;
            return;
        }

        currentTarget = targets.get(0);

        float[] rotations = RotationUtil.getRotations(currentTarget, mc.player.getEyePos());

        if (smoothRotation) {
            rotations = RotationUtil.smoothRotation(
                mc.player.getYaw(),
                mc.player.getPitch(),
                rotations[0],
                rotations[1],
                rotationSpeed
            );
        }

        NovaClient.getInstance().getRotationManager().setRotation(rotations[0], rotations[1]);

        float cooldown = mc.player.getAttackCooldownProgress(0.5f);
        if (cooldown < cooldownThreshold) return;

        if (multiTarget) {
            for (Entity t : targets) {
                if (mc.player.distanceTo(t) <= range) {
                    mc.interactionManager.attackEntity(mc.player, t);
                }
            }
            mc.player.swingHand(Hand.MAIN_HAND);
        } else {
            if (mc.player.distanceTo(currentTarget) <= range) {
                mc.interactionManager.attackEntity(mc.player, currentTarget);
                mc.player.swingHand(Hand.MAIN_HAND);
                tickCounter = 0;
            }
        }
    }

    private List<Entity> findTargets() {
        List<Entity> list = new ArrayList<>();
        for (Entity entity : mc.world.getEntities()) {
            if (!isValidTarget(entity)) continue;
            if (mc.player.distanceTo(entity) > range) continue;
            list.add(entity);
        }
        list.sort(Comparator.comparingDouble(e -> mc.player.squaredDistanceTo(e)));
        return list;
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

    public Entity getCurrentTarget() {
        return currentTarget;
    }
}