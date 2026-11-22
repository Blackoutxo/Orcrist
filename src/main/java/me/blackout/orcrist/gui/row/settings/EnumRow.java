/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.gui.row.settings;

import me.blackout.orcrist.settings.EnumSetting;

public class EnumRow<E extends Enum<E>> extends LabelRow {
    private final EnumSetting<E> setting;

    public EnumRow(EnumSetting<E> setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    protected String valueText() {
        return setting.get().toString();
    }

    @Override
    public boolean onLeft(double mouseX, int x, int w) {
        setting.cycle();
        return true;
    }

    @Override
    public boolean onRight() {
        setting.prev();
        return true;
    }
}
