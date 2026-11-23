/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.utils.player;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.awt.*;

import static me.blackout.orcrist.Orcrist.mc;

public class ChatUtils {
    private static Component PREFIX;

    private static Component getPrefix() {
        return PREFIX = Component.empty().setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
                .append("[")
                .append(Component.literal("Orcrist").setStyle(Style.EMPTY.withColor(new Color(56, 105, 206).getRGB())))
                .append("]");
    }

    public static void sendMsg(String message) {
        sendMsg(Component.literal(message));
    }

    public static void sendMsg(Component msg) {
        if (mc.level == null || mc.player == null) return;

        MutableComponent message = Component.empty();
        message.append(getPrefix());
        message.append(Component.literal(" "));
        message.append(msg);

        mc.player.sendSystemMessage(message);
    }
}
