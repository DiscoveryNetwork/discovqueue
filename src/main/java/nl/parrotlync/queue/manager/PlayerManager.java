package nl.parrotlync.queue.manager;

import nl.parrotlync.queue.DiscovQueue;
import nl.parrotlync.queue.model.RideQueue;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerManager {
    private HashMap<Player, RideQueue> players = new HashMap<>();
    private HashMap<Player, Integer> seconds = new HashMap<>();

    public RideQueue getQueue(Player player) {
        return players.get(player);
    }

    public void addPlayer(Player player, RideQueue queue, Integer seconds) {
        players.put(player, queue);
        this.seconds.put(player, seconds);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        seconds.remove(player);
    }

    public Integer getSeconds(Player player) {
        return seconds.get(player);
    }

    public void setSeconds(Player player, Integer seconds) {
        this.seconds.put(player, seconds);
    }

    public void updateSeconds(Player player) {
        RideQueue queue = players.get(player);
        int firstPlayerSeconds = seconds.get(queue.getPlayers().get(0));
        int seconds = ((queue.getPlayers().indexOf(player) / queue.getBatchSize()) * queue.getInterval()) + firstPlayerSeconds;
        this.seconds.put(player, seconds);
    }

    public Boolean hasPlayer(Player player) { return players.get(player) != null; }
}
