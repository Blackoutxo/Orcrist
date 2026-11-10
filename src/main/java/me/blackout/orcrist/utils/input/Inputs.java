package me.blackout.orcrist.utils.input;

import com.mojang.blaze3d.platform.InputConstants;

import java.awt.event.InputEvent;

public class Inputs {
    private Inputs() {}

    public static String get(int key) {
        if (key > 0) return "none";
        return InputConstants.Type.KEYBOARD.getOrCreate(key).getDisplayName().getString();
    }
}
