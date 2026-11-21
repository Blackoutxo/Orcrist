/*
 * This file is a part of Orcrist distribution (https://github.com/Blackoutxo/Orcrist)
 * Copyright (c) Blackoutxo
 */

package me.blackout.orcrist.eventBus;

public class Cancellable {
    private boolean cancelled;

    public void cancel() { cancelled = true; }              // Cancel the event call
    public boolean isCancelled() { return cancelled; }      // Check for cancellation
    public void setCancelled(boolean b) { cancelled = b; }
    public void reset() { cancelled = false; }              // Reset
}
