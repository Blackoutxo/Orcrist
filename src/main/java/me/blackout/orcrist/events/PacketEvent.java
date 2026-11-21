/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.events;

import me.blackout.orcrist.eventBus.Cancellable;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;

public class PacketEvent {
    public static class Receive extends Cancellable {
        public Connection connection;
        public Packet<?> packet;

        public Receive(Packet<?> receive, Connection connection) {
            this.setCancelled(false);
            this.packet = receive;
            this.connection = connection;
        }
    }

    public static class Send extends Cancellable {
        public Connection connection;
        public Packet<?> packet;

        public Send(Packet<?> send, Connection connection) {
            this.setCancelled(false);
            this.packet = send;
            this.connection = connection;
        }
    }

    public static class Sent {
        public Connection connection;
        public Packet<?> packet;

        public Sent(Packet<?> sent, Connection connection) {
            this.packet = sent;
            this.connection = connection;
        }
    }
}
