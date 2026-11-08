package me.blackout.orcrist.systems;

import java.util.ArrayList;
import java.util.List;

public class Modules {
    private final static List<Module> modules = new ArrayList<>();

    // Add modules function
    private static void add(Module module) {
        modules.add(module);
    }

    // Init all modules
    private void init() {
        all();
    }

    // All
    private void all() {}

    // Render
    private void render() {}

    // World
    private void world() {}
}
