package me.blackout.orcrist.settings;

public abstract class Setting<T> {
    public final String name;
    public final String description;
    protected T value;
    public final T defaultValue;

    public Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }
}
