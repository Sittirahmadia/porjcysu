package com.example.novaclient.module.modules.combat;

import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.mixin.MinecraftClientAccessor;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.module.setting.BooleanSetting;
import com.example.novaclient.module.setting.NumberSetting;
import com.example.novaclient.util.CombatUtil;
import com.example.novaclient.util.InventoryUtil;
import com.example.novaclient.util.TimerUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

public class ShieldBreaker extends Module {
    public static boolean breakingShield = false;

    private final NumberSetting cps = new NumberSetting("CPS", 1, 20, 20, 1);
    private final NumberSetting reactionDelay = new NumberSetting("Reaction Delay", 0, 250, 0, 5);
    private final NumberSetting swapDelay = new NumberSetting("Swap Delay", 0, 500, 50, 10);
    private final NumberSetting attackDelay = new NumberSetting("Attack Delay", 0, 500, 50, 10);
    private final NumberSetting swapBackDelay = new NumberSetting("Swap Back Delay", 0, 500, 100, 10);
    private final BooleanSetting revertSlot = new BooleanSetting("Revert Slot", true);
    private final BooleanSetting rayTraceCheck = new BooleanSetting("Check Facing", true);
    private final BooleanSetting autoStun = new BooleanSetting("Auto Stun", true);
    private final BooleanSetting disableIfUsingItem = new BooleanSetting("Disable if using item", true);

    private final TimerUtil cpsTimer = new TimerUtil();
    private final TimerUtil reactionTimer = new TimerUtil();
    private final TimerUtil swapTimer = new TimerUtil();
    private final TimerUtil attackTimer = new TimerUtil();
    private final TimerUtil swapBackTimer = new TimerUtil();

    private int savedSlot = -1;

    public ShieldBreaker() {
        super("Shield Breaker", "Automatically breaks the opponent's shield", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
        addSettings(cps, reactionDelay, swapDelay, attackDelay, swapBackDelay,
                revertSlot, rayTraceCheck, disableIfUsingItem, autoStun);
    }

    private boolean canRunAuto() {
        if (isNull() || mc.currentScreen != null) return false;
        if (!InventoryUtil.hasWeapon(AxeItem.class)) return false;
        if (mc.player.isUsingItem() && disableIfUsingItem.getValue()) return false;
        return cpsTimer.hasElapsedTime((long) (1000.0 / cps.getValue()));
    }

    private PlayerEntity getTargetPlayer() {
        if (!(mc.crosshairTarget instanceof EntityHitResult hit)) return null;
        if (!(hit.getEntity() instanceof PlayerEntity target)) return null;
        return target;
    }

    @EventHandler
    private void onTick(TickEvent event) {
        if (isNull()) return;

        PlayerEntity target = getTargetPlayer();

        if (savedSlot != -1 && swapBackTimer.hasElapsedTime(swapBackDelay.getValueInt())) {
            boolean shouldSwapBack = false;
            if (target == null) {
                shouldSwapBack = true;
            } else {
                boolean isBlocking = target.isBlocking() && target.isHolding(Items.SHIELD);
                boolean canBreak = !rayTraceCheck.getValue() || !CombatUtil.isShieldFacingAway(target);
                if (!isBlocking || !canBreak) shouldSwapBack = true;
            }
            if (shouldSwapBack) {
                if (revertSlot.getValue()) mc.player.getInventory().selectedSlot = savedSlot;
                savedSlot = -1;
                breakingShield = false;
                return;
            }
        }

        if (!canRunAuto()) return;
        if (target == null) return;

        boolean isBlocking = target.isBlocking() && target.isHolding(Items.SHIELD);
        boolean canBreak = !rayTraceCheck.getValue() || !CombatUtil.isShieldFacingAway(target);

        if (!isBlocking || !canBreak) {
            if (!reactionTimer.hasElapsedTime(reactionDelay.getValueInt() / 2)) reactionTimer.reset();
            return;
        }

        if (!(mc.player.getMainHandStack().getItem() instanceof AxeItem)) {
            if (reactionTimer.hasElapsedTime(reactionDelay.getValueInt())
                    && swapTimer.hasElapsedTime(swapDelay.getValueInt())) {
                breakingShield = true;
                if (savedSlot == -1) savedSlot = mc.player.getInventory().selectedSlot;
                InventoryUtil.swapToWeapon(AxeItem.class);
                attackTimer.reset();
                swapTimer.reset();
            }
            return;
        }

        if (attackTimer.hasElapsedTime(attackDelay.getValueInt()) || savedSlot == -1) {
            ((MinecraftClientAccessor) mc).invokeDoAttack();
            if (autoStun.getValue()) ((MinecraftClientAccessor) mc).invokeDoAttack();
            cpsTimer.reset();
            attackTimer.reset();
            swapBackTimer.reset();
            breakingShield = false;
        }
    }

    @Override
    public void onDisable() {
        if (savedSlot != -1 && revertSlot.getValue()) {
            mc.player.getInventory().selectedSlot = savedSlot;
        }
        savedSlot = -1;
        breakingShield = false;
    }
}