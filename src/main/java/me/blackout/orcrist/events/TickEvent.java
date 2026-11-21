/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.events;

public class TickEvent {

    public static class Pre extends TickEvent {
        private static final Pre INSTANCE = new Pre();

        private Pre() {}

        public static Pre get() {
            return INSTANCE;
        }
    }

    public static class Post extends TickEvent {
        private static final Post INSTANCE = new Post();

        private Post() {}

        public static Post get() {
            return INSTANCE;
        }
    }
}
