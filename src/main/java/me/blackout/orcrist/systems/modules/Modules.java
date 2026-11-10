package me.blackout.orcrist.systems.modules;

import me.blackout.orcrist.systems.modules.combat.KillAura;

import java.util.ArrayList;
import java.util.List;

public class  Modules {
    private final static List<Module> modules = new ArrayList<>();

    // Add modules function
    private static void add(Module module) {
        modules.add(module);
    }

    // Init all modules
    public static void init() {
        combat();
    }

    public static List<Module> all() { return modules; }

    // Combat
    private static void combat() {
        add(new KillAura());
    }

    // Render
    private static void render() {}

    // World
    private static void world() {}

    // Search find
    public static List<Module> get(Category category, String search) {
        String s = search.toLowerCase();
        List<Module> mod = new ArrayList<>();
        for (Module m : modules) {
            if (category != Category.All && m.category != category) continue;
            if (!s.isEmpty() && !m.name.toLowerCase().contains(s)) continue;
            mod.add(m);
        }
        return mod;
    }
}
