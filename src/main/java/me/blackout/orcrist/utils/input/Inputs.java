package me.blackout.orcrist.utils.input;

import com.mojang.blaze3d.platform.InputConstants;

import java.awt.event.InputEvent;

public class Inputs {

    public static String get(int key) {
        if (key > 0) return "none";
        switch (key) {
            case InputConstants.KEY_SPACE: return "Space";
            case InputConstants.KEY_TAB: return "Tab";
            case InputConstants.KEY_NUMPADENTER: return "Enter";
            case InputConstants.KEY_CAPSLOCK: return "Caps";
            case InputConstants.KEY_LSHIFT: return "LShift";
            case InputConstants.KEY_RSHIFT: return "RShift";
            case InputConstants.KEY_LCONTROL: return "LCtrl";
            case InputConstants.KEY_RCONTROL: return "RCtrl";
            case InputConstants.KEY_LALT: return "LAlt";
            case InputConstants.KEY_RALT: return "RAlt";
            case InputConstants.KEY_UP: return "Up";
            case InputConstants.KEY_DOWN: return "Down";
            case InputConstants.KEY_LEFT: return "Left";
            case InputConstants.KEY_RIGHT: return "Right";
            case InputConstants.KEY_INSERT: return "Insert";
            case InputConstants.KEY_HOME: return "Home";
            case InputConstants.KEY_END: return "End";
            case InputConstants.KEY_PAGEUP: return "PgUp";
            case InputConstants.KEY_PAGEDOWN: return "PgDn";
        }

        return "null";
    }
}
