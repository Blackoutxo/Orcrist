/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.utils.entity;

import me.blackout.orcrist.utils.player.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EntityUtils {
    private static final Direction[] HORIZONTAL = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

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

    // City block
    public static List<BlockPos> getCityBlocks(Player player) {
        List<BlockPos> cityPos = new ArrayList<>();
        BlockPos leg = player.blockPosition();

        for (Direction dir : HORIZONTAL) {
            BlockPos pos = leg.relative(dir);

            if (!BlockUtils.isBreakable(pos)) continue;
            if (!BlockUtils.hasAirNeighbour(pos, leg)) continue;

            cityPos.add(pos);
        }

        return cityPos;
    }

    public static BlockPos getCity(Player player, double range, boolean throughWalls) {
        BlockPos feet = player.blockPosition();
        List<BlockPos> sides = new ArrayList<>();

        for (Direction dir : HORIZONTAL) {
            BlockPos pos = feet.relative(dir);
            if (!BlockUtils.hasAirNeighbour(pos, feet)) continue;
            if (BlockUtils.getBreakHit(pos, range, throughWalls).isPresent()) sides.add(pos);
        }

        if (!sides.isEmpty()) {
            sides.sort(Comparator.comparingDouble(BlockUtils::reach));
            return sides.get(0);
        }

        return null;
    }
}
