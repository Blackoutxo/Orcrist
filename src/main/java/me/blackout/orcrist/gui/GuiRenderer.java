/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.gui;

import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.utils.render.text.Fonts;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.awt.*;

public class GuiRenderer {
    private final Color WHITE = new Color(255, 255, 255);

    public static Identifier ARROW;
    public static Identifier CIRCLE;
    public static Identifier TRIANGLE;

    public Category category;
    public Module selected;

    public void init() {
        ARROW = Identifier.fromNamespaceAndPath("orcrist", "icons/elements/arrow.png");
        CIRCLE = Identifier.fromNamespaceAndPath("orcrist", "icons/elements/circle.png");
        TRIANGLE = Identifier.fromNamespaceAndPath("orcrist", "icons/elements/triangle.png");
    }

    // Text helpers
    public int width(String text, int size) {
        return Fonts.get(size).width(text);
    }

    public void text(GuiGraphicsExtractor graphics, String text, int x, int y, int size, Color textColor) {
        Fonts.get(size).draw(graphics, text, x, y, textColor);
    }

    public void textShadow(GuiGraphicsExtractor graphics, String text, int x, int y, int size, Color textColor) {
        Fonts.get(size).drawShadow(graphics, text, x, y, textColor);
    }
}
