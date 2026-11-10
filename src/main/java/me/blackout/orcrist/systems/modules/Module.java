package me.blackout.orcrist.systems.modules;

import me.blackout.orcrist.Orcrist;
import me.blackout.orcrist.settings.KeybindSetting;
import me.blackout.orcrist.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name;
    public final String description;
    public final Category category;

    private boolean enabled;

    private final List<Setting> settings = new ArrayList<>();
    public final KeybindSetting keyBind = add(new KeybindSetting());

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

    protected <S extends Setting<?>> S add(S setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void setEnabled(boolean state) {
        if (state != enabled) toggle();
    }
}
