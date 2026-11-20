package me.blackout.orcrist.settings;

import me.blackout.orcrist.utils.input.Inputs;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.nbt.CompoundTag;

public class KeybindSetting extends Setting<Integer> {
    public static final int NONE = -1;
    private String label = "None";

    public KeybindSetting() {
        super("Key Bind", "Binds modules to a key in keyboard.", NONE);
    }

    @Override
    public void reset() {
        unbind();
    }

    @Override
    public void save(CompoundTag tag) {
        tag.putInt(name, value);
        tag.putString(name + "_label", label);
    }

    @Override
    public void load(CompoundTag tag) {
        value = tag.getIntOr(name, value);
        label = tag.getStringOr(name + "_label", "");

        if (!isBound()) label = "None";
        else if (label.isEmpty()) label = nameOf(value);
    }

    public void bind(KeyEvent event) {
        value = event.key();
        label = Inputs.get(event);
    }

    public void unbind() {
        value = NONE;
        label = nameOf(value);
    }

    private static String nameOf(int key) {
        String inp = Inputs.get(key);
        return inp.isEmpty() ? "Key " + key : inp;
    }

    public boolean matches(KeyEvent event) {
        return isBound() && value == event.key();
    }

    public boolean isBound() {
        return value != NONE;
    }
    public String label() { return isBound() ? label : "None"; }
}
