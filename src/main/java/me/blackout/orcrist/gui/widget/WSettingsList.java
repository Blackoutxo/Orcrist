package me.blackout.orcrist.gui.widget;

import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.gui.row.SettingRow;
import me.blackout.orcrist.gui.row.SettingRows;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
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
        int rowY = y - scroll;

        for (SettingRow row : rows) {
            if (rowY + SettingRow.HEIGHT >= y && rowY <= y + h) row.render(graphics, x, rowY, w, mouseX, mouseY);
            rowY += STEP;
        }

        Renderer2D.stopScissor(graphics);
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
}
