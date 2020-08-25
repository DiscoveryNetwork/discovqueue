package nl.parrotlync.queue.model;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.queue.DiscovQueue;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RideQueue {
    private String name;
    private Location location;
    private Integer batchSize;
    private Integer interval;
    private Boolean opened;
    private Boolean locked;
    private Boolean paused;
    private HashMap<Sign, SignType> signs = new HashMap<>();
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
    }

    public void getBatch() {
        if (opened && !paused && location != null && batchSize != null) {
            String msg = "§7 >> §aYour wait is over! §7<<";
            List<Player> batch = new ArrayList<>();
            if (players.size() < batchSize) {
                batch.addAll(players.subList(0, players.size()));
            } else {
                batch.addAll(players.subList(0, batchSize));
            }

            for (Player player : batch) {
                DiscovQueue.getInstance().getPlayerManager().removePlayer(player);
                removePlayer(player);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
                player.teleport(location);
            }

            for (Player player : players) {
                if (players.indexOf(player) == 0) {
                    DiscovQueue.getInstance().getPlayerManager().setSeconds(player, interval);
                }
                DiscovQueue.getInstance().getPlayerManager().updateSeconds(player);
            }
            updateSigns();
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

    public List<Sign> getSigns() {
        return new ArrayList<>(signs.keySet());
    }

    public void addSign(Sign sign, SignType type) {
        signs.put(sign, type);
    }

    public void removeSign(Sign sign) { signs.remove(sign); }

    public SignType getSignType(Sign sign) {
        return signs.get(sign);
    }

    public void updateSigns() {
        for (Sign sign : signs.keySet()) {
            if (signs.get(sign) == SignType.QUEUE) {
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
            } else if (signs.get(sign) == SignType.WAIT_TIME) {
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
            } else if (signs.get(sign) == SignType.QUEUE_INFO) {
                sign.setLine(0, "[§5QueueInfo§0]");
                sign.setLine(1, "§l" + name);
                if (opened) {
                    sign.setLine(2, "§o" + players.size() + " waiting...");
                    if (paused) {
                        sign.setLine(3, "§6&oPaused");
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
            }
            sign.update();
        }
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
    }

    public List<Player> getPlayers() { return players; }

    public boolean isOpened() { return opened; }

    public boolean isLocked() { return locked; }

    public boolean isPaused() { return paused; }

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
}
