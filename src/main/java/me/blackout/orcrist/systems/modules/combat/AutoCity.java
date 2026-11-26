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
import me.blackout.orcrist.utils.player.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

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

    private Item[] item = new Item[]{Items.NETHERITE_PICKAXE, Items.DIAMOND_PICKAXE};

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
    public void onActivate() {
        initialToggle();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        FindItemResult result = InvUtils.find(item);
        timer++;

        if (!result.found() && !result.isHotbar()) return;
        max = (int) BlockUtils.getState(blockPosTarget).getBlock().getSpeedFactor();

        int currentSlot = mc.player.getInventory().getSelectedSlot();
        boolean isAir = BlockUtils.getState(blockPosTarget).isAir();

        if (mode.get() == HitMode.Vanilla) {
            InvUtils.setSlot(result.slot());

            if (rotate.get()) Rotations.rotateTo(new Vec3(blockPosTarget.getX(), blockPosTarget.getY(), blockPosTarget.getZ()));
        }

        if (first) {
            BlockUtils.breakBlock(blockPosTarget, true);
            first = false;
        }

        if (mode.get() == HitMode.Instant) {
            if (isAir) instCount++;
            else {
                instCount = 0;

                if (timer >= max && currentSlot == result.slot()) {
                    mc.player.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPosTarget, direction));

                    if (swing.get()) PlayerUtils.swing();
                }
            }
        }

        if (target == null && timer >= max && PlayerUtils.distanceToPos(blockPosTarget) > breakRange.get()) {
            if (selfToggle.get()) toggle();
            else initialToggle();
        }
    }

    private Player getTarget() {
        List<Player> targets = new ArrayList<>();

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Player) && TargetUtils.isValidTarget(entity)) {

                boolean noBlock = true;
                BlockPos targetBlockPos = target.getOnPos();

                for (BlockPos pos : EntityUtils.getCityBlocks(target)) {
                    if (!BlockUtils.isAir(pos) && BlockUtils.getState(pos).getBlock() == Blocks.BEDROCK) noBlock = false;
                }

                if (!noBlock || breakBurrow.get() && !BlockUtils.isAir(targetBlockPos)) targets.add(target);
            }
        }

        if (targets.isEmpty()) return null;
        else {
            targets.sort((e1, e2) -> TargetUtils.comparator(priority.get()).compare(e1, e2));
            return targets.get(0);
        }
    }

    private void initialToggle() {
        FindItemResult obsidian = InvUtils.find(Items.OBSIDIAN);
        target = getTarget();

        if (target == null) {
            error("Target not found, disabling.");
            toggle();
            return;
        }

        blockPosTarget = EntityUtils.getCityBlock(target, breakRange.get(), throughWalls.get());

        // BlockPos check
        if (blockPosTarget == null) {
            error("Can't find city block, disabling...");
            toggle();
            return;
        }

        if (support.get()) {
            if (BlockUtils.getState(blockPosTarget.below()).getBlock() == Blocks.BEDROCK) return;
            if (!obsidian.found()) return;
            BlockUtils.place(blockPosTarget.below(), obsidian, targetRange.get(), throughWalls.get(), swing.get(), rotate.get());
        }

        first = true;
        timer = 0;
        instCount = 0;
    }

    private enum HitMode {
        Vanilla,
        Instant,
        Packet,
    }
}
