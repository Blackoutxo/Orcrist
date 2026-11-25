/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.utils.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static me.blackout.orcrist.Orcrist.mc;

public class BlockUtils {

    public static BlockState getState(BlockPos pos) {
        return mc.level.getBlockState(pos);
    }

    public static boolean isAir(BlockPos pos) {
        return getState(pos).isAir();
    }

    public static boolean isLiquid(BlockPos pos) {
        return !getState(pos).getFluidState().isEmpty();
    }

    public static boolean isSolid(BlockPos pos) {
        return !getState(pos).getCollisionShape(mc.level, pos).isEmpty();
    }

    public static boolean isReplacable(BlockPos pos) {
        return getState(pos).canBeReplaced();
    }

    public static boolean isBreakable(BlockPos pos) {
        BlockState state = getState(pos);
        return !state.isAir() && !isLiquid(pos) && state.getDestroySpeed(mc.level, pos) >= 0;
    }

    // Raycasting and checks
    public static double reach(BlockPos pos) {
        Vec3 eye = mc.player.getEyePosition();
        double x = Math.max(pos.getX(), Math.min(eye.x, pos.getX() + 1));
        double y = Math.max(pos.getY(), Math.min(eye.y, pos.getY() + 1));
        double z = Math.max(pos.getZ(), Math.min(eye.z, pos.getZ() + 1));
        return eye.distanceTo(new Vec3(x, y, z));
    }

    public static Vec3 faceCenter(BlockPos pos, Direction face) {
        return Vec3.atCenterOf(pos).add(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
    }

    public static boolean canHitFace(BlockPos pos, Direction face) {
        Vec3 eye = mc.player.getEyePosition();
        Vec3 target = faceCenter(pos, face);
        Vec3 end = target.add(target.subtract(eye).normalize().scale(0.1));

        BlockHitResult hitResult = mc.level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.player));
        return hitResult.getType() == HitResult.Type.BLOCK
                && hitResult.getBlockPos().equals(pos)
                && hitResult.getDirection() == face;
    }

    // Place
    public static boolean place(BlockPos pos, FindItemResult item, double range, boolean throughWalls) {
        return place(pos, item, range, throughWalls, true, true);
    }

    public static boolean place(BlockPos pos, FindItemResult item, double range, boolean throughWalls, boolean swing, boolean rotate) {
        return place(pos, item, InteractionHand.MAIN_HAND, range, throughWalls, swing, rotate);
    }

    public static boolean place(BlockPos pos, FindItemResult item, InteractionHand hand, double range, boolean throughWalls, boolean swing, boolean rotate) {
        Optional<BlockHitResult> result = getHitPlace(pos, range, throughWalls);
        if (result.isEmpty()) return false;

        if (item.found()) InvUtils.setSlot(item.slot());

        if (rotate) Rotations.rotate(result.get().getLocation());

        mc.gameMode.useItemOn(mc.player, hand, result.get());

        ItemStack stack = mc.player.getItemInHand(hand);
        SwingAnimation animation = stack.getInteractAnimation();
        if (swing) mc.player.swing(hand, animation, false);

        return true;
    }

    public static Optional<BlockHitResult> getHitPlace(BlockPos pos, double range, boolean throughWalls) {
        if (!isReplacable(pos)) return Optional.empty();

        Vec3 eye = mc.player.getEyePosition();
        List<BlockHitResult> candidates = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            BlockPos neighbour = pos.relative(dir);
            if (!isSolid(neighbour)) continue;

            Direction face = dir.getOpposite();
            if (reach(neighbour) > range) continue;
            if (!throughWalls && !canHitFace(neighbour, face)) continue;

            candidates.add(new BlockHitResult(faceCenter(neighbour, face), face, neighbour, false));
        }

        return candidates.stream().min(Comparator.comparingDouble(h -> h.getLocation().distanceToSqr(eye)));
    }

    // Break
    public static boolean breakBlock(BlockPos pos, boolean swing) {
        return breakBlock(pos, true, swing, true);
    }

    public static boolean breakBlock(BlockPos pos, boolean throughWalls, boolean swing, boolean rotate) {
        return breakBlock(pos, mc.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), throughWalls, swing, rotate);
    }

    public static boolean breakBlock(BlockPos pos, double range, boolean throughWalls, boolean swing, boolean rotate) {
        Optional<Direction> dir = getBreakHit(pos, range, throughWalls);
        if (dir.isEmpty()) return false;

        if (rotate) Rotations.rotate(faceCenter(pos, dir.get()));

        mc.gameMode.continueDestroyBlock(pos, dir.get());

        ItemStack stack = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
        SwingAnimation animation = stack.getInteractAnimation();
        if (swing) mc.player.swing(InteractionHand.MAIN_HAND, animation, false);

        return true;
    }

    public static Optional<Direction> getBreakHit(BlockPos pos, double range, boolean throughWalls) {
        if (!isBreakable(pos) || reach(pos) > range) return Optional.empty();

        Vec3 eye = mc.player.getEyePosition();
        Direction best = null;
        double bestDist = Double.MAX_VALUE;

        for (Direction face : Direction.values()) {
            if (!throughWalls && !canHitFace(pos, face)) continue;

            double dist = faceCenter(pos, face).distanceToSqr(eye);
            if (dist < bestDist) {
                bestDist = dist;
                best = face;
            }
        }

        return Optional.ofNullable(best);
    }

    // Neighbour check
    public static boolean hasNeighbour(BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (isSolid(pos.relative(dir))) return true;
        }
        return false;
    }

    public static boolean hasAirNeighbour(BlockPos pos, BlockPos exclude) {
        for (Direction dir : Direction.values()) {
            BlockPos neighbour = pos.relative(dir);
            if (neighbour.equals(exclude)) continue;
            if (isAir(neighbour)) return true;
        }
        return false;
    }

    public static List<Direction> getSolidNeighbour(BlockPos pos) {
        List<Direction> list = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            if (isSolid(pos.relative(dir))) list.add(dir);
        }

        return list;
    }
}
