package com.example.novaclient.mixin;

import com.example.novaclient.NovaClient;
import com.example.novaclient.manager.RotationManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    
    public MixinClientPlayerEntity(ClientWorld clientWorld, GameProfile gameProfile) {
        super(clientWorld, gameProfile);
    }
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        RotationManager rotationManager = NovaClient.getInstance().getRotationManager();
        if (rotationManager.isRotating()) {
            setYaw(rotationManager.getYaw());
            setPitch(rotationManager.getPitch());
            rotationManager.reset();
        }
    }
}