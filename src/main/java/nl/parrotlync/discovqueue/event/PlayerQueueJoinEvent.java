package nl.parrotlync.discovqueue.event;

import nl.parrotlync.discovqueue.model.Queue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerQueueJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Queue queue;
    private Player player;

    public PlayerQueueJoinEvent(Queue queue, Player player) {
        this.queue = queue;
        this.player = player;
    }

    public Queue getQueue() { return queue; }

    public Player getPlayer() { return player; }

    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }
}
