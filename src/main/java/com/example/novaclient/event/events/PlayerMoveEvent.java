package com.example.novaclient.event.events;

import com.example.novaclient.event.CancellableEvent;
import net.minecraft.util.math.Vec3d;

public class PlayerMoveEvent extends CancellableEvent {
    private Vec3d movement;
    
    public PlayerMoveEvent(Vec3d movement) {
        this.movement = movement;
    }
    
    public Vec3d getMovement() {
        return movement;
    }
    
    public void setMovement(Vec3d movement) {
        this.movement = movement;
    }
}