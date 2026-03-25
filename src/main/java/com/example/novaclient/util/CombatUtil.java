package com.example.novaclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class CombatUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean isShieldFacingAway(PlayerEntity target) {
        if (!target.isBlocking()) return false;
        if (!target.getMainHandStack().isOf(Items.SHIELD) && !target.getOffHandStack().isOf(Items.SHIELD)) {
            return false;
        }

        if (mc.player == null) return false;

        Vec3d toPlayer = mc.player.getPos().subtract(target.getPos()).normalize();
        Vec3d lookVec = target.getRotationVec(1.0f);

        double dot = lookVec.dotProduct(toPlayer);
        return dot < 0.5;
    }

    public static boolean canSeeEntity(Entity entity) {
        if (mc.player == null || mc.world == null) return false;
        Vec3d playerPos = mc.player.getEyePos();
        Vec3d targetPos = entity.getPos().add(0, entity.getHeight() / 2, 0);
        return mc.world.raycast(new net.minecraft.world.RaycastContext(
            playerPos,
            targetPos,
            net.minecraft.world.RaycastContext.ShapeType.COLLIDER,
            net.minecraft.world.RaycastContext.FluidHandling.NONE,
            mc.player
        )).getType() == net.minecraft.util.hit.HitResult.Type.MISS;
    }
}