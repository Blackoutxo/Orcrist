/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.systems.modules;

import me.blackout.orcrist.Orcrist;
import me.blackout.orcrist.settings.KeybindSetting;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.utils.player.ChatUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name;
    public final String description;
    public final Category category;
    public final Minecraft mc;

    private boolean enabled;

    private final List<Setting> settings = new ArrayList<>();
    public final KeybindSetting keyBind = add(new KeybindSetting());

    public Module(Category category, String name, String description) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.mc = Minecraft.getInstance();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        enabled = !enabled;

        if (enabled) {
            Orcrist.EVENT_BUS.subscribe(this);
            sendToggledMsg();
            onActivate();
        } else {
            Orcrist.EVENT_BUS.unsubscribe(this);
            sendToggledMsg();
            onDeactivate();
        }
    }

    protected void onActivate() {}
    protected void onDeactivate() {}

    protected <S extends Setting<?>> S add(S setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void setEnabled(boolean state) {
        if (state != enabled) toggle();
    }

    private void sendToggledMsg() {
        ChatUtils.sendMsg(name + " toggled " + (enabled ? ChatFormatting.GREEN + "On" : ChatFormatting.RED + "Off"));
    }

    public void info(String message) {
        ChatUtils.sendMsg(appendModule(message));
    }

    public void warn(String message) {
        ChatUtils.sendMsg(appendModule(ChatFormatting.YELLOW + message));
    }

    public void error(String message) {
        ChatUtils.sendMsg(appendModule(ChatFormatting.RED + message));
    }

    private Component appendModule(String message) {
        return Component.literal("[")
               .append(Component.literal(name).setStyle(Style.EMPTY.withColor(new Color(160, 85, 238).getRGB())))
               .append(Component.literal("] ").append(message));
    }
}
