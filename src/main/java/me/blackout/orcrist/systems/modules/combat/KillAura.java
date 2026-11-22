/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.systems.modules.combat;

import me.blackout.orcrist.eventBus.EventHandler;
import me.blackout.orcrist.events.TickEvent;
import me.blackout.orcrist.settings.BoolSetting;
import me.blackout.orcrist.settings.DoubleSetting;
import me.blackout.orcrist.settings.EnumSetting;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.utils.entity.TargetPriority;
import me.blackout.orcrist.utils.entity.TargetUtils;
import me.blackout.orcrist.utils.player.FindItemResult;
import me.blackout.orcrist.utils.player.InvUtils;
import me.blackout.orcrist.utils.player.Rotations;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SwingAnimation;

public class KillAura extends Module {

    public final Setting<Double> range = add(new DoubleSetting.Builder()
            .name("Range")
            .description("Proximity from player the target has to be to attack.")
            .defaultValue(4.5)
            .minValue(1)
            .maxValue(10)
            .build()
    );

    public final Setting<TargetPriority> priority = add(new EnumSetting.Builder<TargetPriority>()
            .name("Priority")
            .description("How to sort through targets.")
            .defaultValue(TargetPriority.LowestDistance)
            .build()
    );

    public final Setting<Boolean> weaponOnly = add(new BoolSetting.Builder()
            .name("Weapon Only")
            .description("Hits with only weapon.")
            .defaultValue(false)
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

    private Item[] item = new Item[]{Items.WOODEN_SWORD, Items.STONE_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD, Items.COPPER_SWORD, };

    public KillAura() {
        super(Category.Combat, "Kill Aura", "Automatically kills entities nearby!");
    }

    @Override
    public void onActivate() {}

    @EventHandler
    private void onTick(TickEvent.Post event) {
        FindItemResult swords = InvUtils.find(item);
        LivingEntity target = TargetUtils.getTarget(range.get(), throughWalls.get(), priority.get());

        if (mc.player == null || mc.level == null || target == null) return;
        if (!swords.found() && weaponOnly.get()) return;

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (target == entity) attack(living);
        }
    }

    private void entityCheck(LivingEntity entity) {

    }

    private void attack(LivingEntity entity) {
        if (mc.player.getAttackStrengthScale(0.5f) < 1.0f) return;
        if (rotate.get()) Rotations.rotate(entity);

        mc.gameMode.attack(mc.player, entity);

        // Swing
        ItemStack held = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
        SwingAnimation anim = held.getAttackAnimation();

        mc.player.swing(InteractionHand.MAIN_HAND, anim, false);
        mc.player.connection.send(ServerboundPunchPacket.INSTANCE);
    }
}