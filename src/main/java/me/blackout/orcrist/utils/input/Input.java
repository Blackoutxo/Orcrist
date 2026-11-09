package me.blackout.orcrist.utils.input;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.Orcrist;
import me.blackout.orcrist.gui.GUI;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

import static me.blackout.orcrist.Orcrist.mc;

public class Input {
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
                mc.gui.setScreen(new GUI());
            }
        });
    }
}
