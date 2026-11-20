/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.settings;

import net.minecraft.nbt.CompoundTag;

public class DoubleSetting extends Setting<Double> {
    public final double minValue, maxValue;

    private DoubleSetting(Builder builder) {
        super(builder.name, builder.description, builder.defaultValue);
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
    }

    @Override
    public void save(CompoundTag tag) {
        tag.putDouble(name, value);
    }

    @Override
    public void load(CompoundTag tag) {
        value = tag.getDoubleOr(name, value);
    }

    public double progress() { return (value - minValue) / (maxValue - minValue); }

    public static class Builder {
        private String name;
        private String description;
        private double defaultValue, minValue, maxValue;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder defaultValue(double defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder minValue(double minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder maxValue(double maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public DoubleSetting build() {
            if (name.isBlank())
                throw new IllegalStateException("Setting name cannot be blank");

            return new DoubleSetting(this);
        }
    }
}
