package com.example.novaclient.manager;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationManager {
    private float yaw;
    private float pitch;
    private boolean rotating;
    
    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.rotating = true;
    }
    
    public void rotate(Vec3d target, Vec3d from) {
        double deltaX = target.x - from.x;
        double deltaY = target.y - from.y;
        double deltaZ = target.z - from.z;
        
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        
        this.yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;
        this.pitch = (float) -Math.toDegrees(Math.atan2(deltaY, distance));
        this.rotating = true;
    }
    
    public void rotateToEntity(Entity entity, Vec3d from) {
        Vec3d targetPos = entity.getPos().add(0, entity.getHeight() / 2.0, 0);
        rotate(targetPos, from);
    }
    
    public float getYaw() {
        return yaw;
    }
    
    public float getPitch() {
        return pitch;
    }
    
    public boolean isRotating() {
        return rotating;
    }
    
    public void reset() {
        rotating = false;
    }
}