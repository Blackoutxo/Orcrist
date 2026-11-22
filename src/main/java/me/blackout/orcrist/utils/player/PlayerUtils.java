/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.utils.player;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static me.blackout.orcrist.Orcrist.mc;

public class PlayerUtils {

    // Health & survival
    public static boolean inLava() {
        return mc.player.isInLava();
    }

    public static boolean onFire() {
        return mc.player.isOnFire();
    }

    public static double getTotalHealth() {
        return (mc.player.getHealth() + mc.player.getAbsorptionAmount());
    }

    public static boolean isLowHealth(double threshold) {
        return getTotalHealth() <= threshold;
    }

    // Movement
    public static boolean isMoving() {
        return mc.player.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6;
    }

    public static boolean isOnGround() {
        return mc.player.onGround();
    }

//    public static double speed() {
//
//    }

    public static boolean isSprinting() {
        return mc.player.isSprinting();
    }

    // Distance and visibility
    public static boolean inRange(Entity entity, double range, boolean throughWall) {
        return throughWall ? distanceTo(entity) <= range : ((distanceTo(entity) <= range) && canSee(entity));
    }

    public static double distanceTo(Entity entity) {
        if (mc.player == null || entity == null) return 0;

        AABB box = entity.getBoundingBox();
        double dx = mc.player.getX() - Mth.clamp(mc.player.getX(), box.minX, box.maxX);
        double dy = mc.player.getY() - Mth.clamp(mc.player.getEyeY(), box.minY, box.maxY);
        double dz = mc.player.getZ() - Mth.clamp(mc.player.getZ(), box.minZ, box.maxZ);

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static boolean canSee(Entity entity) {
        if (mc.player == null || entity == null || mc.level == null) return false;

        Vec3 from = mc.player.getEyePosition();
        AABB box = entity.getBoundingBox();

        double cx = (box.minX + box.maxX) / 2.0;
        double cz = (box.minZ + box.maxZ) / 2.0;

        return clear(from, new Vec3(cx, entity.getEyeY(), cz))
                || clear(from, new Vec3(cx, (box.minY + box.maxY) / 2.0, cz))
                || clear(from, new Vec3(cx, box.minY + 0.1, cz));
    }

    private static boolean clear(Vec3 from, Vec3 to) {
        return mc.level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc.player)).getType() == HitResult.Type.MISS;
    }
}
