package com.example.novaclient.mixin;

import com.example.novaclient.module.modules.combat.Hitboxes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public abstract Box getBoundingBox();

    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    private void onGetBoundingBox(CallbackInfoReturnable<Box> cir) {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof LivingEntity)) return;
        float expansion = Hitboxes.getExpansion();
        if (expansion > 0) {
            cir.setReturnValue(cir.getReturnValue().expand(expansion));
        }
    }
}