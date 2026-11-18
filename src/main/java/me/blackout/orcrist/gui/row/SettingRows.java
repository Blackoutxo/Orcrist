package me.blackout.orcrist.gui.row;

import me.blackout.orcrist.gui.row.settings.BoolRow;
import me.blackout.orcrist.settings.BoolSetting;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class SettingRows {
    private SettingRows() {}

    public static List<SettingRow> createFor(Module module) {
        List<SettingRow> rows = new ArrayList<>();

        for (Setting<?> setting : module.getSettings()) {
            SettingRow row = create(setting);
            if (row != null) rows.add(row);
        }

        return rows;
    }

    private static SettingRow create(Setting<?> s) {
        if (s instanceof BoolSetting b) return new BoolRow(b);

        return null;
    }
}
