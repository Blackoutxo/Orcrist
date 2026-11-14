package me.blackout.orcrist.systems;

import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.systems.modules.Modules;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Path DIR = FabricLoader.getInstance().getGameDir().resolve("orcrist");
    private static final Path File = DIR.resolve("orcrist.dat");

    // Save
    public static void save() {
        CompoundTag root = new CompoundTag();

        // Each module and settings
        for (Module m : Modules.all()) {
            CompoundTag mod = new CompoundTag();
            mod.putBoolean("enabled", m.isEnabled());

            CompoundTag settings = new CompoundTag();
            for (Setting<?> s : m.getSettings()) s.save(settings);
            mod.put("Settings", settings);

            root.put(m.name, mod); // Contains all states and values
        }

        try {
            if (!Files.exists(DIR)) Files.createDirectories(DIR); // Create dir if no dir
            NbtIo.writeCompressed(root, File); // Write nbt
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Load
    public static void load() {
        if (!Files.exists(File)) return;

        CompoundTag root;
        try {
            root = NbtIo.readCompressed(File, NbtAccounter.unlimitedHeap());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (Module m : Modules.all()) {
            if (!root.contains(m.name)) continue;
            CompoundTag mod = root.getCompoundOrEmpty(m.name);
            CompoundTag settings = root.getCompoundOrEmpty("Settings");

            for (Setting<?> s : m.getSettings()) {
                s.load(settings);
            }

            m.setEnabled(mod.getBooleanOr("enabled", false));
        }
    }
}
