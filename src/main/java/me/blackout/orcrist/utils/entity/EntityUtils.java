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
import java.util.List;

import static me.blackout.orcrist.Orcrist.mc;

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

    public static BlockPos getCityBlock(Player player, double range, boolean throughWalls) {
        for (BlockPos pos : getCityBlocks(player)) {
            if (!(mc.player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > range)) continue;
            if (throughWalls && BlockUtils.canHitFace(pos, mc.player.getDirection())) return pos;
        }

        return player.getOnPos();
    }
}
