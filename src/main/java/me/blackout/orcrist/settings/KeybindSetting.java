package me.blackout.orcrist.settings;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.utils.input.Inputs;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.nbt.CompoundTag;

public class KeybindSetting extends Setting<Integer> {
    public static final int NONE = -1;
    private String label = "None";

    public KeybindSetting() {
        super("Key Bind", "Binds modules to a key in keyboard.", NONE);
    }

    public boolean isBound() {
        return value != NONE;
    }
    public String label() { return isBound() ? label : "None"; }

    public void bind(KeyEvent event) {
        value = event.key();
        label = Inputs.get(event.key());
    }

    public void unbind() {
        value = NONE;
        label = "None";
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
    }
}
