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
import me.blackout.orcrist.utils.entity.TargetPriority;
import me.blackout.orcrist.utils.entity.TargetUtils;
import me.blackout.orcrist.utils.player.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
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

    public final Setting<Integer> airTime = add(new IntSetting.Builder()
            .name("Time")
            .description("The amount of time the block should stay air to toggle off.")
            .defaultValue(60)
            .minValue(1)
            .maxValue(60)
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
            .description("Mines where blocks aren't visible.")
            .defaultValue(false)
            .build()
    );

    public final Setting<Boolean> pauseOneEat = add(new BoolSetting.Builder()
            .name("Pause On Eat")
            .description("Pauses module while eating.")
            .defaultValue(true)
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
    private BlockPos cityPos, targetFeet;

    private Direction direction;
    private int timer, breakTicks, airTicks, attempts, prevSlot = -1;
    private boolean started;

    public AutoCity() {
        super(Category.Combat, "Auto City", "Automatically mines target's surround block");
    }

    @Override
    public void onActivate() {
        if (mc.player == null || mc.level == null) toggle();
        prevSlot = mc.player.getInventory().getSelectedSlot();
        if (!init()) toggle();
    }

    @Override
    public void onDeactivate() {
        if (prevSlot != -1) InvUtils.setSlot(prevSlot);
        target = null; cityPos = null; direction = null; prevSlot = -1;
    }

    private boolean init() {

        // Target
        target = findTarget();
        if (target == null) {
            error("Couldn't find target, disabling.");
            toggle();
            return false;
        }

        // City
        cityPos = findCity(target);
        if (cityPos == null) {
            error("Couldn't find city block, disabling.");
            toggle();
            return false;
        }

        // Reach & Raycast
        direction = BlockUtils.getBreakHit(cityPos, breakRange.get(), throughWalls.get()).orElse(null);
        if (direction == null) {
            error("Can't reach city block, disabling.");
            toggle();
            return false;
        }

        // Inventory
        FindItemResult pickaxe = InvUtils.find(item);
        boolean creative = mc.player.getAbilities().instabuild;

        if (!creative && (!pickaxe.found() || !pickaxe.isHotbar())) {
            error("Couldn't find pickaxe in hotbar, disabling.");
            toggle();
            return false;
        }

        // Block break tick
        breakTicks = breakTick(cityPos, pickaxe);
        if (breakTicks < 0) {
            error("Block can't be broken, disabling.");
            toggle();
            return false;
        }

        // Support
        if (support.get()) placeSupport();

        targetFeet = target.blockPosition();
        timer = 0; airTicks = 0; attempts = 0; started = false;
        info("Attempting to city " + target.getName().getString());
        return true;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || target == null || mc.level == null || cityPos == null) return;
        if (pauseOneEat.get() && mc.player.isUsingItem()) return;

        boolean creative = mc.player.getAbilities().instabuild;
        boolean isAir = BlockUtils.isAir(cityPos);

        if (target.isAlive() || !target.blockPosition().equals(targetFeet)
                || BlockUtils.reach(cityPos) > breakRange.get()
                || (isAir && mode.get() == HitMode.Instant)) {
            finish();
            return;
        }

        if (mode.get() == HitMode.Instant) {
            airTicks = isAir ? airTicks + 1 : 0;
            if (airTicks >= airTime.get()) finish();
            if (isAir) return;
        }

        FindItemResult pickaxe = InvUtils.find(item);
        if (!creative && (!pickaxe.found() || !pickaxe.isHotbar())) {
            error("Can't find pickaxe, disabling.");
            toggle();
            return;
        }

        timer++;
        boolean ready = timer >= breakTicks;
        Vec3 point = BlockUtils.faceCenter(cityPos, direction);

        boolean tool = mode.get() == HitMode.Vanilla || !delayed.get() || ready;
        if (!creative && tool && mc.player.getInventory().getSelectedSlot() != pickaxe.slot()) {
            InvUtils.setSlot(pickaxe.slot());
        }

        boolean holdingTool = creative || mc.player.getInventory().getSelectedSlot() == pickaxe.slot();

        switch (mode.get()) {
            case Vanilla -> {
                if (rotate.get()) Rotations.rotate(point);
                mc.gameMode.continueDestroyBlock(cityPos, direction);
                if (swing.get()) PlayerUtils.swing();
            }

            case Packet, Instant -> {
                if (!started) {
                    if (rotate.get()) Rotations.rotate(point);
                    sendAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK);
                    if (swing.get()) PlayerUtils.swing();

                    started = true;
                    timer = 0;
                    return;
                }

                if (!ready || holdingTool) return;
                if (mode.get() == HitMode.Instant && delay.get() > 0 && timer % delay.get() != 0) return;

                if (rotate.get()) Rotations.rotate(point);
                sendAction(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK);
                if (swing.get()) PlayerUtils.swing();

                if (mode.get() == HitMode.Packet && ++attempts > 5) {
                    info("Server rejected block breaking, disabling.");
                    toggle();
                }
            }
        }
    }

    private void finish() {
        if (selfToggle.get() || !init()) toggle();
    }

    private void sendAction(ServerboundPlayerActionPacket.Action action) {
        mc.player.connection.send(new ServerboundPlayerActionPacket(action, cityPos, direction));
    }

    private Player findTarget() {
        List<Player> targets = new ArrayList<>();

        for (Player p : mc.level.players()) {
            if (p == mc.player || p.isCreative() && !TargetUtils.isValidTarget(p)) continue;
            if (PlayerUtils.distanceTo(p) > targetRange.get()) continue;
            if (findCity(p) == null) continue;
            targets.add(p);
        }

        if (targets.isEmpty()) return null;
        targets.sort(TargetUtils.comparator(priority.get()));
        return targets.get(0);
    }

    private BlockPos findCity(Player player) {
        BlockPos feet = player.blockPosition();
        List<BlockPos> sides = new ArrayList<>();

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos pos = feet.relative(dir);
            if (!BlockUtils.hasAirNeighbour(pos, feet)) continue;
            if (BlockUtils.getBreakHit(pos, breakRange.get(), throughWalls.get()).isPresent()) sides.add(pos);
        }

        if (!sides.isEmpty()) {
            sides.sort(Comparator.comparingDouble(BlockUtils::reach));
            return sides.get(0);
        }

        if (breakBurrow.get()
                && BlockUtils.getState(feet).getBlock() != Blocks.COBWEB
                && BlockUtils.getBreakHit(feet, breakRange.get(), throughWalls.get()).isPresent()) return feet;

        return null;
    }

    private void placeSupport() {
        BlockPos below = cityPos.below();
        if (!BlockUtils.isReplacable(below)) return;

        FindItemResult obsidian = InvUtils.find(Items.OBSIDIAN);
        if (!obsidian.found() || !obsidian.isHotbar()) {
            info("No obsidian found, skipping support.");
            return;
        }

        if (!BlockUtils.place(below, obsidian, breakRange.get(), throughWalls.get(), swing.get(), rotate.get()))
            error("Couldn't place support!");
    }

    private int breakTick(BlockPos pos, FindItemResult pickaxe) {
        if (mc.player.getAbilities().instabuild) return 1;

        var inv = mc.player.getInventory();
        int old = inv.getSelectedSlot();
        inv.setSelectedSlot(pickaxe.slot());

        float progress = BlockUtils.getState(pos).getDestroyProgress(mc.player, mc.level, pos);
        inv.setSelectedSlot(old);

        return progress <= 0 ? -1 : (int) Math.ceil(1.0 / progress);
    }

    private enum HitMode {
        Vanilla,
        Instant,
        Packet,
    }
}
