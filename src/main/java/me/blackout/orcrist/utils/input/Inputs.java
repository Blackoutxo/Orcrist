package me.blackout.orcrist.utils.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.KeyEvent;

import java.awt.event.InputEvent;

public class Inputs {
    private Inputs() {}

    public static String get(KeyEvent event) {
        return clean(InputConstants.getKey(event).getDisplayName().getString(), event.key());
    }

    public static String get(int key) {
        if (key <= 0) return "none";
        return clean(InputConstants.Type.KEYBOARD.getOrCreate(key).getDisplayName().getString(), key);
    }

    private static String clean(String name, int key) {
        return (name.isEmpty() || name.startsWith("key.")) ? "key " + key : name;
    }
}
