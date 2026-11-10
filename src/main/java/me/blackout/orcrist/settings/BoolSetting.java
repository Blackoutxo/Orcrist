package me.blackout.orcrist.settings;

import net.minecraft.nbt.CompoundTag;

public class BoolSetting extends Setting<Boolean> {
    public BoolSetting(String name, String description, Boolean defaultValue) {
        super(name, description, defaultValue);
    }

    @Override
    public void save(CompoundTag tag) {
        tag.putBoolean(name, value);
    }

    @Override
    public void load(CompoundTag tag) {
        value = tag.getBooleanOr(name, value);
    }

    public void toggle() { value = !value; }
}
