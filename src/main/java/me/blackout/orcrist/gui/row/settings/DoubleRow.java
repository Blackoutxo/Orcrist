/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.gui.row.settings;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.gui.row.SettingRow;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.gui.utils.NumberEditor;
import me.blackout.orcrist.settings.DoubleSetting;
import me.blackout.orcrist.utils.Utils;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;

public class DoubleRow extends SettingRow {
    private final DoubleSetting number;
    private double fill;

    private boolean dragging;

    private final NumberEditor editor = new NumberEditor();
    private final GuiRenderer renderer = new GuiRenderer();

    private int NAME_W = 45, BOX_W = 30;

    private double lastShown = Double.NaN;
    private String shownText = "";

    public DoubleRow(DoubleSetting setting) {
        super(setting);
        this.number = setting;
        this.fill = setting.progress();
    }

    private int trackX(int x) {
        return x + NAME_W;
    }

    private int trackW(int w) {
        return w - NAME_W - BOX_W - 8;
    }

    private int boxX(int x, int w) {
        return x + w - BOX_W;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        if (dragging) setFromMouse(mouseX, x, w);

        renderer.text(graphics, number.name, x + 1, y + 3, 9, GuiValues.TEXT);

        // Fix ts
        double target = Math.max(0, Math.min(1, number.progress()));
        fill = dragging ? target : Math.max(0, Math.min(1, Utils.animate(fill, target, 20)));

        int tx = trackX(x);
        int ty = y + HEIGHT / 2;
        int tw = trackW(w);

        Renderer2D.quadWH(graphics, tx, ty, tw, 1, GuiValues.TRACK);
        Renderer2D.horizontalGradient(graphics, tx, ty, tx + tw * fill, ty + 1, GuiValues.TF_START, GuiValues.TF_END);
        Renderer2D.circle(graphics, tx + tw * fill, ty + 0.5, 7, GuiValues.TEXT);

        renderBox(graphics, boxX(x, w), y, y + 3);
    }

    private void renderBox(GuiGraphicsExtractor graphics, int boxX, int y, int textY) {
        Renderer2D.quadWH(graphics, boxX, y + 4, BOX_W, 12, GuiValues.VALBOX);

        String text = editor.isActive() ? editor.text() + "|" : shownValue();
        int textX = boxX + Math.max(2, (BOX_W - renderer.width(text, 9)) / 2);
        renderer.text(graphics, text, textX, textY, 9, GuiValues.TEXT);
    }

    private String shownValue() {
        double n = number.get();

        if (n != lastShown) {
            lastShown = n;
            shownText = Utils.format(n, 2);
        }

        return shownText;
    }

    @Override
    public boolean onLeft(double mouseX, int x, int w) {
        // Click on box
        if (mouseX >= boxX(x, w)) {
            editor.begin(shownValue());
            return true;
        }

        // Click on track check
        if (mouseX < trackX(x) - 5) return false;
        dragging = true;
        setFromMouse(mouseX, x, w);

        return true;
    }

    private void setFromMouse(double mouseX, int x, int w) {
        double p = Math.max(0, Math.min(1, (mouseX - trackX(x)) / trackW(w)));
        double raw = number.minValue + p * (number.maxValue - number.minValue);
        number.set(Utils.round(raw, 2));
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!editor.isActive()) return false;

        int key = event.key();
        if (key == InputConstants.KEY_NUMPADENTER || key == InputConstants.KEY_RETURN) commit();
        else if (key == InputConstants.KEY_ESCAPE) editor.cancel();
        else if (key == InputConstants.KEY_BACKSPACE) editor.backSpace();
        return true;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!editor.isActive()) return false;
        editor.type((char) event.codepoint());
        return true;
    }

    @Override
    public void onFocusLost() {
        if (editor.isActive()) editor.cancel();
    }

    private void commit() {
        double n = editor.commit();
        if (Double.isNaN(n)) return;
        n = Math.max(number.minValue, Math.min(number.maxValue, n));
        number.set(Utils.round(n, 2));
    }

    @Override
    public void mouseReleased() {
        dragging = false;
    }
}
