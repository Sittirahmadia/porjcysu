package com.example.novaclient.event.events;

import com.example.novaclient.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public class RenderWorldEvent extends Event {
    private final MatrixStack matrices;
    private final float tickDelta;
    
    public RenderWorldEvent(MatrixStack matrices, float tickDelta) {
        this.matrices = matrices;
        this.tickDelta = tickDelta;
    }
    
    public MatrixStack getMatrices() {
        return matrices;
    }
    
    public float getTickDelta() {
        return tickDelta;
    }
}