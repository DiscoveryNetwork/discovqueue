package nl.parrotlync.discovqueue.manager;

import nl.parrotlync.discovqueue.model.Queue;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    private final HashMap<UUID, Queue> players = new HashMap<>();

    public void addPlayer(Player player, Queue queue) {
        players.put(player.getUniqueId(), queue);
    }

    public boolean hasPlayer(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public Queue getQueue(Player player) {
        return players.get(player.getUniqueId());
    }
}
