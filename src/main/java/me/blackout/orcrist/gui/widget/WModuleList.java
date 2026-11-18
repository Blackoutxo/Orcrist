package me.blackout.orcrist.gui.widget;

import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.systems.modules.Modules;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.util.List;

public class WModuleList extends WScrollable {
    private static final int STEP = 30; // Card height dim with gaps
    private static final int DOTS = 20; // Dots area

    private GuiRenderer renderer;
    private Category lastCategory;

    private Identifier DOT = Identifier.fromNamespaceAndPath("orcrist", "icons/dots.png");

    public WModuleList(GuiRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        List<Module> modules = Modules.get(renderer.category, "");

        if (renderer.category != lastCategory) {
            scroll = 0;
            lastCategory = renderer.category;
        }
        clampScroll(modules.size() * STEP);

        boolean inside = contains(mouseX, mouseY);
        Renderer2D.startScissor(graphics, x, y, w, h);
        int cardY = y - scroll;

        for (Module m : modules) {
            if (cardY + 25 >= y & cardY <= y + h) {
                drawCard(graphics, m, cardY, inside);
            }
        }
    }

    private void drawCard(GuiGraphicsExtractor graphics, Module m, int cardY, boolean hover) {
        Color background = (m == renderer.selected) ? new Color(58, 70, 104) : hover ? new Color(50, 60, 87) : new Color(40, 48, 70);

        Renderer2D.quadWH(graphics, x, cardY, w, 26, background);
        renderer.text(graphics, m.name, x + 8, cardY, 26, m.isEnabled() ? new Color(255, 255, 255) : new Color(203, 201, 201));

        if (!m.getSettings().isEmpty()) drawDots(graphics, x + w - 12, cardY + 26 / 2 - 5);
    }

    private void drawDots(GuiGraphicsExtractor graphics, int dotsX, int dotsY) {
        Renderer2D.icon(graphics, DOT, 512, dotsX, dotsY, 2, 0, new Color(255, 255, 255));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return true;
    }
}
