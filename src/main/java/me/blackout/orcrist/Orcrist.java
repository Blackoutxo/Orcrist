package me.blackout.orcrist;

import me.blackout.orcrist.eventBus.EventBus;
import me.blackout.orcrist.utils.Utils;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Orcrist implements ModInitializer {
	public static final String MOD_ID = "orcrist";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);
	public static EventBus EVENT_BUS = new EventBus();

	public static Minecraft mc;

	@Override
	public void onInitialize() {
		LOG.info("Initializing PvP Assist");

		// Init global client accessor
		mc = Minecraft.getInstance();

		// Subscribe
		EVENT_BUS.subscribe(this);

		// Register necessity
		Utils.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
