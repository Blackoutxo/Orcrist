package me.blackout.orcrist.utils.render;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.awt.*;

import static me.blackout.orcrist.Orcrist.mc;

public class Renderer2D {

    // Icons
    private static final Identifier ARROW = Identifier.fromNamespaceAndPath("orcrist", "icons/elements/arrow.png");
    private static final Identifier CIRCLE = Identifier.fromNamespaceAndPath("orcrist", "icons/elements/circle.png");
    private static final Identifier TRIANGLE = Identifier.fromNamespaceAndPath("orcrist", "icons/elements/triangle.png");

    // Text
    private static final Style style = Style.EMPTY.withFont(
            FontDescription.CODEC
                    .parse(JsonOps.INSTANCE, new JsonPrimitive("orcrist:spacegrotesk"))
                    .result()
                    .orElseThrow()
    );

    private static Component styled(String text) {
        return Component.literal(text).setStyle(style);
    }

    public static void text(GuiGraphicsExtractor graphics, String text, int x, int y, Color color) {
        graphics.text(mc.font, styled(text), x, y, color.getRGB());
    }

    // Texture icon
    public static void icon(GuiGraphicsExtractor graphics, Identifier tex, int texSize, double x, double y, double size, float rotation, Color color) {
        graphics.pose().pushMatrix();
        graphics.pose().translate((float) x, (float) y);
        graphics.pose().rotate((float) Math.toRadians(rotation));
        float s = (float) (size / texSize);

        graphics.pose().scale(s, s);
        graphics.pose().translate(-texSize / 2f, -texSize / 2f);

        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, 0, 0, 0, 0, texSize, texSize, texSize, texSize, color.getRGB());

        graphics.pose().popMatrix();
    }

    public static void arrow(GuiGraphicsExtractor graphics, double x, double y, double size, float rotation, Color color) {
        icon(graphics, ARROW, 32, x, y, size, rotation, color);
    }

    public static void circle(GuiGraphicsExtractor graphics, double x, double y, double size, Color color) {
        icon(graphics, CIRCLE, 64, x, y, size, 0, color);
    }

    public static void triangle(GuiGraphicsExtractor graphics, double x, double y, double size, float rotation, Color color) {
        icon(graphics, TRIANGLE, 128, x, y, size, rotation, color);
    }

    // Rounded corners quads

    // Horizontally rounded quads (pill shaped)
    public static void quadRoundedH(GuiGraphicsExtractor graphics, int x, int y, int width, int height, Color color) {
        circle(graphics, x, y + height / 2.0, height, color);
        circle(graphics, x + width, y + height / 2.0, height, color);
        quadWH(graphics, x, y, width, height, color);
    }

    // Lines
    public static void drawHorizontalLine(GuiGraphicsExtractor graphics, int x, int fromY, int toY, Color color) {
        graphics.horizontalLine(x, fromY, toY, color.getRGB());
    }

    public static void drawVerticalLine(GuiGraphicsExtractor graphics, int x, int fromY, int toY, Color color) {
        graphics.verticalLine(x, fromY, toY, color.getRGB());
    }

    // Quads
    public static void quadWH(GuiGraphicsExtractor graphics, int x, int y, int width, int height, Color color) {
        quad(graphics, x, y, x + width, y + height, color);
    }

    public static void quad(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, Color color) {
        int ax = round(Math.min(x1, x2)), ay = round(Math.min(y1, y2));
        int bx = round(Math.max(x1, x2)), by = round(Math.max(y1, y2));
        graphics.fill(ax, ay, bx, by, color.getRGB());
    }

    public static void outlinedQuad(GuiGraphicsExtractor graphics, int x, int y, int width, int height, Color bgColor, Color outlineColor) {
        quad(graphics, x, y, x + width, y + height, bgColor);
        graphics.outline(x, y, width, height, outlineColor.getRGB());
    }

    public static void fill(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, Color color) {
        int ax = round(Math.min(x1, x2)), ay = round(Math.min(y1, y2));
        int bx = round(Math.min(x1, x2)), by = round(Math.min(y1, y2));
        graphics.fill(ax, ay, bx, by, color.getRGB());
    }

    // Gradients
    public static void verticalGradient(GuiGraphicsExtractor graphics, double x1, double y1, double x2, double y2, Color topColor, Color bottomColor) {
        graphics.fillGradient(round(x1), round(y1), round(x2), round(y2), topColor.getRGB(), bottomColor.getRGB());
    }

    public static void horizontalGradient(GuiGraphicsExtractor graphics, double x1, double y1, double x2, double y2, Color leftColor, Color rightColor) {
        int left = round(Math.min(x1, x2)), right = round(Math.max(x1, x2));
        int top = round(Math.min(y1, y2)), bottom = round(Math.max(y1, y2));
        int w = right - left;

        if (w <= 0) return;

        for (int i = 0; i < w; i++) {
            float t = w == 1 ? 0f : i / (float) (w - 1);
            graphics.fill(left + i, top, left + i + 1, bottom, lerpColor(leftColor.getRGB(), rightColor.getRGB(), t));
        }
    }

    // Scissors
    public static void startScissor(GuiGraphicsExtractor graphics, double x, double y, double width, double height) {
        graphics.enableScissor(round(x), round(y), round(x + width), round(y + height));
    }

    public static void stopScissor(GuiGraphicsExtractor graphics) {
        graphics.disableScissor();
    }

    // Helpers
    private static int round(double v) {
        return (int) Math.round(v);
    }

    private static int lerpColor(int a, int b, float t) {
        int aa = (a >>> 24) & 255, ar = (a >> 16) & 255, ag = (a >> 8) & 255, ab = a & 255;
        int ba = (b >>> 24) & 255, br = (b >> 16) & 255, bg = (b >> 8) & 255, bb = b & 255;
        return ((int) (aa + (ba - aa) * t) << 24)
                | ((int) (ar + (br - ar) * t) << 16)
                | ((int) (ag + (bg - ag) * t) << 8)
                |  (int) (ab + (bb - ab) * t);
    }
}
