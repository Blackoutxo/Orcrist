/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.mixin;

import me.blackout.orcrist.Orcrist;
import me.blackout.orcrist.events.TickEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPreTick(CallbackInfo ci) {
        Orcrist.EVENT_BUS.post(TickEvent.Pre.get());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPostTick(CallbackInfo ci) {
        Orcrist.EVENT_BUS.post(TickEvent.Post.get());
    }
}
