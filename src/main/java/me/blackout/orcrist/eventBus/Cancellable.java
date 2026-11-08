package me.blackout.orcrist.eventBus;

public class Cancellable {
    private boolean cancelled;

    public void cancel() { cancelled = true; }            // Cancel the event call
    public boolean isCancelled() { return cancelled; }    // Check for cancellation
    public void reset() { cancelled = false; }            // Reset
}
