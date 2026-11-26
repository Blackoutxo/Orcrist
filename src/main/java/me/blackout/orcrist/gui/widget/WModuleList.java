/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.gui.GuiRenderer;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.systems.modules.Modules;
import me.blackout.orcrist.utils.Utils;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.util.List;

public class WModuleList extends WScrollable {
    private static final int STEP = 30; // Card height dim with gaps

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
            if (cardY + GuiValues.CARD_H >= y & cardY <= y + h) {
                boolean hover = inside || Utils.hovered(mouseX, mouseY, x, cardY, w, GuiValues.CARD_H);
                drawCard(graphics, m, cardY, hover);
            }
            cardY += STEP;
        }

        Renderer2D.stopScissor(graphics);
    }

    private void drawCard(GuiGraphicsExtractor graphics, Module m, int cardY, boolean hover) {
        Color background = (m == renderer.selected) ? GuiValues.CARD_SEL : hover ? GuiValues.CARD_HOV : GuiValues.CARD;

        Renderer2D.quadWH(graphics, x, cardY, w, GuiValues.CARD_H, background);
        Renderer2D.quadWH(graphics, x, cardY + GuiValues.CARD_H, w, 2, new Color(24, 24, 24, 50));
        renderer.text(graphics, m.name, x + 5, cardY + 4, 10, GuiValues.TEXT);

        Renderer2D.quadWH(graphics, x + w - 10, cardY, 10, GuiValues.CARD_H, GuiValues.CARD.darker());
        if (!m.getSettings().isEmpty()) drawDots(graphics, x + w - 4, cardY + 13);
    }

    private void drawDots(GuiGraphicsExtractor graphics, int dotsX, int dotsY) {
        Renderer2D.icon(graphics, DOT, 64, dotsX, dotsY, 10, 0, new Color(255, 255, 255));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!contains(mouseX, mouseY)) return false;

        List<Module> modules = Modules.get(renderer.category, "");

        int rel = (int) (mouseY - (y - scroll));
        int index = rel / STEP;
        if (index < 0 || index >= modules.size() || rel % STEP >= GuiValues.CARD_H) return true;

        Module m = modules.get(index);
        boolean onDots = mouseX >= x + w - 20;

        if (button == InputConstants.MOUSE_BUTTON_LEFT && !onDots) {
            m.toggle();
        } else if ((button == InputConstants.MOUSE_BUTTON_RIGHT) && !m.getSettings().isEmpty()) {
            renderer.selected = (renderer.selected == m) ? null : m;
        }

        return true;
    }
}
