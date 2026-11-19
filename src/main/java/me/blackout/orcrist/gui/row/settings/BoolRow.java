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

    public BoolRow(BoolSetting setting) {
        super(setting);
        this.bool = setting;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        boolean on = bool.get();
        renderer.text(graphics, bool.name, x + 1, y, 9, on ? new Color(255, 255, 255) : new Color(255, 255, 255).darker());

        int px = x + w - 5 - P_WIDTH;
        int py = y + (HEIGHT - P_HEIGHT) / 2;
        double t = Utils.animate(0, on ? 1 : 0, 18);

        Renderer2D.quadRoundedH(graphics, px, py, P_WIDTH, P_HEIGHT, on ? GuiValues.BTN_ON : GuiValues.BTN_OFF);
        Renderer2D.circle(graphics, px + t * P_WIDTH, py * P_HEIGHT / 2.0, KNOB, GuiValues.KNOB);
    }

    @Override
    protected boolean onLeft(double mouseX, int x, int w) {
        bool.toggle();
        return true;
    }
}
