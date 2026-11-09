package me.blackout.orcrist.settings;

import net.minecraft.nbt.CompoundTag;

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

    public T get() { return value; }
    public void set(T value) { this.value = value; }
    public void reset() { value = defaultValue; }

    public abstract void save(CompoundTag tag);
    public abstract void load(CompoundTag tag);
}
