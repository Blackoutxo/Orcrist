package me.blackout.orcrist.gui;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.systems.Config;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
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
        panel.mouseScrolled(scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    // Helper
    public void setTextInput(boolean on) {
        minecraft.onTextInputFocusChange(this, on);
    }

    @Override
    public boolean charTyped(@NotNull CharacterEvent event) {
        if (panel.charTyped((char) event.codepoint())) return true;
        return super.charTyped(event);
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        if (panel.keyPressed(event.key())) return true;
        if (event.key() == InputConstants.KEY_BACKSPACE && panel.backSpace()) return true;
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        setTextInput(false);
        Config.save();
        super.removed();
    }
}
