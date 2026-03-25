package com.example.novaclient.module.modules.combat;

import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.AttackEntityEvent;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.mixin.MinecraftClientAccessor;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.module.setting.BooleanSetting;
import com.example.novaclient.module.setting.NumberSetting;
import com.example.novaclient.util.EnchantmentUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class BreachSwap extends Module {
    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 10, 100, 30, 1);
    private final BooleanSetting onlyOnGround = new BooleanSetting("Only on Ground", true);
    private final BooleanSetting silentSwap = new BooleanSetting("Silent Swap", true);

    private int originalSlot = -1;
    private boolean shouldSwitchBack = false;
    private long switchTime = 0;
    private boolean isSwappingAttack = false;

    private static final RegistryKey<Enchantment> BREACH_KEY = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("minecraft", "breach"));

    public BreachSwap() {
        super("Breach Swap", "Switches to a Breach mace when attacking", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
        addSettings(switchDelay, onlyOnGround, silentSwap);
    }

    @EventHandler
    public void onAttack(AttackEntityEvent event) {
        if (isNull() || isSwappingAttack) return;
        if (onlyOnGround.getValue() && !mc.player.isOnGround()) return;
        if (ShieldBreaker.breakingShield) return;
        if (!(event.getTarget() instanceof LivingEntity)) return;

        int maceSlot = findBreachMaceSlot();
        if (maceSlot == -1) return;

        if (originalSlot == -1) originalSlot = mc.player.getInventory().selectedSlot;

        if (silentSwap.getValue()) {
            int prevSlot = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = maceSlot;
            isSwappingAttack = true;
            ((MinecraftClientAccessor) mc).invokeDoAttack();
            isSwappingAttack = false;
            mc.player.getInventory().selectedSlot = prevSlot;
        } else {
            mc.player.getInventory().selectedSlot = maceSlot;
            isSwappingAttack = true;
            ((MinecraftClientAccessor) mc).invokeDoAttack();
            isSwappingAttack = false;
            shouldSwitchBack = true;
            switchTime = System.currentTimeMillis();
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (isNull()) return;
        if (ShieldBreaker.breakingShield) return;

        if (shouldSwitchBack && System.currentTimeMillis() - switchTime >= switchDelay.getValue()) {
            if (originalSlot != -1) {
                mc.player.getInventory().selectedSlot = originalSlot;
                originalSlot = -1;
            }
            shouldSwitchBack = false;
        }

        if (mc.options.attackKey.isPressed()) {
            HitResult hitResult = mc.crosshairTarget;
            if (hitResult instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity) {
                int maceSlot = findBreachMaceSlot();
                if (maceSlot != -1) {
                    if (originalSlot == -1) originalSlot = mc.player.getInventory().selectedSlot;
                    if (silentSwap.getValue()) {
                        int prevSlot = mc.player.getInventory().selectedSlot;
                        mc.player.getInventory().selectedSlot = maceSlot;
                        ((MinecraftClientAccessor) mc).invokeDoAttack();
                        mc.player.getInventory().selectedSlot = prevSlot;
                    } else {
                        mc.player.getInventory().selectedSlot = maceSlot;
                        ((MinecraftClientAccessor) mc).invokeDoAttack();
                        switchTime = System.currentTimeMillis();
                        shouldSwitchBack = true;
                    }
                }
            }
        }
    }

    private int findBreachMaceSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            Item item = stack.getItem();
            if (item instanceof MaceItem && EnchantmentUtil.hasEnchantment(stack, mc.world, BREACH_KEY)) return i;
        }
        return -1;
    }

    @Override
    public void onDisable() {
        if (originalSlot != -1) {
            mc.player.getInventory().selectedSlot = originalSlot;
            originalSlot = -1;
        }
        shouldSwitchBack = false;
        isSwappingAttack = false;
    }
}