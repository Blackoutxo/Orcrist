package me.blackout.orcrist.gui.theme;

import me.blackout.orcrist.gui.GUI;
import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.gui.widget.WModuleList;
import me.blackout.orcrist.gui.widget.WSettingsList;
import me.blackout.orcrist.gui.widget.WSidebar;
import me.blackout.orcrist.gui.widget.Widget;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;
import java.util.List;

public class OrcristGuiTheme {
    public int x, y;
    private boolean dragging;
    private int dragX, dragY;

    private final GuiRenderer renderer = new GuiRenderer();
    private final WSidebar sidebar = new WSidebar(renderer);
    private final WModuleList moduleList = new WModuleList(renderer);
    private final WSettingsList settingsList = new WSettingsList(renderer);

    private final List<Widget> widgets;

    public OrcristGuiTheme(GUI gui, int x, int y) {
        this.x = x;
        this.y = y;

        this.widgets = List.of(sidebar, moduleList, settingsList);
    }

    // Set layout
    private void layout() {
        int midX = x + 108;
        int top = y + 24;
        int bottom = y + 272;
        int listTop = top + 8;
        int rightX = x + 140;

        sidebar.setBound(x, y, GuiValues.SIDEBAR_W, GuiValues.UI_H);
        moduleList.setBound(midX, listTop, 132, bottom - listTop);
        settingsList.setBound(rightX, top, x + GuiValues.UI_W - GuiValues.PAD - rightX, bottom - top);
    }

    // Render
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
        layout();

        int contentX = x + GuiValues.SIDEBAR_W;
        int contentW = GuiValues.UI_W - GuiValues.SIDEBAR_W;

        Renderer2D.quadWH(graphics, contentX, y, contentW, GuiValues.STRIP_H, new Color(21, 26, 43));

    }
}
