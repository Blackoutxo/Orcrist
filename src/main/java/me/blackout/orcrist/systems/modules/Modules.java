package me.blackout.orcrist.systems.modules;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.eventBus.EventHandler;
import me.blackout.orcrist.events.KeyInputEvent;
import me.blackout.orcrist.systems.modules.combat.KillAura;

import java.util.ArrayList;
import java.util.List;

import static me.blackout.orcrist.Orcrist.EVENT_BUS;
import static me.blackout.orcrist.Orcrist.mc;

public class  Modules {
    private final static List<Module> modules = new ArrayList<>();

    // Add modules function
    private static void add(Module module) {
        modules.add(module);
    }

    // Init all modules
    public static void init() {
        EVENT_BUS.subscribe(new Modules());

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

    // Module key event
    @EventHandler(priority = 9999)
    public void onKey(KeyInputEvent event) {
        if (event.getAction() != InputConstants.PRESS) return;
        if (mc.player == null) return;

        for (Module module : modules) {
            if (module.keyBind.matches(event.getKeyEvent())) module.toggle();
        }
    }

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
