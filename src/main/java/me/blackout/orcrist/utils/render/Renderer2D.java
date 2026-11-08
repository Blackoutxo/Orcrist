package me.blackout.orcrist.utils.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;

public class Renderer2D {

    // Lines
    public static void drawHorizontalLine(GuiGraphicsExtractor graphics, int x, int fromY, int toY, Color color) {
        graphics.horizontalLine(x, fromY, toY, color.getRGB());
    }

    public static void drawVerticalLine(GuiGraphicsExtractor graphics, int x, int fromY, int toY, Color color) {
        graphics.verticalLine(x, fromY, toY, color.getRGB());
    }

    // Quads
    public static void quad(GuiGraphicsExtractor graphics, int x, int y, int width, int height, Color color) {
        fill(graphics, x, y, x + width, y + height, color);
    }

    public static void outlinedQuad(GuiGraphicsExtractor graphics, int x, int y, int width, int height, Color color) {
        fill(graphics, x, y, x + width, y + height, color);
        graphics.outline(x, y, width, height, color.getRGB());
    }

    public static void fill(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, Color color) {
        graphics.fill(x1, y1, x2, y2, color.getRGB());
    }
}
