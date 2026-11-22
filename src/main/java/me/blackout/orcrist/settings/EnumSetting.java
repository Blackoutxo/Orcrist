/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.settings;

import net.minecraft.nbt.CompoundTag;

public class EnumSetting<E extends Enum<E>> extends Setting<E> {
    private final E[] values;

    public EnumSetting(Builder<E> builder) {
        super(builder.name, builder.description, builder.defaultValue);
        this.values = builder.defaultValue.getDeclaringClass().getEnumConstants();
    }

    public void cycle() {
        value = values[(value.ordinal() + 1) % values.length];
    }

    public void prev() {
        value = values[(value.ordinal() - 1) % values.length];
    }

    public E[] getValues() {
        return values;
    }

    @Override
    public void save(CompoundTag tag) {
        tag.putString(name, value.name());
    }

    @Override
    public void load(CompoundTag tag) {
        String saved = tag.getStringOr(name, value.name());

        for (E e : values) {
            if (e.name().equals(saved)) {
                value = e;
                return;
            }
        }
    }

    public static class Builder<E extends Enum<E>> {
        private String name;
        private String description;
        private E defaultValue;

        public Builder<E> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<E> description(String description) {
            this.description = description;
            return this;
        }

        public Builder<E> defaultValue(E defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public EnumSetting<E> build() {
            if (name.isBlank())
                throw new IllegalStateException("Settings name cannot be empty");
            else if (defaultValue == null)
                throw new IllegalStateException("Default value cannot be empty");

            return new EnumSetting<>(this);
        }
    }
}
