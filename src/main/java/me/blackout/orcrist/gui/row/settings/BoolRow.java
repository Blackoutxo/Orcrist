/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.gui.row.settings;

import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.gui.row.SettingRow;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.settings.BoolSetting;
import me.blackout.orcrist.utils.Utils;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;

public class BoolRow extends SettingRow {
    private final int P_WIDTH = 12, P_HEIGHT = 10, KNOB = 8;
    // Pill is the knob's sliding place

    private final BoolSetting bool;
    private final GuiRenderer renderer = new GuiRenderer();

    private double knob;

    public BoolRow(BoolSetting setting) {
        super(setting);
        this.bool = setting;

        this.knob = setting.get() ? 1 : 0;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        boolean on = bool.get();
        renderer.text(graphics, bool.name, x + 1, y + (HEIGHT - 9) / 2, 9, on ? new Color(255, 255, 255) : new Color(255, 255, 255).darker());

        knob = Utils.animate(knob, on ? 1 : 0, 18);

        int px = x + w - 5 - P_WIDTH;
        int py = y + (HEIGHT - P_HEIGHT) / 2;

        Renderer2D.quadRoundedH(graphics, px, py, P_WIDTH, P_HEIGHT, on ? GuiValues.BTN_ON : GuiValues.BTN_OFF);

        double knobX = (px - 3) + P_HEIGHT / 2.0 + knob * (P_WIDTH - 3);
        double knobY = py + P_HEIGHT / 2.0;
        Renderer2D.circle(graphics, knobX, knobY, KNOB, on ? GuiValues.KNOB.darker() : GuiValues.KNOB);
    }

    @Override
    protected boolean onLeft(double mouseX, int x, int w) {
        bool.toggle();
        return true;
    }
}
