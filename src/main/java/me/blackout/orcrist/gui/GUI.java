package me.blackout.orcrist.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class GUI extends Screen {
    private Panel panel;

    public GUI() {
        super(Component.literal("Orcrist"));
    }

    @Override
    protected void init() {
        if (panel != null) return;
        panel = new Panel(this, width / 2 - 200, height / 2 - 150);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        panel.render(graphics, mouseX, mouseY);
    }

    @Override
    public void mouseMoved(double x, double y) {

        super.mouseMoved(x, y);
    }
}
