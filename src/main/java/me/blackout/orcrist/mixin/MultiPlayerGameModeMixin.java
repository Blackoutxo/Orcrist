/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.mixin;

import me.blackout.orcrist.mixinInterface.IMultiPlayerGameMode;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin implements IMultiPlayerGameMode {

    @Shadow
    protected void ensureHasSentCarriedItem() {}

    @Override
    public void orcrist$syncSelected() {
        ensureHasSentCarriedItem();
    }
}
