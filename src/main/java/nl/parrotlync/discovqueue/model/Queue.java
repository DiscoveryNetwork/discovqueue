package nl.parrotlync.discovqueue.model;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.discovqueue.DiscovQueue;
import nl.parrotlync.discovqueue.task.QueueTimer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.RedstoneWallTorch;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Queue {
    protected String name;
    protected int size;
    protected int interval;
    private BukkitTask task;
    protected boolean opened = false;
    protected boolean locked = false;
    protected boolean paused = false;
    protected int count = 0;
    private Location location;
    protected final List<Player> players = new ArrayList<>();
    private final HashMap<Sign, SignType> signs = new HashMap<>();

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
        if (players.size() != 0) {
            count = interval;
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    protected void resetCount() {
        count = interval;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean toggleOpened() {
        opened = !opened;
        updateSigns();
        if (!opened) {
            for (Player player : players) {
                DiscovQueue.getInstance().getPlayerManager().removePlayer(player);
            }
            players.clear();
            stopTimer();
        }
        return opened;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean toggleLocked() {
        locked = !locked;
        updateSigns();
        return locked;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean togglePaused() {
        paused = !paused;
        updateSigns();
        return paused;
    }

    public void addPlayer(Player player) {
        if (players.size() == 0) { resetCount(); }
        players.add(player);
        updateSigns();
    }

    public void removePlayer(Player player) {
        players.remove(player);
        updateSigns();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addSign(Sign sign, SignType signType) {
        signs.put(sign, signType);
    }

    public void removeSign(Sign sign) {
        signs.remove(sign);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public List<Player> getBatch() {
        List<Player> batch = new ArrayList<>();
        if (players.size() < size) {
            batch.addAll(players.subList(0, players.size()));
        } else {
            batch.addAll(players.subList(0, size));
        }
        return batch;
    }

    public void teleportBatch() {
        if (location != null && opened && !paused) {
            List<Player> batch = getBatch();
            for (Player player : batch) {
                player.teleport(location);
                DiscovQueue.getInstance().getPlayerManager().removePlayer(player);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7 >> §aYour wait is over! §7<<"));
                players.remove(player);
            }
            updateSigns();
            resetCount();
        }
    }
    
    public void activateReceivers() {
        Bukkit.getScheduler().runTask(DiscovQueue.getInstance(), () -> {
            for (Sign sign : signs.keySet()) {
                if (signs.get(sign) == SignType.RECEIVER) {
                    Block block = sign.getBlock();
                    if (sign.getBlockData() instanceof WallSign) {
                        WallSign wallSign = (WallSign) sign.getBlockData();
                        BlockFace facing = wallSign.getFacing();
                        block.setType(Material.REDSTONE_WALL_TORCH);
                        RedstoneWallTorch torch = (RedstoneWallTorch) block.getBlockData();
                        torch.setFacing(facing);
                        block.setBlockData(torch);
                        block.getState().update();
                        Bukkit.getScheduler().runTaskLater(DiscovQueue.getInstance(), () -> {
                            block.setType(Material.OAK_WALL_SIGN);
                            block.getState().update();
                            Sign newSign = (Sign) block.getState();
                            newSign.setLine(0, "[qreceiver]");
                            newSign.setLine(1, name);
                            newSign.update();
                        }, 20);
                    } else if (sign.getBlockData() instanceof org.bukkit.block.data.type.Sign) {
                        block.setType(Material.REDSTONE_TORCH);
                        block.getState().update();
                        Bukkit.getScheduler().runTaskLater(DiscovQueue.getInstance(), () -> {
                            block.setType(Material.OAK_SIGN);
                            block.getState().update();
                            Sign newSign = (Sign) block.getState();
                            newSign.setLine(0, "[qreceiver]");
                            newSign.setLine(1, name);
                            newSign.update();
                        }, 20);
                    }
                }
            }
        });
    }

    public HashMap<Sign, SignType> getSigns() {
        return signs;
    }

    public void updateSigns() {
        Bukkit.getScheduler().runTask(DiscovQueue.getInstance(), () -> {
            for (Sign sign : signs.keySet()) {
                SignType signType = signs.get(sign);
                if (signType == SignType.QUEUE) {
                    updateQueueSign(sign);
                }
            }
        });
    }

    protected void runTimer() {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(DiscovQueue.getInstance(), new QueueTimer(this), 0L, 20L);
    }

    public void stopTimer() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    private void updateQueueSign(Sign sign) {
        sign.setLine(0, "[§2Queue§0]");
        sign.setLine(1, "§l" + name);
        if (opened) {
            sign.setLine(2, "§o" + players.size() + " waiting...");
            sign.setLine(3, "§1§oClick to join!");
            if (locked) {
                sign.setLine(3, "§c§oLocked");
            }
        } else {
            sign.setLine(2, "");
            sign.setLine(3, "§4Closed");
        }
        sign.update();
    }
}
