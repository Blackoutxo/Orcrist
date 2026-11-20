/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.systems.modules.combat;

import me.blackout.orcrist.settings.BoolSetting;
import me.blackout.orcrist.settings.DoubleSetting;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;

public class KillAura extends Module {

    public Setting<Boolean> entities = add(new BoolSetting.Builder()
            .name("Entities")
            .description("target other entities as well!")
            .defaultValue(false)
            .build()
    );

    public Setting<Double> range = add(new DoubleSetting.Builder()
            .name("Range")
            .description("ATCK range")
            .defaultValue(5)
            .minValue(1)
            .maxValue(7)
            .build()
    );

    public KillAura() {
        super("Kill Aura", "Automatically kills entities nearby!", Category.Combat);
    }
}
