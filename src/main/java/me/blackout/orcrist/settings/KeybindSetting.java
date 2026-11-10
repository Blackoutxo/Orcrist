package me.blackout.orcrist.settings;

import net.minecraft.nbt.CompoundTag;

public class KeybindSetting extends Setting<Integer> {
    public static final int NONE = -1;

    public KeybindSetting() {
        super("Key Bind", "", NONE);
    }

    public boolean isBound() {
        return value != NONE;
    }

    @Override
    public void save(CompoundTag tag) {
        tag.putInt(name, value);
    }

    @Override
    public void load(CompoundTag tag) {
        tag.getIntOr(name, value);
    }
}
