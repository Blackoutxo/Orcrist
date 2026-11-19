package me.blackout.orcrist.gui.row.settings;

import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.gui.row.SettingRow;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;

public abstract class LabelRow extends SettingRow {
    private GuiRenderer renderer;

    public LabelRow(Setting<?> setting) {
        super(setting);
    }

    protected abstract String valueText();
    protected Color color() { return new Color(255, 255,255); }

    @Override
    public void render(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        Renderer2D.quadWH(graphics, x, y, w, HEIGHT, GuiValues.ROW);
        renderer.text(graphics, setting.name, x + 6, HEIGHT, 9, new Color(255, 255, 255));
        renderer.text(graphics, valueText(), w - 3, y, 9,color());
    }
}
