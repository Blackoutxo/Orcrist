package me.blackout.orcrist.systems.modules.combat;

import me.blackout.orcrist.eventBus.EventHandler;
import me.blackout.orcrist.settings.BoolSetting;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;

public class KillAura extends Module {

    public Setting<Boolean> entities = add(new BoolSetting("Entities", "Attacks any entities.", false));

    public KillAura() {
        super("Kill Aura", "Automatically kills entities nearby!", Category.Combat);
    }

    @EventHandler
    public void onTick() {
        if (entities.get()) {

        }

    }
}
