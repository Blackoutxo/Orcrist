/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.utils.player;

import me.blackout.orcrist.mixinInterface.IMultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

import static me.blackout.orcrist.Orcrist.mc;

public class InvUtils {
    public static int HOTBAR_START = 0, HOTBAR_END = 8;
    public static int MAIN_START = 9, MAIN_END = 35;

    public static int ARMOR_START = 36, ARMOR_END = 39;
    public static int OFFHAND = 40;

    public static int previousSlot = -1;

    private InvUtils() {}

    private static boolean ready() {
        return mc.player != null && mc.gameMode != null;
    }

    // Get items
    public static ItemStack getMainHand() {
        return mc.player == null ? ItemStack.EMPTY : mc.player.getMainHandItem();
    }

    public static ItemStack getOffhand() {
        return mc.player == null ? ItemStack.EMPTY : mc.player.getOffhandItem();
    }

    public static ItemStack getArmor(EquipmentSlot slot) {
        return mc.player == null ? ItemStack.EMPTY : mc.player.getItemBySlot(slot);
    }

    public static ItemStack getSlot(int slot) {
        if (mc.player == null || !isInventorySlot(slot)) return ItemStack.EMPTY;
        return mc.player.getInventory().getItem(slot);
    }

    public static int getSelectedSlot() {
        return mc.player == null ? -1 : mc.player.getInventory().getSelectedSlot();
    }

    // ----- Testing items ----- //
    public static boolean testInMainHand(Predicate<ItemStack> predicate) {
        return predicate.test(getMainHand());
    }

    public static boolean testInOffHand(Predicate<ItemStack> predicate) {
        return predicate.test(getOffhand());
    }

    public static boolean testInHand(Predicate<ItemStack> predicate) {
        return predicate.test(getMainHand()) || predicate.test(getOffhand());
    }

    public static boolean testInHotbar(Predicate<ItemStack> predicate) {
        if (mc.player == null) return false;
        if (testInOffHand(predicate)) return true;

        var inventory = mc.player.getInventory();
        for (int i = HOTBAR_START; i < HOTBAR_END; i++) {
            if (predicate.test(inventory.getItem(i))) return true;
        }

        return false;
    }

    public static boolean testInMainHand(Item... item) { return testInMainHand(stack -> isOneOf(stack, item)); }
    public static boolean testInOffhand(Item... item) { return testInOffHand(stack -> isOneOf(stack, item)); }
    public static boolean testInHands(Item... item) { return testInHand(stack -> isOneOf(stack, item)); }
    public static boolean testInHotbar(Item... item) { return testInHotbar(stack -> isOneOf(stack, item)); }

    private static boolean isOneOf(ItemStack stack, Item... items) {
        for (Item item : items) {
            if (stack.is(item)) return true;
        }

        return false;
    }

    // ----- Finding Items ----- //
    public static FindItemResult findEmpty() {
        return find(ItemStack::isEmpty);
    }

    public static FindItemResult find(Item... item) {
        return find(stack -> isOneOf(stack, item));
    }

    public static FindItemResult find(Predicate<ItemStack> predicate) {
        return find(predicate, HOTBAR_START, MAIN_END);
    }

    public static FindItemResult findAnywhere(Predicate<ItemStack> predicate) {
        return find(predicate, HOTBAR_START, OFFHAND);
    }

    public static FindItemResult find(Predicate<ItemStack> predicate, int start, int end) {
        if (mc.player == null) return FindItemResult.empty();

        start = Math.max(HOTBAR_START, start);
        end  = Math.min(OFFHAND, end);

        var inventory = mc.player.getInventory();
        int foundSlot = 0, count = 0;

        for (int i = start; i <= end; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!predicate.test(stack)) continue;

            if (foundSlot == -1) foundSlot = i;
            count += stack.count();
        }

