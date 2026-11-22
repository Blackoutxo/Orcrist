/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.utils.player;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static me.blackout.orcrist.Orcrist.mc;

public class Rotations {
    public record Rotation(float yaw, float pitch) {}

    public enum AimPart { HEAD, BODY, FEET, NEAREST }

    public static Vec3 aimPoint(Entity target, AimPart part) {
        AABB box = target.getBoundingBox();

        double cx = (box.minX + box.maxX) / 2.0;
        double cz = (box.minZ + box.maxZ) / 2.0;

        return switch (part) {
            case HEAD -> new Vec3(cx, target.getEyeY(), cz);
            case BODY -> new Vec3(cx, (box.minY + box.maxY) / 2.0, cz);
            case FEET -> new Vec3(cz, box.minY + 0.1, cz);
            case NEAREST -> {
                Vec3 eye = mc.player.getEyePosition();
                yield new Vec3(Mth.clamp(eye.x, box.minX, box.maxX),
                               Mth.clamp(eye.y, box.minY, box.maxY),
                               Mth.clamp(eye.z, box.minZ, box.maxZ));
            }
        };
    }

    public static Rotation rotateTo(Vec3 target) {
        Vec3 eye = mc.player.getEyePosition();

        double dx = target.x - eye.x;
        double dy = target.y - eye.y;
        double dz = target.z - eye.z;

        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90);
        float pitch = (float) (-Math.toDegrees(Math.atan2(dy, horizontal)));

        return new Rotation(Mth.wrapDegrees(yaw), Math.clamp(pitch, -90f, 90f));
    }

    public static Rotation rotateTo(Entity target, AimPart part) {
        return rotateTo(aimPoint(target, part));
    }

    public static Rotation currentRotation() {
        return new Rotation(mc.player.getYRot(), mc.player.getXRot());
    }

    public static Rotation mouseSnap(Rotation from, Rotation to) {
        double s = mc.options.sensitivity().get() * 0.6 + 0.2;
        float step = (float) (s * s * s * 8.0 * 0.15);

        float yaw = from.yaw() + Math.round(Mth.wrapDegrees(to.yaw() - from.yaw()) / step) * step;
        float pitch = from.pitch() + Math.round(Mth.wrapDegrees(to.pitch() - from.pitch()) / step) * step;

        return new Rotation(yaw, Math.clamp(pitch, -90f, 90f));
    }

    public static Rotation rotateTo(Entity target) {
        return rotateTo(target.getBoundingBox().getCenter());
    }

    public static void look(Rotation r) {
        if (mc.player == null) return;

        mc.player.setYRot(r.yaw());
        mc.player.setXRot(r.pitch());
    }

    // Server side rotation
    public static void rotate(Entity entity) {
        Rotation rotation = rotateTo(entity);

        mc.player.connection.send(new ServerboundMovePlayerPacket.Rot(
                rotation.yaw, rotation.pitch,
                PlayerUtils.isOnGround(), mc.player.horizontalCollision
        ));
    }
}
