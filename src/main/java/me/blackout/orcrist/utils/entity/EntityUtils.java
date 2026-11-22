/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.utils.entity;

import net.minecraft.world.entity.LivingEntity;

public class EntityUtils {

    public static boolean isInLava(LivingEntity entity) {
        return entity.isInLava();
    }

    public static boolean isOnFire(LivingEntity entity) {
        return entity.isOnFire();
    }

    public static double getTotalHealth(LivingEntity entity) {
        return (entity.getHealth() + entity.getAbsorptionAmount());
    }

    public static boolean isLowHealth(LivingEntity entity, double threshold) {
        return (getTotalHealth(entity) <= threshold);
    }
}
