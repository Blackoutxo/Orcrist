package me.blackout.orcrist.utils.render.text;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static me.blackout.orcrist.Orcrist.mc;

public class GlyphFont {
    private static final String FONT_PATH = "/assets/orcrist/font/spacegrotesk.ttf";
    private static final int SCALE = 2;
    private static final int PAD = 2;
    private static final int FIRST = 32, LAST = 255;
    private static final int ATLAS_W = 1024;

    private static Font baseFont;

    private int size;
    private Identifier texId;

    private boolean attempted, ready;  // Bool checks to render

    private final int[] u = new int[LAST + 1], v = new int[LAST + 1];  // Glyph data in atlas pixels
    private final int[] cellW = new int[LAST + 1], adv = new int[LAST + 1];

    private int cellH, atlasH, lineHeight;

    public GlyphFont(int size) {
        this.size = size;
        this.texId = Identifier.fromNamespaceAndPath("orcrist", "font_atlas_" + size);
    }

    public void draw(GuiGraphicsExtractor graphics, String text, int x, int y, Color color) {
        if (text.isEmpty() || !ensureReady()) return;

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(1f / SCALE, 1f / SCALE);

        int pen = 0;
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i);
            if (c < FIRST || c > LAST) c = '?';
            graphics.blit(RenderPipelines.GUI_TEXTURED, texId, pen - PAD, 0, u[c], v[c], cellW[c], cellH, ATLAS_W, atlasH, color.getRGB());
            pen += adv[c];
        }

        graphics.pose().popMatrix();
    }

    public void drawShadow(GuiGraphicsExtractor graphics, String text, int x, int y, Color color) {
        Color shadow = new Color(color.getRed() / 4, color.getGreen() / 4, color.getBlue() / 4, color.getAlpha());
        draw(graphics, text, x + 1, y + 1, shadow);
        draw(graphics, text, x, y, color);
    }

    public int width(String text) {
        if (text == null || !ensureReady()) return 0;
        int w = 0;
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i);
            if (c < FIRST || c > LAST) c = '?';
            w += adv[c];
        }
        return (int) Math.ceil(w / (double) SCALE);
    }

    public int height(String text) {
        return ensureReady() ? lineHeight / SCALE : size;
    }

    // Baking atlas (Better performance than laggy slow rendering)
    private boolean ensureReady() {
        if (!attempted) {
            attempted = true;
            try {
                bake();
                ready = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ready;
    }

    private void bake() throws Exception {
        Font font = base().deriveFont(Font.PLAIN, (float) size * SCALE);

        Graphics2D img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
        hints(img);
        img.setFont(font);

        FontMetrics fm = img.getFontMetrics();
        int ascent = fm.getAscent(), descent = fm.getDescent();
        img.dispose();

        lineHeight = ascent + descent;
        cellH = lineHeight + PAD * 2;

        int x = 0, y = 0;
        for (int c = FIRST; c <= LAST; c++) {
            adv[c] = fm.charWidth((char) c);
            cellW[c] = adv[c] + PAD * 2 + 2;

            if (x + cellW[c] > ATLAS_W) { x = 0; y += cellH; }

            u[c] = x;
            v[c] = y;

            x += cellW[c];
        }

        atlasH = Integer.highestOneBit(Math.max(1, y + cellH - 1)) << 1;

        BufferedImage bimg = new BufferedImage(ATLAS_W, atlasH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bimg.createGraphics();
        hints(g);

        g.setFont(font);
        g.setColor(Color.WHITE);

        for (int c = FIRST; c <= LAST; c++) {
            g.drawString(String.valueOf((char) c), u[c] + PAD, v[c] + PAD + ascent);
        }
        g.dispose();

        DynamicTexture tex = new DynamicTexture(() -> "orcrist_font" + size, ATLAS_W, atlasH, false);
        NativeImage pixel = tex.getPixels();

        for (int py = 0; py < atlasH; py++) {
            for (int px = 0; px < ATLAS_W; px++) {
                pixel.setPixel(px, py, bimg.getRGB(px, py));
            }
        }

        tex.upload();
        mc.getTextureManager().register(texId, tex);
    }

    private static void hints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    private static synchronized Font base() throws Exception {
        if (baseFont == null) {
            try (InputStream in = GlyphFont.class.getResourceAsStream(FONT_PATH)) {
                if (in == null) throw new IllegalStateException("Font not found: " + FONT_PATH);
                baseFont = Font.createFont(Font.TRUETYPE_FONT, in);
            }
        }

        return baseFont;
    }
}
