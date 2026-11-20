/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.events;

import me.blackout.orcrist.eventBus.Cancellable;
import net.minecraft.client.input.KeyEvent;

public class KeyInputEvent extends Cancellable {
    private static final KeyInputEvent INSTANCE = new KeyInputEvent();

    private KeyEvent keyEvent;
    private int action;

    public static KeyInputEvent get(KeyEvent keyEvent, int action) {
        INSTANCE.reset();
        INSTANCE.keyEvent = keyEvent;
        INSTANCE.action = action;
        return INSTANCE;
    }

    public KeyEvent getKeyEvent() {
        return keyEvent;
    }
    public int getKey() {
        return keyEvent.key();
    }
    public int getAction() {
        return action;
    }
}
