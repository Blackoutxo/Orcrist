package me.blackout.orcrist.gui.theme;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.gui.GUI;
import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.gui.widget.WModuleList;
import me.blackout.orcrist.gui.widget.WSettingsList;
import me.blackout.orcrist.gui.widget.WSidebar;
import me.blackout.orcrist.gui.widget.Widget;
import me.blackout.orcrist.utils.Utils;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class OrcristGuiTheme {
    public int x, y;
    private double prevX, prevY;
    private boolean dragging;

    private GUI gui;

    private final GuiRenderer renderer = new GuiRenderer();
    private final WSidebar sidebar = new WSidebar(renderer);
    private final WModuleList moduleList = new WModuleList(renderer);
    private final WSettingsList settingsList = new WSettingsList(renderer);

    private final List<Widget> widgets;

    public OrcristGuiTheme(GUI gui, int x, int y) {
        this.x = x;
        this.y = y;

        this.prevX = x;
        this.prevY = y;

        this.gui = gui;
        this.widgets = List.of(sidebar, moduleList, settingsList);
    }

    // Set layout
    private void layout() {
        int midX = x + 108;
        int top = y + 24;
        int bottom = y + 272;
        int listTop = top + 8;
        int rightX = x + GuiValues.CARD_W + 20;
        int rightW = x + GuiValues.UI_W - GuiValues.PAD - rightX;

        sidebar.setBound(x, y, GuiValues.SIDEBAR_W, GuiValues.UI_H);
        moduleList.setBound(midX, listTop, GuiValues.CARD_W, bottom - listTop);
        settingsList.setBound(rightX, top, rightW - 20, bottom - top);
    }

    // Render
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        x = (int) Math.round(prevX);
        y = (int) Math.round(prevY);
        layout();

        int contentX = x + GuiValues.SIDEBAR_W;
        int contentW = GuiValues.UI_W - GuiValues.SIDEBAR_W;

        Renderer2D.quadWH(graphics, x - 2, y - 2, GuiValues.UI_W + 1, GuiValues.UI_H + 3, GuiValues.C_BG);
        Renderer2D.quadWH(graphics, contentX, y, contentW, GuiValues.STRIP_H, new Color(21, 26, 43));

        Renderer2D.verticalGradient(graphics, contentX, y + GuiValues.STRIP_H, x + GuiValues.UI_W - 2, y + GuiValues.UI_H - 1, new Color(24, 33, 63), new Color(43, 68, 145));
        Renderer2D.verticalGradient(graphics, contentX, y + 28, x + GuiValues.UI_W - 1, y + 33, new Color(142, 168, 224), new Color(46, 51, 73));

        for (Widget w : widgets) w.render(graphics, mouseX, mouseY);
    }

    // Mouse clicked
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean insideWindow = Utils.hovered(mouseX, mouseY, x, y, GuiValues.UI_W, GuiValues.UI_H);
        boolean consumed = false;

        for (Widget w : widgets) consumed |= w.mouseClicked(mouseX, mouseY, button);

        boolean header = Utils.hovered(mouseX, mouseY, x + GuiValues.SIDEBAR_W, y, GuiValues.UI_W - GuiValues.SIDEBAR_W, GuiValues.STRIP_H);
        if (header && button == InputConstants.MOUSE_BUTTON_LEFT) {
            dragging = true;
            return true;
        }

        return insideWindow;
    }

    // Mouse drag
    public boolean mouseDragged(double dragX, double dragY) {
        if (!dragging) return false;
        prevX = Math.max(0, Math.min(prevX + dragX, gui.width - GuiValues.UI_W));
        prevY = Math.max(0, Math.min(prevY + dragY, gui.height - GuiValues.UI_H));
        return true;
    }

    // Mouse released
    public void mouseReleased() {
        dragging = false;
        for (Widget w : widgets) w.mouseReleased();
    }

    // Mouse Scrolled
    public void mouseScrolled(double mouseX, double mouseY, double amount) {
        for (Widget w : widgets) if (w.mouseScrolled(mouseX, mouseY, amount)) return;
    }

    // Key pressed
    public void keyPressed(KeyEvent event) {
        for (Widget w : widgets) w.keyPressed(event);
    }
}
