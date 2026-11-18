package me.blackout.orcrist.utils;

import me.blackout.orcrist.utils.input.Input;

public class Utils {
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
}
