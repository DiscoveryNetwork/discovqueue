package nl.parrotlync.queue.manager;

import nl.parrotlync.queue.model.RideQueue;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerManager {
    private HashMap<Player, RideQueue> players = new HashMap<>();

    public RideQueue getQueue(Player player) {
        return players.get(player);
    }

    public void addPlayer(Player player, RideQueue queue) {
        players.put(player, queue);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public Boolean hasPlayer(Player player) { return players.get(player) != null; }
}
