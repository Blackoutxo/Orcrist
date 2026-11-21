/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.utils;

import me.blackout.orcrist.utils.input.Input;
import net.minecraft.client.gui.screens.Screen;

import java.util.Locale;
import java.util.function.Consumer;

import static me.blackout.orcrist.Orcrist.mc;

public class Utils {
    private static Consumer<Boolean> hook = on -> {};

    private static long nano = System.nanoTime();
    private static double dt = 0.016;

    public static void register() {
        Input.registerKey();
    }

    public static boolean hovered(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    public static void beginFrame() {
        long now = System.nanoTime();
        dt = Math.min((now - nano) / 1_000_000_000.0, 0.1);
        nano = now;
    }

    public static double animate(double value, double target, double progress) {
        double diff = target - value;
        if (Math.abs(diff) < 0.001) return target;
        return value + diff * (1.0 - Math.exp(-progress * dt));
    }

    // Number formatting
    public static String format(double value, int decimals) {
        String s = String.format(Locale.ROOT, "%." + decimals + "f", value);
        if (s.indexOf('.') < 0) return s;

        int end = s.length();

        while (s.charAt(end - 1) == '0') end--;
        if (s.charAt(end - 1) == '.') end--;
        s = s.substring(0, end);
        return s.equals("-0") ? "0" : s;
    }

    public static double round(double value, int decimals) {
        double scale = Math.pow(10, decimals);
        return Math.round(value * scale) / scale;
    }

    // Text input focus helpers
    public static void setHook(Consumer<Boolean> h) {
        hook = h;
    }

    public static void set(boolean on) {
        hook.accept(on);
    }

    public static void openScreen(Screen screen) {
        if (mc != null) mc.gui.setScreen(screen);
    }
}
