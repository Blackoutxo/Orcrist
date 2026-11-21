/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.utils.player;

import net.minecraft.world.InteractionHand;

import static me.blackout.orcrist.Orcrist.mc;

public record FindItemResult(int slot, int count) {

    public boolean found() {
        return slot != -1;
    }

    public boolean isHotbar() {
        return slot >= 0 && slot <= 8;
    }

    public boolean isMain() {
        return slot >= 9 && slot <= 35;
    }

    public boolean isMainHand() {
        return mc.player != null && isHotbar() && slot == mc.player.getInventory().getSelectedSlot();
    }

    public boolean isOffhand() {
        return slot == 40;
    }

    public InteractionHand getHand() {
        if (isMainHand()) return InteractionHand.MAIN_HAND;
        if (isOffhand()) return InteractionHand.OFF_HAND;

        return null;
    }

    public static FindItemResult empty() {
        return new FindItemResult(-1, 0);
    }
}
