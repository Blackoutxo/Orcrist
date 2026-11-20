/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.settings;

import net.minecraft.nbt.CompoundTag;

public class BoolSetting extends Setting<Boolean> {
    private BoolSetting(Builder builder) {
        super(builder.name, builder.description, builder.defaultValue);
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

    public static class Builder {
        private String name;
        private String description;
        private boolean defaultValue;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder defaultValue(boolean defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public BoolSetting build() {
            if (name.isBlank())
                throw new IllegalStateException("Setting name cannot be blank");

            return new BoolSetting(this);
        }
    }
}
