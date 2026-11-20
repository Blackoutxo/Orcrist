/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.gui.row;

import me.blackout.orcrist.gui.row.settings.BoolRow;
import me.blackout.orcrist.gui.row.settings.KeybindRow;
import me.blackout.orcrist.settings.BoolSetting;
import me.blackout.orcrist.settings.KeybindSetting;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class SettingRows {
    private SettingRows() {}

    public static List<SettingRow> createFor(Module module) {
        List<SettingRow> rows = new ArrayList<>();
        KeybindSetting keyBind = null;

        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof KeybindSetting k && k.name.startsWith("Key Bind")) {
                keyBind = k;
                continue;
            }

            SettingRow row = create(setting);
            if (row != null) rows.add(row);
        }

        if (keyBind != null && keyBind.name.startsWith("Key Bind")) rows.add(new KeybindRow(keyBind));
        return rows;
    }

    private static SettingRow create(Setting<?> s) {
        if (s instanceof BoolSetting b) return new BoolRow(b);

        return null;
    }
}