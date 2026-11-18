package me.blackout.orcrist.gui;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.gui.theme.OrcristGuiTheme;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.systems.Config;
import me.blackout.orcrist.utils.Utils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class GUI extends Screen {
    private OrcristGuiTheme panel;

    public GUI() {
        super(Component.literal("Orcrist"));
    }

    @Override
    protected void init() {
        if (panel != null) return;
        panel = new OrcristGuiTheme(width / 2 - GuiValues.UI_W / 2, height / 2 - GuiValues.UI_H / 2);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        Utils.beginFrame();
        panel.render(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
        if (panel.mouseClicked(event.x(), event.y(), event.button())) return true;
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent event) {
        panel.mouseReleased();
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        panel.mouseScrolled(mouseX, mouseY, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        Config.save();
        super.removed();
    }
}
