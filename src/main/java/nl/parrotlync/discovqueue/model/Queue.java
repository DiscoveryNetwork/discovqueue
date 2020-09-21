package nl.parrotlync.discovqueue.model;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import nl.parrotlync.discovqueue.DiscovQueue;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class Queue {
    private String name;
    private Location location;
    private Integer batchSize;
    private Integer interval;
    private Boolean opened = false;
    private Boolean locked = false;
    private Boolean paused = false;
    private HashMap<Sign, SignType> signs = new HashMap<>();
    private List<Player> players = new ArrayList<>();

    public void teleportBatch() {}

    public void teleportBatch(Collection<MinecartMember<?>> minecartMembers) {}

    // Players
    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    // Signs
    public void addSign(Sign sign, SignType type) {
        signs.put(sign, type);
    }

    public void removeSign(Sign sign) {
        signs.remove(sign);
    }

    public List<Sign> getSigns() {
        return new ArrayList<>(signs.keySet());
    }

    public SignType getSignType(Sign sign) {
        return signs.get(sign);
    }

    // Toggles
    public boolean isOpened() {
        return opened;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isPaused() {
        return paused;
    }

    public void toggleOpened() {
        opened = !opened;
        updateSigns();
        if (!opened) {
            for (Player player : players) {
                DiscovQueue.getInstance().getPlayerManager().removePlayer(player);
            }
            players.clear();
        }
    }

    public void toggleLocked() {
        locked = !locked;
        updateSigns();
    }

    public void togglePaused() {
        paused = !paused;
        updateSigns();
    }

    // Getters & setters
    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    // Sign updates
    public void updateSigns() {
        for (Sign sign : signs.keySet()) {
            if (signs.get(sign) == SignType.QUEUE) {
                updateQueueSign(sign);
            } else if (signs.get(sign) == SignType.WAIT_TIME) {
                updateWaitSign(sign);
            } else if (signs.get(sign) == SignType.QUEUE_INFO) {
                updateInfoSign(sign);
            }
        }
    }

    private void updateQueueSign(Sign sign) {
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

    private void updateWaitSign(Sign sign) {
        sign.setLine(0, "[§1WaitTime§0]");
        sign.setLine(1, "§l" + name);
        if (opened) {
            sign.setLine(2, "§o" + players.size() + " waiting...");
            if (locked) {
                sign.setLine(3, "§c§oLocked");
            } else {
                int secondsLeft;
                if (players.size() != 0) {
                    secondsLeft = DiscovQueue.getInstance().getPlayerManager().getSeconds(players.get(players.size() - 1));
                } else {
                    secondsLeft = (interval > 15) ? 15 : interval;
                }
                int minutes = secondsLeft / 60;
                int seconds = secondsLeft % 60;
                sign.setLine(3, String.format("%02d:%02d", minutes, seconds));
            }
        } else {
            sign.setLine(2, "");
            sign.setLine(3, "§4Closed");
        }
        sign.update();
    }

    private void updateInfoSign(Sign sign) {
        sign.setLine(0, "[§5QueueInfo§0]");
        sign.setLine(1, "§l" + name);
        if (opened) {
            sign.setLine(2, "§o" + players.size() + " waiting...");
            if (paused) {
                sign.setLine(3, "§6§oPaused");
            } else {
                int secondsLeft;
                if (players.size() != 0) {
                    secondsLeft = DiscovQueue.getInstance().getPlayerManager().getSeconds(players.get(0));
                } else {
                    secondsLeft = 0;
                }
                int minutes = secondsLeft / 60;
                int seconds = secondsLeft % 60;
                sign.setLine(3, String.format("%02d:%02d", minutes, seconds));
            }
        } else {
            sign.setLine(2, "");
            sign.setLine(3, "§4Closed");
        }
        sign.update();
    }


    void updatePlayerSeconds() {
        for (Player player : getPlayers()) {
            if (getPlayers().indexOf(player) == 0) {
                DiscovQueue.getInstance().getPlayerManager().setSeconds(player, getInterval());
            }
            DiscovQueue.getInstance().getPlayerManager().updateSeconds(player);
        }
    }

    public void updatePlayerSeconds(Integer seconds) {
        for (Player player : getPlayers()) {
            if (getPlayers().indexOf(player) == 0) {
                DiscovQueue.getInstance().getPlayerManager().setSeconds(player, seconds);
            }
            DiscovQueue.getInstance().getPlayerManager().updateSeconds(player);
        }
    }

    List<Player> getBatch() {
        List<Player> batch = new ArrayList<>();
        if (getPlayers().size() < getBatchSize()) {
            batch.addAll(getPlayers().subList(0, getPlayers().size()));
        } else {
            batch.addAll(getPlayers().subList(0, getBatchSize()));
        }
        return batch;
    }
}
