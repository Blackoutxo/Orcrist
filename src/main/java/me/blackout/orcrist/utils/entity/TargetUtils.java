/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.utils.entity;

import me.blackout.orcrist.utils.player.PlayerUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static me.blackout.orcrist.Orcrist.mc;

public class TargetUtils {
    public static boolean isValidTarget(Entity entity) {
        if (entity == null) return false;
        return (entity.isAlive() && !entity.isSpectator() && entity.isAttackable());
    }

    public static Comparator<LivingEntity> comparator(TargetPriority priority) {
        return switch (priority) {
            case LowestHealth -> Comparator.comparingDouble(EntityUtils::getTotalHealth);
            case HighestHealth -> Comparator.comparingDouble(EntityUtils::getTotalHealth).reversed();
            case LowestDistance -> Comparator.comparingDouble(PlayerUtils::distanceTo);
            case HighestDistance -> Comparator.comparingDouble(PlayerUtils::distanceTo).reversed();
        };
    }

    public static List<LivingEntity> getTargets(double range, boolean throughWall, TargetPriority priority) {
        if (mc.player == null || mc.level == null) return List.of();

        List<LivingEntity> targets = new ArrayList<>();
        for (Entity e : mc.level.entitiesForRendering()) {
            if (e == mc.player) continue;
            if (!(e instanceof LivingEntity living)) continue;
            if (!isValidTarget(living)) continue;
            if (!PlayerUtils.inRange(living, range, throughWall)) continue;
            targets.add(living);
        }

        targets.sort(comparator(priority));
        return targets;
    }

    public static LivingEntity getTarget(double range, boolean throughWall, TargetPriority priority) {
        List<LivingEntity> targets = getTargets(range, throughWall, priority);
        return targets.isEmpty() ? null : targets.get(0);
    }
}
