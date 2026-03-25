package com.example.novaclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static int findItemInHotbar(Item item) {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static void switchToSlot(int slot) {
        if (mc.player == null) return;
        if (slot < 0 || slot > 8) return;
        mc.player.getInventory().selectedSlot = slot;
    }

    public static int findItem(Item item) {
        if (mc.player == null) return -1;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static boolean hasWeapon(Class<?> weaponClass) {
        if (mc.player == null) return false;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (weaponClass.isInstance(stack.getItem())) {
                return true;
            }
        }
        return false;
    }

    public static void swapToWeapon(Class<?> weaponClass) {
        if (mc.player == null) return;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (weaponClass.isInstance(stack.getItem())) {
                mc.player.getInventory().selectedSlot = i;
                return;
            }
        }
    }
}