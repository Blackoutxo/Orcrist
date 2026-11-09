package me.blackout.orcrist.utils.render;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import net.minecraft.IdentifierException;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.awt.*;

import static me.blackout.orcrist.Orcrist.mc;

public class Renderer2D {

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
