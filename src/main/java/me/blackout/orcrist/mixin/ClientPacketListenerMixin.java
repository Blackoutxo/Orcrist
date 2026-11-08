package me.blackout.orcrist.mixin;

import me.blackout.orcrist.Orcrist;
import me.blackout.orcrist.events.SendMessageEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Inject(at = @At("HEAD"), method = "sendChat", cancellable = true)
	private void onSendChat(String message, CallbackInfo ci) {
		SendMessageEvent event = Orcrist.EVENT_BUS.post(SendMessageEvent.get(message));
		if (event.isCancelled()) ci.cancel();
	}
}