package me.blackout.orcrist.gui.widget;

import me.blackout.orcrist.utils.Utils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;

public abstract class Widget {
    protected int x, y, w, h;

    public void setBound(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public boolean contains(double x, double y) {
        return Utils.hovered(x, y, this.x, this.y, w, h);
    }

    public abstract void render(GuiGraphicsExtractor graphics, int x, int y);

    public boolean mouseClicked(double x, double y, int button) { return false; }
    public void mouseReleased() {}
    public boolean mouseScrolled(double x, double y, double amount) { return false; }
    public boolean keyPressed(KeyEvent event) { return false; }
    public boolean charTyped(CharacterEvent event) { return false; }
}
