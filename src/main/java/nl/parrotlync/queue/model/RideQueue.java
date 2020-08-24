package nl.parrotlync.queue.model;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.queue.event.PlayerQueueLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RideQueue {
    private String name;
    private Location location;
    private Sign sign;
    private Integer batchSize;
    private Integer interval;
    private Integer secondsLeft;
    private Boolean opened;
    private Boolean locked;
    private Boolean paused;
    private List<Player> players = new ArrayList<>();

    public RideQueue(String name) {
        this.name = name;
        this.opened = false;
        this.locked = false;
        this.paused = false;
    }

    public void addPlayer(Player player) {
        if (!players.contains(player) && !locked && opened) {
            players.add(player);
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
        if (players.size() == 0) {
            secondsLeft = interval;
        }
    }

    public void getBatch() {
        if (opened && !paused && location != null && batchSize != null) {
            String msg = "§7 >> §2Your wait is over! &7<<";
            List<Player> batch = new ArrayList<>();
            if (players.size() < batchSize) {
                batch.addAll(players.subList(0, players.size()));
            } else {
                batch.addAll(players.subList(0, batchSize));
            }

            for (Player player : batch) {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueLeaveEvent(this, player));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
                player.teleport(location);
            }
            this.secondsLeft = interval;
        }
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public void updateSign() {
        sign.setLine(0, "[§2Queue§0]");
        sign.setLine(1, "§l" + name);
        if (opened) {
            sign.setLine(2, "§o" + players.size() + " waiting...");
            if (locked) {
                sign.setLine(3, "§c§oLocked");
            } else {
                sign.setLine(3, "§1§oClick to join!");
            }
        } else {
            sign.setLine(2, "");
            sign.setLine(3, "§4Closed");
        }
        sign.update();
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getBatchSize() {
        return batchSize;
    }


    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
        this.secondsLeft = interval;
    }

    public Integer getSecondsLeft() {
        return secondsLeft;
    }

    public void setSecondsLeft(Integer secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public List<Player> getPlayers() { return players; }

    public boolean isOpened() { return opened; }

    public boolean isLocked() { return locked; }

    public boolean isPaused() { return paused; }

    public void toggleOpened() {
        opened = !opened;
        updateSign();
        if (!opened) {
            players.clear();
        }
    }

    public void toggleLocked() {
        locked = !locked;
        updateSign();
    }

    public void togglePaused() {
        paused = !paused;
        updateSign();
    }
}
