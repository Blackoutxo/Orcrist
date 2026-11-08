package me.blackout.orcrist.systems;

import me.blackout.orcrist.Orcrist;

public abstract class Module {
    public final String name;
    public final String description;
    public final Category category;

    private boolean enabled;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public boolean isEnabled() { return enabled; }

    public void toggle() {
        enabled = !enabled;

        if (enabled) {
            Orcrist.EVENT_BUS.subscribe(this);
            onEnable();
        } else {
            Orcrist.EVENT_BUS.unsubscribe(this);
            onDisable();
        }
    }

    protected void onEnable() {}
    protected void onDisable() {}
}
