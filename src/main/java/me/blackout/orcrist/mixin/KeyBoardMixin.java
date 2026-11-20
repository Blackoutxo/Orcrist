package me.blackout.orcrist.mixin;

import me.blackout.orcrist.Orcrist;
import me.blackout.orcrist.events.KeyInputEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyBoardMixin {
    @Inject(at = @At("HEAD"), method = "keyPress")
    public void keyPress(final long handle, final @KeyEvent.Action int action, final KeyEvent event, CallbackInfo ci) {
        KeyInputEvent keyEvent = Orcrist.EVENT_BUS.post(KeyInputEvent.get(event, action));
        if (keyEvent.isCancelled()) ci.cancel();
    }
}
