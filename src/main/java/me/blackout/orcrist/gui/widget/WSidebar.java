package me.blackout.orcrist.gui.widget;

import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.utils.Utils;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;

public class WSidebar extends Widget {
    private static final int MARGIN = 6, SPACING = 2;

    private static final Color SIDEBAR = new Color(22, 22, 33);
    private final Color HOVER = new Color(133, 121, 121, 115);

    private GuiRenderer renderer;

    public WSidebar(GuiRenderer renderer) {
        this.renderer = renderer;
    }

    private int rowY(int index) {
        return y + MARGIN + index * (25 + SPACING);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int cx, int cy) {
        Renderer2D.quadWH(graphics, x, y, w, h, SIDEBAR);

        Category[] all = Category.values();

        for (int i = 0; i < all.length; i++) {
            int rowy = rowY(i);

            boolean selected = all[i] == renderer.category;
            boolean hover = Utils.hovered(cx, cy, x, rowy, w, 25);

            if (selected || hover) {
                Renderer2D.quadWH(graphics, x, rowy, w, 25, selected ? new Color(0, 0, 0) : new Color(255, 255, 255));
            }
            if (selected) Renderer2D.quadWH(graphics, x, rowy, 2, 25, new Color(40, 70, 178));

            renderer.text(graphics, all[i].name, x + 16, rowy, 25, selected ? new Color(255, 255, 255) : new Color(203, 201, 201));
        }
        renderer.text(graphics, "Orcrist", x + 8, y + h - 20, 14, new Color(203, 201, 201));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Category[] all = Category.values();
        for (int i = 0; i < all.length; i++) {
            if (Utils.hovered(mouseX, mouseY, x, rowY(i), w, 25)) {
                renderer.category = all[i];
                return true;
            }
        }
        return false;
    }
}
