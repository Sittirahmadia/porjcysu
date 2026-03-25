package com.example.novaclient.mixin;

import com.example.novaclient.NovaClient;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.event.events.WorldChangeEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Shadow
    public ClientWorld world;

    private ClientWorld lastWorld = null;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        NovaClient instance = NovaClient.getInstance();
        if (instance == null) return;
        instance.getEventBus().post(new TickEvent());
        if (world != lastWorld) {
            if (lastWorld != null) {
                instance.getEventBus().post(new WorldChangeEvent());
            }
            lastWorld = world;
        }
    }
}