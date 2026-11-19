package me.blackout.orcrist.gui.widget;

import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.utils.Utils;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.awt.*;

public class WSidebar extends Widget {
    private static final int MARGIN = 6, SPACING = 2;
    private GuiRenderer renderer;

    public WSidebar(GuiRenderer renderer) {
        this.renderer = renderer;
    }

    private int rowY(int index) {
        return y + MARGIN + index * (GuiValues.CATEGORY_H + SPACING);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int cx, int cy) {
        Renderer2D.quadWH(graphics, x, y, w, h, GuiValues.SIDEBAR_BG);

        Category[] all = Category.values();

        for (int i = 0; i < all.length; i++) {
            int rowy = rowY(i) + 5;

            boolean selected = all[i] == renderer.category;
            boolean hover = Utils.hovered(cx, cy, x, rowy, w, GuiValues.CATEGORY_H);

            if (selected || hover) {
                Renderer2D.quadWH(graphics, x, rowy, w - 2, GuiValues.CATEGORY_H, selected ? GuiValues.CTG_SEL : GuiValues.CTG_HOV);
            }

            if (selected) Renderer2D.verticalGradient(graphics, x, rowy, x + 2, rowy + GuiValues.CATEGORY_H, new Color(140, 237, 235, 1), new Color(45, 71, 128, 1));

            Renderer2D.icon(graphics, Identifier.fromNamespaceAndPath("orcrist", all[i].path), 64, x + 8, rowy + GuiValues.CATEGORY_H / 2, 13, 0, new Color(255, 255, 255));
            renderer.text(graphics, all[i].name, x + 20, rowy, 11, selected ? GuiValues.TEXT : GuiValues.TEXT_DIM);
        }
        renderer.text(graphics, "Orcrist", x + 8, y + h - 20, 9, new Color(253, 253, 253, 148));
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