        return new FindItemResult(foundSlot, count);
    }

    // Hotbar
    public static FindItemResult findInHotbar(Item... item) {
        return findInHotbar(stack -> isOneOf(stack, item));
    }

    public static FindItemResult findInHotbar(Predicate<ItemStack> predicate) {
        if (mc.player == null) return FindItemResult.empty();

        if (predicate.test(getOffhand())) {
            return new FindItemResult(OFFHAND, getOffhand().count());
        }

        return find(predicate, HOTBAR_START, HOTBAR_END);
    }

    // Main
    public static FindItemResult findInMain(Item... item) {
        return findInMain(stack -> isOneOf(stack, item));
    }

    public static FindItemResult findInMain(Predicate<ItemStack> predicate) {
        return find(predicate, MAIN_START, MAIN_END);
    }

    // Hotbar switch
    public static boolean setSlot(int slot) {
        if (!ready() || !isHotbar(slot)) return false;

        var inventory = mc.player.getInventory();
        if (inventory.getSelectedSlot() == slot) return true;

        inventory.setSelectedSlot(slot);
        ((IMultiPlayerGameMode) mc.gameMode).orcrist$syncSelected();
        return true;
    }

    public static boolean swap(int slot, boolean savePrevious) {
        if (!ready()) return false;
        if (slot == OFFHAND) return true;
        if (!isHotbar(slot)) return false;

        if (!savePrevious) previousSlot = -1;
        else if (previousSlot == -1) previousSlot = mc.player.getInventory().getSelectedSlot();

        return setSlot(slot);
    }

    public static boolean swapBack() {
        if (previousSlot == -1) return false;

        int slot = previousSlot;
        previousSlot = -1;
        return setSlot(slot);
    }

    public static int getPreviousSlot() {
        return previousSlot;
    }

    public static InteractionHand handFor(int slot) {
        return slot == OFFHAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    // ----- Inventory Item Move ----- //
    public static boolean move(int from, int to) {
        if (!ready() || from == to) return false;

        AbstractContainerMenu menu = mc.player.containerMenu;

        if (!menu.getCarried().isEmpty() || getSlot(from).isEmpty()) return false;

        int fromId = getMenuSlot(from);
        int toId = getMenuSlot(to);
        if (fromId == - 1 || toId == -1) return false;

        click(fromId, 0, ContainerInput.PICKUP);
        click(toId, 0, ContainerInput.PICKUP);

        if (!menu.getCarried().isEmpty()) click(fromId, 0, ContainerInput.PICKUP);
        return true;
    }

    // ----- Quick Swapping ----- //
    public static boolean swapWithHotbar(int invSlot, int hotBarSlot) {
        return isHotbar(hotBarSlot) && hotKeySwap(invSlot, hotBarSlot);
    }

    public static boolean swapWithOffhand(int invSlot) {
        return hotKeySwap(invSlot, OFFHAND);
    }

    public static boolean hotKeySwap(int invSlot, int button) {
        if (!ready()) return false;

        int menuSlot = getMenuSlot(invSlot);
        if (menuSlot == -1) return false;

        click(menuSlot, 0, ContainerInput.SWAP);
        return true;
    }

    public static boolean shiftClick(int invSlot) {
        if (!ready()) return false;

        int menuSlot = getMenuSlot(invSlot);
        if (menuSlot == -1) return false;

        click(menuSlot, 0, ContainerInput.QUICK_MOVE);
        return true;
    }

    // ----- Slot helper ----- //
    public static int armorIndex(EquipmentSlot slot) {
        return switch(slot) {
            case FEET -> 36;
            case LEGS -> 37;
            case CHEST -> 38;
            case HEAD -> 39;
            case OFFHAND -> OFFHAND;
            default -> -1;
        };
    }

    public static int getMenuSlot(int invSlot) {
        if (mc.player == null || !isInventorySlot(invSlot)) return -1;
        return mc.player.containerMenu.findSlot(mc.player.getInventory(), invSlot).orElse(-1);
    }

    // Click
    private static void click(int menuSlot, int button, ContainerInput input) {
        if (!ready()) return;
        mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, menuSlot, button, input, mc.player);
    }

    public static boolean isHotbar(int slot) { return slot >= HOTBAR_START && slot <= HOTBAR_END; }
    public static boolean isMain(int slot) { return slot >= MAIN_START && slot <= MAIN_END; }
    public static boolean isArmor(int slot) { return slot >= ARMOR_START && slot <= ARMOR_START; }
    public static boolean isInventorySlot(int slot) { return slot >= HOTBAR_START && slot <= OFFHAND; }

    public static MoveAction action() {
        return new MoveAction();
    }

    public static class MoveAction {
        private int from = -1;
    }
}
