package me.blackout.orcrist.gui.row.settings;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.gui.utils.GuiValues;
import me.blackout.orcrist.settings.KeybindSetting;
import net.minecraft.client.input.KeyEvent;

import java.awt.*;

public class KeybindRow extends LabelRow {
    private final KeybindSetting keyBind;
    private boolean listening;

    //private final GuiRenderer renderer = new GuiRenderer();

    public KeybindRow(KeybindSetting setting) {
        super(setting);
        this.keyBind = setting;
    }

    @Override
    protected String valueText() {
        return listening ? "...." : keyBind.label();
    }

    @Override
    protected Color color() {
        return listening ? GuiValues.TEXT_DIM : GuiValues.TEXT;
    }

    @Override
    protected boolean onLeft(double mouseX, int x, int w) {
        listening = !listening;
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!listening) return false;

        int key = event.key();
        if (key == InputConstants.KEY_ESCAPE) {}
        else if (key == InputConstants.KEY_DELETE) keyBind.unbind();
        else keyBind.bind(event);

        listening = false;
        return true;
    }
}
