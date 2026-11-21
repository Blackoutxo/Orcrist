/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.gui.utils;

import me.blackout.orcrist.utils.Utils;

public class NumberEditor {

    private final StringBuilder sb = new StringBuilder();
    private boolean active, fresh;

    public boolean isActive() {
        return active;
    }

    public String text() {
        return sb.toString();
    }

    public void begin(String current) {
        sb.setLength(0);
        sb.append(current);
        fresh = true;
        active = true;
        Utils.set(true);
    }

    public void type(char c) {
        boolean allowed = (c >= '0' && c <= '9') || c == '.' || c == '-';
        if (!allowed || !active) return;

        if (fresh) { sb.setLength(0);  fresh = false; }

        if (c == '-' && sb.length() > 0) return;
        if (c == '.' && sb.indexOf(".") >= 0) return;

        if (sb.length() < 9) sb.append(c);
    }

    public void backSpace() {
        if (fresh) { sb.setLength(0); fresh = false; }
        if (!sb.isEmpty()) sb.setLength(sb.length() - 1);
    }

    // Finish entering the value
    public double commit() {
        String s = sb.toString();
        end();

        return Double.parseDouble(s);
    }

    public void cancel() {
        end();
    }

    private void end() {
        active = false;
        Utils.set(false);
    }
}
