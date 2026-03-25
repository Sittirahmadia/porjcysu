package com.example.novaclient.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil {

    public static float[] getRotations(Entity target, Vec3d eyePos) {
        return getRotations(target.getPos().add(0, target.getHeight() / 2, 0), eyePos);
    }

    public static float[] getRotations(BlockPos pos, Vec3d eyePos) {
        return getRotations(Vec3d.ofCenter(pos), eyePos);
    }

    public static float[] getRotations(Vec3d target, Vec3d eyePos) {
        Vec3d diff = target.subtract(eyePos);
        double diffX = diff.x;
        double diffY = diff.y;
        double diffZ = diff.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
            MathHelper.wrapDegrees(yaw),
            MathHelper.wrapDegrees(pitch)
        };
    }

    public static float[] smoothRotation(float currentYaw, float currentPitch, float targetYaw, float targetPitch, float speed) {
        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float pitchDiff = MathHelper.wrapDegrees(targetPitch - currentPitch);

        float newYaw = currentYaw + yawDiff * speed;
        float newPitch = currentPitch + pitchDiff * speed;

        return new float[]{newYaw, newPitch};
    }

    public static double getAngleDifference(float yaw1, float pitch1, float yaw2, float pitch2) {
        float yawDiff = MathHelper.wrapDegrees(yaw2 - yaw1);
        float pitchDiff = MathHelper.wrapDegrees(pitch2 - pitch1);
        return Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);
    }
}