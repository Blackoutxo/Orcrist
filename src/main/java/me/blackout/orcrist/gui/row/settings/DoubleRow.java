/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.gui.row.settings;

import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.gui.row.SettingRow;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.settings.DoubleSetting;
import me.blackout.orcrist.utils.Utils;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class DoubleRow extends SettingRow {
    private final DoubleSetting number;
//    private String format;
    private double fill;

    private boolean dragging;

    private final GuiRenderer renderer = new GuiRenderer();

    private int NAME_W = 50, BOX_W = 40;

    public DoubleRow(DoubleSetting setting) {
        super(setting);
        this.number = setting;
    }

    private int trackX(int x) {
        return x + NAME_W;
    }

    private int trackW(int w) {
        return w - NAME_W - BOX_W - 10;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        if (dragging) setFromMouse(mouseX, x, w);

        renderer.text(graphics, number.name, x + 1, y, HEIGHT, GuiValues.TEXT);

        fill = Utils.animate(number.get(), number.progress(), 20);

        int tx = trackX(x);
        int ty = y + 10;
        int tw = trackW(w);

        Renderer2D.quadWH(graphics, tx, ty, tw, 1, GuiValues.TRACK);
        Renderer2D.horizontalGradient(graphics, tx, ty, tx + tw * fill, ty + 1, GuiValues.TF_START, GuiValues.TF_END);
        Renderer2D.circle(graphics, tx + tw * fill, ty, 7, GuiValues.TEXT);

        int boxX = x + w - BOX_W;
        Renderer2D.quadWH(graphics, boxX, y + 4, BOX_W, 12, GuiValues.VALBOX);
        renderer.text(graphics, number.get().toString(), boxX + 4, y, HEIGHT, GuiValues.TEXT);
    }

    private void setFromMouse(double mouseX, int x, int w) {
        double p = Math.max(0, Math.min(1, (mouseX - trackX(x))) / trackW(w));
        number.set(number.minValue + p * (number.maxValue - number.minValue));
    }

    @Override
    public void mouseReleased() {
        dragging = false;
    }
}
