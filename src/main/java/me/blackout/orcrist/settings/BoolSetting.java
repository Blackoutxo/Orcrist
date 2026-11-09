package me.blackout.orcrist.settings;

import net.minecraft.nbt.CompoundTag;

public class BoolSetting extends Setting<Boolean> {
    public BoolSetting(String name, String description, Boolean defaultValue) {
        super(name, description, defaultValue);
    }

    @Override
    public void save(CompoundTag tag) {

    }

    @Override
    public void load(CompoundTag tag) {

    }

    public void toggle() { value = !value; }

    /**TODO: Add save*/
}
