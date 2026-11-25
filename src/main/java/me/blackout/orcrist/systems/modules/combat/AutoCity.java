/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.systems.modules.combat;

import me.blackout.orcrist.eventBus.EventHandler;
import me.blackout.orcrist.events.TickEvent;
import me.blackout.orcrist.settings.*;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.utils.entity.EntityUtils;
import me.blackout.orcrist.utils.entity.TargetPriority;
import me.blackout.orcrist.utils.entity.TargetUtils;
import me.blackout.orcrist.utils.player.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class AutoCity extends Module {

    public final Setting<Double> targetRange = add(new DoubleSetting.Builder()
            .name("T Range")
            .description("The range in which player should get targetted.")
            .defaultValue(5.0)
            .minValue(0.0)
            .maxValue(6.0)
            .build()
    );

    public final Setting<Double> breakRange = add(new DoubleSetting.Builder()
            .name("Range")
            .description("Range in which blocks are allowed to break.")
            .defaultValue(5.0)
            .minValue(0.0)
            .maxValue(6.0)
            .build()
    );

    public final Setting<Boolean> selfToggle = add(new BoolSetting.Builder()
            .name("Self Toggle")
            .description("Automatically toggles off after task completion.")
            .defaultValue(true)
            .build()
    );

    public final Setting<TargetPriority> priority = add(new EnumSetting.Builder<TargetPriority>()
            .name("Priority")
            .description("What kind of target to prioritize")
            .defaultValue(TargetPriority.LowestDistance)
            .build()
    );

    public final Setting<HitMode> mode = add(new EnumSetting.Builder<HitMode>()
            .name("Mode")
            .description("How to mine the block.")
            .defaultValue(HitMode.Packet)
            .build()
    );

    public final Setting<Boolean> delayed = add(new BoolSetting.Builder()
            .name("Delayed Switch")
            .description("Switches to pickaxes only if the block is ready to be broken.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Integer> delay = add(new IntSetting.Builder()
            .name("Delay")
            .description("Delay between mining blocks in ticks.")
            .defaultValue(0)
            .minValue(0)
            .maxValue(10)
            .build()
    );

    public final Setting<Boolean> breakBurrow = add(new BoolSetting.Builder()
            .name("Break Burrow")
            .description("Automatically breaks target's burrow.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Boolean> throughWalls = add(new BoolSetting.Builder()
            .name("Through Walls")
            .description("Only breaks visible sides.")
            .defaultValue(false)
            .build()
    );

    public final Setting<Boolean> support = add(new BoolSetting.Builder()
            .name("Support")
            .description("Will add support blocks around target city block if none.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Boolean> rotate = add(new BoolSetting.Builder()
            .name("Rotate")
            .description("Rotates to block of interaction.")
            .defaultValue(false)
            .build()
    );

    public final Setting<Boolean> swing = add(new BoolSetting.Builder()
            .name("Swing")
            .description("Renders client side swing.")
            .defaultValue(false)
            .build()
    );

    private Player target;
    private BlockPos blockPosTarget;
    private BlockPos targetPos;

    private Direction direction;
    private int timer;
    private int max;
    private int instCount;

    private boolean first;

    public AutoCity() {
        super(Category.Combat, "Auto City", "Automatically mines target's surround block");
    }

    @Override
    public void onActivate() {}

    @EventHandler
    private void onTick(TickEvent.Pre event) {

    }

    private Player getTarget() {
        List<Player> targets = new ArrayList<>();

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Player) && TargetUtils.isValidTarget(entity)) {

                boolean noBlock = true;
                BlockPos targetBlockPos = target.getOnPos();

                for (BlockPos pos : EntityUtils.getCityBlock(target)) {
                    if (!BlockUtils.isAir(pos) && BlockUtils.getState(pos).getBlock() == Blocks.BEDROCK) noBlock = false;
                }

                if (!noBlock || breakBurrow.get() && !BlockUtils.isAir(targetBlockPos)) targets.add(target);
            }
        }

        if (targets.isEmpty()) return null;
        else {
            targets.sort((e1, e2) -> TargetUtils.comparator(priority.get()));
        }
    }

    private enum HitMode {
        Vanilla,
        Packet
    }
}
