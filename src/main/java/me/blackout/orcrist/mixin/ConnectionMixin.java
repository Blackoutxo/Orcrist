/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.mixin;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import me.blackout.orcrist.Orcrist;
import me.blackout.orcrist.events.PacketEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void onReceive(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
        PacketEvent.Receive event = Orcrist.EVENT_BUS.post(new PacketEvent.Receive(packet, (Connection) (Object) this));
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V", at = @At("HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        PacketEvent.Send event = Orcrist.EVENT_BUS.post(new PacketEvent.Send(packet, (Connection) (Object) this));
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V", at = @At("RETURN"))
    private void onSent(Packet<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        Orcrist.EVENT_BUS.post(new PacketEvent.Sent(packet, (Connection) (Object) this));
    }
}
