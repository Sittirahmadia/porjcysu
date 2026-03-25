package com.example.novaclient.mixin;

import com.example.novaclient.NovaClient;
import com.example.novaclient.event.events.RenderWorldEvent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci) {
        MatrixStack matrices = new MatrixStack();
        RenderWorldEvent event = new RenderWorldEvent(matrices, tickCounter.getTickDelta(false));
        NovaClient.getInstance().getEventBus().post(event);
    }
}
