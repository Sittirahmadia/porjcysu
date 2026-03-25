package com.example.novaclient.module.modules.combat;

import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.TickEvent;
import com.example.novaclient.mixin.MinecraftClientAccessor;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.module.setting.BooleanSetting;
import com.example.novaclient.module.setting.KeybindSetting;
import com.example.novaclient.module.setting.NumberSetting;
import com.example.novaclient.util.KeyUtils;
import com.example.novaclient.util.TimerUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class ElytraHotSwap extends Module {
    private final KeybindSetting hotswapKey = new KeybindSetting("Hotswap Key", GLFW.GLFW_KEY_G, false);
    private final NumberSetting swapDelay = new NumberSetting("Swap Delay (MS)", 50, 500, 150, 25);
    private final BooleanSetting autoSwitchBack = new BooleanSetting("Auto Switch Back", true);
    private final BooleanSetting silentSwap = new BooleanSetting("Silent Swap", true);
    private final BooleanSetting toggleMode = new BooleanSetting("Toggle Mode", true);

    private final TimerUtil swapTimer = new TimerUtil();
    private boolean keyPressed = false;
    private boolean isSwapping = false;
    private int originalSlot = -1;
    private ItemStack previousChestArmor = ItemStack.EMPTY;
    private boolean swappingToElytra = true;
    private SwapState currentState = SwapState.IDLE;

    public ElytraHotSwap() {
        super("Elytra HotSwap", "Quickly swap to elytra and equip it, or swap back to chestplate", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
        addSettings(hotswapKey, swapDelay, autoSwitchBack, silentSwap, toggleMode);
    }

    @EventHandler
    private void onTick(TickEvent event) {
        if (isNull()) return;

        boolean currentKeyState = KeyUtils.isKeyPressed(hotswapKey.getKeyCode());
        if (currentKeyState && !keyPressed) startHotswap();
        keyPressed = currentKeyState;

        if (!isSwapping) return;
        if (!swapTimer.hasElapsedTime(swapDelay.getValueInt())) return;

        switch (currentState) {
            case SWITCHING_TO_ITEM -> handleSwitchingToItem();
            case EQUIPPING_ITEM -> handleEquippingItem();
            case SWITCHING_BACK -> handleSwitchingBack();
        }
    }

    private void startHotswap() {
        if (isSwapping) return;
        boolean hasElytraEquipped = hasElytraEquipped();

        if (toggleMode.getValue() && hasElytraEquipped) {
            swappingToElytra = false;
            int chestplateSlot = findChestplateInHotbar();
            if (chestplateSlot == -1 && previousChestArmor.isEmpty()) return;
        } else {
            swappingToElytra = true;
            if (findElytraInHotbar() == -1) return;
            if (!hasElytraEquipped) previousChestArmor = mc.player.getInventory().getArmorStack(2).copy();
        }

        isSwapping = true;
        originalSlot = mc.player.getInventory().selectedSlot;
        currentState = SwapState.SWITCHING_TO_ITEM;
        swapTimer.reset();
    }

    private void handleSwitchingToItem() {
        int targetSlot = swappingToElytra ? findElytraInHotbar() : findChestplateInHotbar();

        if (targetSlot == -1) {
            if (!swappingToElytra && !previousChestArmor.isEmpty()) {
                currentState = SwapState.EQUIPPING_ITEM;
                swapTimer.reset();
                return;
            }
            finishHotswap();
            return;
        }

        if (!silentSwap.getValue()) mc.player.getInventory().selectedSlot = targetSlot;
        currentState = SwapState.EQUIPPING_ITEM;
        swapTimer.reset();
    }

    private void handleEquippingItem() {
        if (swappingToElytra) equipElytra();
        else equipChestplate();

        if (autoSwitchBack.getValue() && !silentSwap.getValue()) {
            currentState = SwapState.SWITCHING_BACK;
        } else {
            finishHotswap();
        }
        swapTimer.reset();
    }

    private void equipElytra() {
        int elytraSlot = silentSwap.getValue() ? findElytraInHotbar() : mc.player.getInventory().selectedSlot;
        if (elytraSlot == -1) { finishHotswap(); return; }
        ItemStack elytraStack = mc.player.getInventory().getStack(elytraSlot);
        if (elytraStack.isEmpty() || elytraStack.getItem() != Items.ELYTRA) { finishHotswap(); return; }

        if (silentSwap.getValue()) {
            int currentSlot = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = elytraSlot;
            if (mc.interactionManager != null) ((MinecraftClientAccessor) mc).invokeDoItemUse();
            mc.player.getInventory().selectedSlot = currentSlot;
        } else {
            if (mc.interactionManager != null) ((MinecraftClientAccessor) mc).invokeDoItemUse();
        }
    }

    private void equipChestplate() {
        int chestplateSlot = findChestplateInHotbar();

        if (chestplateSlot != -1) {
            ItemStack stack = mc.player.getInventory().getStack(chestplateSlot);
            if (!stack.isEmpty() && isChestplate(stack)) {
                if (silentSwap.getValue()) {
                    int currentSlot = mc.player.getInventory().selectedSlot;
                    mc.player.getInventory().selectedSlot = chestplateSlot;
                    if (mc.interactionManager != null) ((MinecraftClientAccessor) mc).invokeDoItemUse();
                    mc.player.getInventory().selectedSlot = currentSlot;
                } else {
                    if (mc.interactionManager != null) ((MinecraftClientAccessor) mc).invokeDoItemUse();
                }
                return;
            }
        }

        if (!previousChestArmor.isEmpty()) {
            mc.player.getInventory().setStack(36 + mc.player.getInventory().selectedSlot, previousChestArmor.copy());
            if (mc.interactionManager != null) ((MinecraftClientAccessor) mc).invokeDoItemUse();
            previousChestArmor = ItemStack.EMPTY;
        }
    }

    private void handleSwitchingBack() {
        if (originalSlot != -1) mc.player.getInventory().selectedSlot = originalSlot;
        finishHotswap();
    }

    private void finishHotswap() {
        isSwapping = false;
        currentState = SwapState.IDLE;
        originalSlot = -1;
        swappingToElytra = true;
    }

    private int findElytraInHotbar() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == Items.ELYTRA) return i;
        }
        return -1;
    }

    private int findChestplateInHotbar() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && isChestplate(stack)) return i;
        }
        return -1;
    }

    private boolean isChestplate(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() == Items.DIAMOND_CHESTPLATE
                || stack.getItem() == Items.IRON_CHESTPLATE
                || stack.getItem() == Items.GOLDEN_CHESTPLATE
                || stack.getItem() == Items.LEATHER_CHESTPLATE
                || stack.getItem() == Items.CHAINMAIL_CHESTPLATE
                || stack.getItem() == Items.NETHERITE_CHESTPLATE;
    }

    private boolean hasElytraEquipped() {
        ItemStack chestArmor = mc.player.getInventory().getArmorStack(2);
        return !chestArmor.isEmpty() && chestArmor.getItem() == Items.ELYTRA;
    }

    @Override
    public int getKey() {
        return GLFW.GLFW_KEY_UNKNOWN;
    }

    @Override
    public void setKey(int key) {}

    @Override
    public void onEnable() {
        keyPressed = false;
        isSwapping = false;
        originalSlot = -1;
        previousChestArmor = ItemStack.EMPTY;
        swappingToElytra = true;
        currentState = SwapState.IDLE;
    }

    @Override
    public void onDisable() {
        if (isSwapping) finishHotswap();
        previousChestArmor = ItemStack.EMPTY;
    }

    private enum SwapState {
        IDLE,
        SWITCHING_TO_ITEM,
        EQUIPPING_ITEM,
        SWITCHING_BACK
    }
}