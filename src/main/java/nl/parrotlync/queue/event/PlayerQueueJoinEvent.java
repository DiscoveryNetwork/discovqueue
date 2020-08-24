package nl.parrotlync.queue.event;

import nl.parrotlync.queue.model.RideQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerQueueJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private RideQueue queue;
    private Player player;

    public PlayerQueueJoinEvent(RideQueue queue, Player player) {
        this.queue = queue;
        this.player = player;
    }

    public RideQueue getQueue() { return queue; }

    public Player getPlayer() { return player; }

    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }
}
