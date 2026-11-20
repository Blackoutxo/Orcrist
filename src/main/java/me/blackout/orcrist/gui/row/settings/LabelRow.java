/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.gui.row.settings;

import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.gui.row.SettingRow;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;

public abstract class LabelRow extends SettingRow {
    private GuiRenderer renderer = new GuiRenderer();

    public LabelRow(Setting<?> setting) {
        super(setting);
    }

    protected abstract String valueText();
    protected Color color() { return new Color(255, 255,255); }

    @Override
    public void render(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        Renderer2D.quadWH(graphics, x, y, w, HEIGHT, GuiValues.ROW);

        int textY = y + (HEIGHT - 8) / 2;
        renderer.text(graphics, setting.name, x + 6, textY, 9, new Color(255, 255, 255));

        String value = valueText();
        int valueX = x + w - 8 - renderer.width(value, 9);
        renderer.text(graphics, value, valueX, textY, 9, color());
    }
}
