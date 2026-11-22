/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.systems.modules.combat;

import me.blackout.orcrist.eventBus.EventHandler;
import me.blackout.orcrist.events.TickEvent;
import me.blackout.orcrist.settings.BoolSetting;
import me.blackout.orcrist.settings.DoubleSetting;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.utils.entity.TargetUtils;
import me.blackout.orcrist.utils.player.FindItemResult;
import me.blackout.orcrist.utils.player.InvUtils;
import me.blackout.orcrist.utils.player.PlayerUtils;
import me.blackout.orcrist.utils.player.Rotations;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class KillAura extends Module {

    public final Setting<Double> range = add(new DoubleSetting.Builder()
            .name("Range")
            .description("Proximity from player the target has to be to attack.")
            .defaultValue(4.5)
            .minValue(1)
            .maxValue(10)
            .build()
    );

//    public final Setting<Boolean> invisibles = add(new BoolSetting.Builder()
//            .name("Invisibles")
//            .description("Hits invisible targets.")
//            .defaultValue(true)
//            .build()
//    );

    public final Setting<Boolean> weaponOnly = add(new BoolSetting.Builder()
            .name("Weapon Only")
            .description("Hits with only weapon.")
            .defaultValue(false)
            .build()
    );

    public final Setting<Boolean> criticalHits = add(new BoolSetting.Builder()
            .name("Critical Only")
            .description("Hits only crits.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Boolean> throughWalls = add(new BoolSetting.Builder()
            .name("Through Walls")
            .description("Hits targets through walls.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Boolean> rotate = add(new BoolSetting.Builder()
            .name("Rotate")
            .description("Whether to rotate to targets or not.")
            .defaultValue(true)
            .build()
    );

    private Item[] item = new Item[]{Items.WOODEN_SWORD, Items.STONE_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD, Items.COPPER_SWORD};

    public KillAura() {
        super(Category.Combat, "Kill Aura", "Automatically kills entities nearby!");
    }

    @Override
    public void onActivate() {}

    @EventHandler
    private void onTick(TickEvent.Post event) {
        FindItemResult swords = InvUtils.find(item);

        if (mc.player == null || mc.level == null) return;
        if (!swords.found() && weaponOnly.get()) return;

        if (swords.found() && !swords.isHotbar()) {
            InvUtils.swapWithHotbar(swords.slot(), 1);
            InvUtils.setSlot(1);
        } else if (swords.isHotbar()) InvUtils.setSlot(swords.slot());

        for (Entity entity : mc.level.entitiesForRendering())
            attack(entity);
    }

    private void attack(Entity entity) {
        if (!TargetUtils.isValidTarget(entity)) return;
        if (!PlayerUtils.inRange(entity, range.get(), throughWalls.get())) return;
        if (mc.player.getAttackStrengthScale(0.5f) < 1.0f) return;
        if (entity.asLivingEntity().getHealth() > 15) return;

        if (rotate.get()) Rotations.rotateTo(new Vec3(entity.getX(), entity.getEyeY(), entity.getZ()));

        mc.gameMode.attack(mc.player, entity);
    }
}