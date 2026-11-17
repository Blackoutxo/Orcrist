package me.blackout.orcrist.utils.render.text;

import java.util.HashMap;
import java.util.Map;

public class Fonts {
    private static final Map<Integer, GlyphFont> cache = new HashMap<>();

    private Fonts() {}

    public static GlyphFont get(int size) {
        return cache.computeIfAbsent(size, GlyphFont::new);
    }
}
