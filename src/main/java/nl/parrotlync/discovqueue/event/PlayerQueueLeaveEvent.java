package nl.parrotlync.discovqueue.event;

import nl.parrotlync.discovqueue.model.Queue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerQueueLeaveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Queue queue;

    public PlayerQueueLeaveEvent(Player player, Queue queue) {
        this.player = player;
        this.queue = queue;
    }

    public Player getPlayer() { return player; }

    public Queue getQueue() { return queue; }

    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }
}
