/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.utils.entity;

import net.minecraft.world.entity.Entity;

public class TargetUtils {
    public static boolean isValidTarget(Entity entity) {
        if (entity == null) return false;
        return (entity.isAlive() && !entity.isSpectator() && entity.isAttackable());
    }
}
