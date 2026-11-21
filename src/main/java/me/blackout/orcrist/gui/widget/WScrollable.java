/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.gui.widget;

public abstract class WScrollable extends Widget {
    protected int scroll;

    protected void clampScroll(int cHeight) {
        scroll = Math.max(0, Math.min(scroll, Math.max(0, cHeight - h)));
    }

    @Override
    public boolean mouseScrolled(double x, double y, double amount) {
        if (!contains(x, y)) return false;
        scroll += (int) (-Math.signum(amount) * 20);
        return true;
    }
}
