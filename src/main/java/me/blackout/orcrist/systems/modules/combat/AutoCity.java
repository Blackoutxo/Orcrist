/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.systems.modules.combat;

import me.blackout.orcrist.settings.BoolSetting;
import me.blackout.orcrist.settings.DoubleSetting;
import me.blackout.orcrist.settings.EnumSetting;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.utils.entity.TargetPriority;
import me.blackout.orcrist.utils.entity.TargetUtils;
import me.blackout.orcrist.utils.player.BlockUtils;
import me.blackout.orcrist.utils.player.FindItemResult;
import me.blackout.orcrist.utils.player.InvUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class AutoCity extends Module {

    public final Setting<HitMode> mode = add(new EnumSetting.Builder<HitMode>()
            .name("Hit mode")
            .description("How to break target's block")
            .defaultValue(HitMode.Packet)
            .build()
    );

    public final Setting<TargetPriority> priority = add(new EnumSetting.Builder<TargetPriority>()
            .name("Priority")
            .description("Which target to give the most priority to.")
            .defaultValue(TargetPriority.LowestDistance)
            .build()
    );

    public final Setting<Double> range = add(new DoubleSetting.Builder()
            .name("Range")
            .description("Proximity from player the target has to be to attack.")
            .defaultValue(4.5)
            .minValue(1)
            .maxValue(10)
            .build()
    );

    public final Setting<Boolean> overridePriority = add(new BoolSetting.Builder()
            .name("Target ping")
            .description("Targets the player with highest ping.")
            .defaultValue(false)
            .build()
    );

    public final Setting<Boolean> placeCrystal = add(new BoolSetting.Builder()
            .name("Place Crystal")
            .description("Places crystal next to city block.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Boolean> swing = add(new BoolSetting.Builder()
            .name("Swing")
            .description("Whether to show swing animation or not.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Boolean> rotate = add(new BoolSetting.Builder()
            .name("Rotate")
            .description("Rotates to position of place or break.")
            .defaultValue(true)
            .build()
    );

    private Item[] items = new Item[]{Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE};
    private Player target;
    private BlockPos cityPos;

    public AutoCity() {
        super(Category.Combat, "Auto City", "Automatically mines target's surround block");
    }

    @Override
    public void onActivate() {
        mine();
    }

    @Override
    public void onDeactivate() {
        cityPos = null;
        target = null;
    }

    private void mine() {
        FindItemResult pickaxes = InvUtils.find(items);
        target = (Player) TargetUtils.getTarget(range.get(), true, priority.get());

        if (!pickaxes.found()) {
            info("No pickaxes found in hotbar, toggling....");
            toggle();
            return;
        }

        InvUtils.setSlot(pickaxes.slot());

        if (target == null) {
            info("No targets found, toggling....");
            toggle();
            return;
        }

        cityPos = findPos(target);

        BlockUtils.breakBlock(cityPos, range.get(), true, true, rotate.get());
    }

    private BlockPos findPos(Player player) {
        BlockPos feet = player.blockPosition();

        for (Direction dir : Direction.values()) {
            BlockPos pos = feet.relative(dir);

            if (!BlockUtils.isBreakable(pos)) continue;
            if (!BlockUtils.hasAirNeighbour(pos, feet)) continue;
            if (placeCrystal.get()) placeCrystal(pos);

            return pos;
        }

        return null;
    }

    private void placeCrystal(BlockPos pos) {
        FindItemResult crystal = InvUtils.find(Items.END_CRYSTAL);
        if (!crystal.found() && placeCrystal.get()) {
            info("No crystals found continuing anyway,");
            return;
        }

        BlockUtils.place(pos.below(), crystal, range.get(), true, swing.get(), rotate.get());
    }

    public enum HitMode {
        Vanilla,
        Packet
    }
}
