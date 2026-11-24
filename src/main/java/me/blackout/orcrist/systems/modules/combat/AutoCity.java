/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.systems.modules.combat;

import me.blackout.orcrist.settings.BoolSetting;
import me.blackout.orcrist.settings.EnumSetting;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.utils.entity.TargetPriority;

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

    public final Setting<Boolean> overridePriority = add(new BoolSetting.Builder()
            .name("Target ping")
            .description("Targets the player with highest ping.")
            .defaultValue(false)
            .build()
    );

    public final Setting<Boolean> swing = add(new BoolSetting.Builder()
            .name("Swing")
            .description("Whether to show swing animation or not.")
            .defaultValue(true)
            .build()
    );

    public AutoCity() {
        super(Category.Combat, "Auto City", "Automatically mines target's surround block");
    }

    public enum HitMode {
        Vanilla,
        Packet
    }
}
