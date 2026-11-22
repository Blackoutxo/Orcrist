/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.gui.widget;

import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.gui.row.SettingRow;
import me.blackout.orcrist.gui.row.SettingRows;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.utils.Utils;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WSettingsList extends WScrollable {
    private static final int STEP = SettingRow.HEIGHT + GuiValues.GAP;

    private final GuiRenderer renderer;
    private final Map<Module, List<SettingRow>> rowCache = new HashMap<>();
    private Module lastSelected;

    public WSettingsList(GuiRenderer renderer) {
        this.renderer = renderer;
    }

    private List<SettingRow> rows() {
        if (renderer.selected == null) return List.of();
        return rowCache.computeIfAbsent(renderer.selected, SettingRows::createFor);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (renderer.selected == null) return;

        if (renderer.selected != lastSelected) {
            scroll = 0;
            lastSelected = renderer.selected;
        }

        List<SettingRow> rows = rows();
        clampScroll(rows.size() * STEP);

        Renderer2D.startScissor(graphics, x, y, w, h);

        // Description
//        renderer.text(graphics, lastSelected.description, x, y, 7, GuiValues.TEXT);
//        Renderer2D.horizontalGradient(graphics, x, y + 10, x + ((double) w / 2), y + 9, new Color(255, 255, 255, 10), new Color(255, 255, 255));
//        Renderer2D.horizontalGradient(graphics, x + w, y + 10, (x + w) - ((double) w / 2), y + 9, new Color(255, 255, 255), new Color(255, 255, 255, 10));

        SettingRow hovered = null;
        int rowY = y - scroll;

        for (SettingRow row : rows) {
            if (rowY + SettingRow.HEIGHT >= y && rowY <= y + h) {
                row.render(graphics, x, rowY, w, mouseX, mouseY);

                int nameWidth = renderer.width(row.name(), 9);
                if (contains(mouseX, mouseY) && Utils.hovered(mouseX, mouseY, x, rowY, nameWidth, SettingRow.HEIGHT)) hovered = row;
            }

            rowY += STEP;
        }

        Renderer2D.stopScissor(graphics);

        if (hovered != null) drawToolTip(graphics, hovered.description(), mouseX, mouseY);
    }

    private void drawToolTip(GuiGraphicsExtractor graphics, String text, int mouseX, int mouseY) {
        int textWidth = renderer.width(text, 6);
        double wd = (double) (textWidth) / 2;

        // Container box
        Renderer2D.quadWH(graphics, mouseX + 11, mouseY + 11, textWidth + 1, 12, GuiValues.SIDEBAR_BG);

        // Top box gradient
        Renderer2D.horizontalGradient(graphics, mouseX + 11, mouseY + 10, (mouseX + wd), mouseY + 11, new Color(255, 255, 255, 20), new Color(255, 255, 255));
        Renderer2D.horizontalGradient(graphics, mouseX + textWidth, mouseY + 10, (mouseX + textWidth + 1) - wd, mouseY + 11, new Color(255, 255, 255), new Color(255, 255, 255, 20));

        Renderer2D.horizontalGradient(graphics, mouseX + 11, mouseY + 23, (mouseX + wd), mouseY + 24, new Color(255, 255, 255, 20), new Color(255, 255, 255));
        Renderer2D.horizontalGradient(graphics, mouseX + textWidth, mouseY + 23, (mouseX + textWidth + 1) - wd, mouseY + 24, new Color(255, 255, 255), new Color(255, 255, 255, 20));

        renderer.text(graphics, text, mouseX + 12, mouseY + 12, 6, GuiValues.TEXT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<SettingRow> rows = rows();
        int clickIndex = -1;

        if (contains(mouseX, mouseY)) {
            int relP = (int) (mouseY - (y - scroll));
            int index = relP / STEP;

            if (index >= 0 && index < rows.size() && relP % STEP < SettingRow.HEIGHT) clickIndex = index;
        }

        for (int i = 0; i < rows.size(); i++) {
            if (i != clickIndex) rows.get(i).onFocusLost();
        }

        if (clickIndex >= 0) return rows.get(clickIndex).click(mouseX, button, x, w);

        return contains(mouseX, mouseY);
    }

    @Override
    public void mouseReleased() {
        for (SettingRow row : rows()) row.mouseReleased();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        for (SettingRow row : rows()) {
            if (row.keyPressed(event)) return true;
        }

        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        for (SettingRow row : rows()) if (row.charTyped(event)) return true;
        return false;
    }
}
