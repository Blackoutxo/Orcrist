package me.blackout.orcrist.utils.input;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.Orcrist;
import me.blackout.orcrist.gui.GUI;
import me.blackout.orcrist.utils.Utils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;

import static me.blackout.orcrist.Orcrist.mc;

public class Input {
    private static KeyMapping openGUI;

    public static void registerKey() {
        KeyMapping.Category CATEGORY = KeyMapping.Category.register(
                Orcrist.id("orcrist")
        );

        KeyMapping openGUIKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.orcrist.open-gui",
                        InputConstants.Type.KEYBOARD,
                        InputConstants.KEY_PERIOD,
                        CATEGORY
                ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGUIKey.consumeClick()) {
                Utils.openScreen(new GUI(null));
            }
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!canOpen(screen)) return;

            ScreenKeyboardEvents.afterKeyPress(screen).register((s, event) -> {
                if (openGUIKey.matches(event)) Utils.openScreen(new GUI(s));
            });
        });
    }

    private static boolean canOpen(Screen screen) {
        return screen instanceof TitleScreen
                || screen instanceof PauseScreen
                || screen instanceof JoinMultiplayerScreen
                || screen instanceof SelectWorldScreen;
    }
}
