package com.example.novaclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

public class DamageUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static float getPlayerHealth(PlayerEntity player) {
        return player.getHealth() + player.getAbsorptionAmount();
    }

    public static float calculateCrystalDamage(BlockPos crystalPos, net.minecraft.entity.Entity entity) {
        return calculateCrystalDamage(Vec3d.ofCenter(crystalPos).add(0, 1, 0), entity);
    }

    public static float calculateCrystalDamage(Vec3d crystalPos, net.minecraft.entity.Entity entity) {
        if (mc.world == null) return 0;
        if (!(entity instanceof LivingEntity living)) return 0;

        double distance = Math.sqrt(entity.squaredDistanceTo(crystalPos));
        if (distance > 12) return 0;

        double exposure = Explosion.getExposure(crystalPos, entity, mc.world);
        double impact = (1.0 - distance / 12.0) * exposure;
        float damage = (float) ((impact * impact + impact) / 2.0 * 7.0 * 12.0 + 1.0);

        damage = getReductionAmount(living, damage);
        return Math.max(0, damage);
    }

    private static float getReductionAmount(LivingEntity entity, float damage) {
        if (mc.world == null) return damage;

        DamageSource source = mc.world.getDamageSources().explosion((Explosion) null);

        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return damage;
        }

        float armor = (float) entity.getAttributeValue(EntityAttributes.ARMOR);
        float toughness = (float) entity.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS);
        float resistance = 0;

        if (entity.hasStatusEffect(StatusEffects.RESISTANCE)) {
            int amplifier = entity.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier();
            resistance = (amplifier + 1) * 5;
        }

        float armorReduction = net.minecraft.entity.DamageUtil.getDamageLeft(entity, damage, source, armor, toughness);
        armorReduction = armorReduction * (1 - resistance / 25f);

        int protection = EnchantmentHelper.getProtectionAmount(mc.world.getRegistryManager(), entity, source);
        float protectionReduction = MathHelper.clamp(protection, 0, 20);
        armorReduction = armorReduction * (1 - protectionReduction / 25f);

        return Math.max(armorReduction, 0);
    }
}
