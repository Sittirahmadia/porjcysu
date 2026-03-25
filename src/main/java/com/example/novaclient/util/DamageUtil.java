package com.example.novaclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
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

    public static float calculateCrystalDamage(BlockPos crystalPos, Entity entity) {
        return calculateCrystalDamage(Vec3d.ofCenter(crystalPos).add(0, 1, 0), entity);
    }

    public static float calculateCrystalDamage(Vec3d crystalPos, Entity entity) {
        if (mc.world == null) return 0;
        if (!(entity instanceof LivingEntity living)) return 0;

        double distance = Math.sqrt(entity.squaredDistanceTo(crystalPos));
        if (distance > 12) return 0;

        double exposure = getExposure(crystalPos, entity);
        double impact = (1.0 - distance / 12.0) * exposure;
        float damage = (float) ((impact * impact + impact) / 2.0 * 7.0 * 12.0 + 1.0);

        damage = getReductionAmount(living, damage);
        return Math.max(0, damage);
    }

    /**
     * Client-side reimplementation of Explosion.getExposure (removed in 1.21.2+).
     * Casts rays from the explosion centre to sample points on the entity's bounding
     * box and returns the fraction that are not obstructed by solid blocks.
     */
    private static double getExposure(Vec3d source, Entity entity) {
        if (mc.world == null) return 0;
        var box = entity.getBoundingBox();
        double dx = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double dy = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double dz = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
        if (dx < 0 || dy < 0 || dz < 0) return 0;

        double offsetX = (1.0 - Math.floor(1.0 / dx) * dx) / 2.0;
        double offsetZ = (1.0 - Math.floor(1.0 / dz) * dz) / 2.0;

        int unobstructed = 0;
        int total = 0;
        for (double fx = 0; fx <= 1.0; fx += dx) {
            for (double fy = 0; fy <= 1.0; fy += dy) {
                for (double fz = 0; fz <= 1.0; fz += dz) {
                    double tx = MathHelper.lerp(fx, box.minX, box.maxX) + offsetX;
                    double ty = MathHelper.lerp(fy, box.minY, box.maxY);
                    double tz = MathHelper.lerp(fz, box.minZ, box.maxZ) + offsetZ;
                    if (!rayHitsBlock(source, new Vec3d(tx, ty, tz))) {
                        unobstructed++;
                    }
                    total++;
                }
            }
        }
        return total == 0 ? 0.0 : (double) unobstructed / total;
    }

    /** Returns true if a solid block lies between {@code from} and {@code to}. */
    private static boolean rayHitsBlock(Vec3d from, Vec3d to) {
        if (mc.world == null) return true;
        Vec3d delta = to.subtract(from);
        double len = delta.length();
        if (len == 0) return false;
        Vec3d step = delta.normalize().multiply(0.3);
        int steps = (int) Math.ceil(len / 0.3);
        for (int i = 0; i <= steps; i++) {
            double t = Math.min(i * 0.3, len);
            Vec3d pos = from.add(delta.normalize().multiply(t));
            var state = mc.world.getBlockState(BlockPos.ofFloored(pos));
            if (!state.isAir() && state.getBlock().getBlastResistance() > 0) {
                return true;
            }
        }
        return false;
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
        armorReduction *= (1 - resistance / 25f);

        int protection = getProtectionAmount(entity, source);
        float protectionReduction = MathHelper.clamp(protection, 0, 20);
        armorReduction *= (1 - protectionReduction / 25f);

        return Math.max(armorReduction, 0);
    }

    /**
     * Client-side reimplementation of EnchantmentHelper.getProtectionAmount for 1.21.4.
     * Since the vanilla helper now strictly requires a ServerWorld to evaluate data-driven
     * enchantments, client-side mods must manually match registry keys and apply vanilla math.
     */
    private static int getProtectionAmount(LivingEntity entity, DamageSource source) {
        int total = 0;
        for (var stack : entity.getArmorItems()) {
            if (stack.isEmpty()) continue;
            
            ItemEnchantmentsComponent enchants = EnchantmentHelper.getEnchantments(stack);
            
            for (RegistryEntry<Enchantment> entry : enchants.getEnchantments()) {
                int level = enchants.getLevel(entry);
                if (level > 0) {
                    if (entry.matchesKey(Enchantments.PROTECTION)) {
                        total += level;
                    } else if (entry.matchesKey(Enchantments.BLAST_PROTECTION) && source.isIn(DamageTypeTags.IS_EXPLOSION)) {
                        total += level * 2;
                    } else if (entry.matchesKey(Enchantments.FIRE_PROTECTION) && source.isIn(DamageTypeTags.IS_FIRE)) {
                        total += level * 2;
                    } else if (entry.matchesKey(Enchantments.PROJECTILE_PROTECTION) && source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                        total += level * 2;
                    } else if (entry.matchesKey(Enchantments.FEATHER_FALLING) && source.isIn(DamageTypeTags.IS_FALL)) {
                        total += level * 3;
                    }
                }
            }
        }
        return total;
    }
}
