package me.blackout.orcrist.utils.input;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.systems.modules.Modules;

import java.util.HashSet;
import java.util.Set;

import static me.blackout.orcrist.Orcrist.mc;

public class KeyBindManager {
    private static Set<Integer> down = new HashSet<>();

    public void run() {
        boolean canPress = mc.gui.screen() != null && mc.player != null;

        for (Module module : Modules.all()) {
            int key = module.keyBind.get();

            if (key < 0) continue;

            boolean pressed = InputConstants.isKeyDown(key);
            boolean wasPressed = down.contains(key);

            if (pressed && canPress && !wasPressed) {
                // ChatUtils toggle off / on message
                module.toggle();
            }

            if (pressed) down.add(key); else down.remove(key);
        }
    }
}
