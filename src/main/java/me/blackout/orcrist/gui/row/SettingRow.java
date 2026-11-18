package me.blackout.orcrist.gui.row;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.settings.Setting;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.event.KeyEvent;

public abstract class SettingRow {
    public static final int HEIGHT = 20;

    protected final Setting<?> setting;

    protected SettingRow(Setting<?> setting) {
        this.setting = setting;
    }

    // Render
    public abstract void render(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY);

    // Mouse click
    public final boolean click(double mouseX, int button, int x, int w) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT) onLeft(mouseX, x, w);
        if (button == InputConstants.MOUSE_BUTTON_RIGHT) onRight();
        return false;
    }

    protected boolean onLeft(double mouseX, int x, int w) {
        return false;
    }

    protected boolean onRight() {
        setting.reset();
        return true;
    }

    public void mouseReleased() {}
    public boolean keyPressed(KeyEvent event) { return false; }
    public void onFocusLost() {}
}
