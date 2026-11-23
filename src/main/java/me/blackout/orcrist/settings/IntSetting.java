/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.settings;

import net.minecraft.nbt.CompoundTag;

public class IntSetting extends Setting<Integer> {
    public final int minValue, maxValue;

    public IntSetting(Builder builder) {
        super(builder.name, builder.description, builder.defaultValue);
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
    }

    @Override
    public void save(CompoundTag tag) {
        tag.putInt(name, value);
    }

    @Override
    public void load(CompoundTag tag) {
        value = tag.getIntOr(name, value);
    }

    public double progress() {
        return (double) (value - minValue) / (maxValue - minValue);
    }

    public static class Builder {
        private String name;
        private String description;
        private int defaultValue;
        private int minValue, maxValue;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder defaultValue(int defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder minValue(int minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder maxValue(int maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public IntSetting build() {
            if (name.isBlank()) throw new IllegalStateException("Settings name cannot be left blank.");
            return new IntSetting(this);
        }
    }
}
