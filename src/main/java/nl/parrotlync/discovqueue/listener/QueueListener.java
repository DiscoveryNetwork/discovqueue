package nl.parrotlync.discovqueue.listener;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.discovqueue.DiscovQueue;
import nl.parrotlync.discovqueue.event.PlayerQueueJoinEvent;
import nl.parrotlync.discovqueue.event.PlayerQueueLeaveEvent;
import nl.parrotlync.discovqueue.manager.PlayerManager;
import nl.parrotlync.discovqueue.model.RideQueue;
import nl.parrotlync.discovqueue.model.SignType;
import nl.parrotlync.discovqueue.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class QueueListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String header = event.getLine(0);
        if (header.equalsIgnoreCase("[queue]") || header.equalsIgnoreCase("[waittime]") || header.equalsIgnoreCase("[queueinfo]")) {
            if (event.getPlayer().hasPermission("queue.manage")) {
                RideQueue queue = DiscovQueue.getInstance().getQueueManager().getQueue(event.getLine(1));
                if (queue != null) {
                    Sign sign = (Sign) event.getBlock().getState();
                    SignType type;
                    if (header.equalsIgnoreCase("[queue]")) {
                        setQueueText(event, queue);
                        type = SignType.QUEUE;
                    } else if (header.equalsIgnoreCase("[waittime]")) {
                        setWaitText(event, queue);
                        type = SignType.WAIT_TIME;
                    } else {
                        setInfoText(event, queue);
                        type = SignType.QUEUE_INFO;
                    }
                    queue.addSign(sign, type);
                    ChatUtil.sendMessage(event.getPlayer(), "§aSign registered.", true);
                } else {
                    ChatUtil.sendMessage(event.getPlayer(), "§cThat queue does not exist!", true);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getBlock().getState();
            Player player = event.getPlayer();
            if (sign.getLine(0).equals("[§2Queue§0]") || sign.getLine(0).equals("[§1WaitTime§0]") || sign.getLine(0).equals("[§5QueueInfo§0]")) {
                if (DiscovQueue.getInstance().getQueueManager().getQueue(sign.getLine(1).replace("§l", "")) != null) {
                    RideQueue queue = DiscovQueue.getInstance().getQueueManager().getQueue(sign.getLine(1).replace("§l", ""));
                    if (!player.hasPermission("queue.manage")) {
                        event.setCancelled(true);
                        ChatUtil.sendMessage(player, "§cYou don't have permission to do that!", true);
                    } else {
                        queue.removeSign(sign);
                        ChatUtil.sendMessage(player, "§cSign unregistered.", true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                if (sign.getLine(0).equals("[§2Queue§0]")) {
                    if (DiscovQueue.getInstance().getQueueManager().getQueue(sign.getLine(1).replace("§l", "")) != null) {
                        RideQueue queue = DiscovQueue.getInstance().getQueueManager().getQueue(sign.getLine(1).replace("§l", ""));
                        if (!DiscovQueue.getInstance().getPlayerManager().hasPlayer(player)) {
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueJoinEvent(queue, player));
                        } else {
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueLeaveEvent(queue, player));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQueueJoin(PlayerQueueJoinEvent event) {
        Player player = event.getPlayer();
        if (!DiscovQueue.getInstance().getPlayerManager().hasPlayer(player)) {
            RideQueue queue = event.getQueue();
            if (queue.isOpened()) {
                if (!queue.isLocked()) {
                    int seconds;
                    if (queue.getPlayers().size() != 0) {
                        int firstPlayerSeconds = DiscovQueue.getInstance().getPlayerManager().getSeconds(queue.getPlayers().get(0));
                        seconds = (((queue.getPlayers().size()) / queue.getBatchSize()) * queue.getInterval()) + firstPlayerSeconds;
                    } else {
                        seconds = (queue.getInterval() > 15) ? 15 : queue.getInterval();
                    }
                    DiscovQueue.getInstance().getPlayerManager().addPlayer(player, queue, seconds);
                    queue.addPlayer(player);
                    ChatUtil.sendMessage(player, "§7You have joined the queue for §3" + queue.getName(), true);
                    queue.updateSigns();
                } else {
                    String msg = "§cWe're sorry, but the queue for §3" + queue.getName() + " §cis currently locked.";
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
                }
            } else {
                String msg = "§cWe're sorry, but the queue for §3" + queue.getName() + " §cis currently closed.";
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
            }
        } else {
            ChatUtil.sendMessage(player, "§cYou are already in a queue! Type §o/queue leave §cor click the sign again to leave.", true);
        }
    }

    @EventHandler
    public void onPlayerQueueLeave(PlayerQueueLeaveEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = DiscovQueue.getInstance().getPlayerManager();
        if (playerManager.hasPlayer(player)) {
            RideQueue queue = event.getQueue();
            if (queue.getPlayers() != null && queue.getPlayers().size() != 0) {
                int index = queue.getPlayers().indexOf(player);
                for (Player queuePlayer : queue.getPlayers().subList(index, queue.getPlayers().size())) {
                    int oldSeconds = playerManager.getSeconds(queuePlayer);
                    DiscovQueue.getInstance().getPlayerManager().setSeconds(queuePlayer, oldSeconds - queue.getInterval());
                }
            }
            playerManager.removePlayer(player);
            queue.removePlayer(player);
            queue.updateSigns();
            ChatUtil.sendMessage(player, "§7You have left the queue for §3" + queue.getName(), true);
        }
    }

    private void setQueueText(SignChangeEvent event, RideQueue queue) {
        event.setLine(0, "[§2Queue§0]");
        event.setLine(1, "§l" + queue.getName());
        if (queue.isOpened()) {
            event.setLine(2, "§o" + queue.getPlayers().size() + " waiting...");
            if (queue.isLocked()) {
                event.setLine(3, "§c§oLocked");
            } else {
                event.setLine(3, "§1§oClick to join!");
            }
        } else {
            event.setLine(2, "");
            event.setLine(3, "§4Closed");
        }
    }

    private void setWaitText(SignChangeEvent event, RideQueue queue) {
        event.setLine(0, "[§1WaitTime§0]");
        event.setLine(1, "§l" + queue.getName());
        if (queue.isOpened()) {
            event.setLine(2, "§o" + queue.getPlayers().size() + " waiting...");
            if (queue.isLocked()) {
                event.setLine(3, "§c§oLocked");
            } else {
                event.setLine(3, "00:15");
            }
        } else {
            event.setLine(2, "");
            event.setLine(3, "§4Closed");
        }
    }

    private void setInfoText(SignChangeEvent event, RideQueue queue) {
        event.setLine(0, "[§5QueueInfo§0]");
        event.setLine(1, "§l" + queue.getName());
        if (queue.isOpened()) {
            event.setLine(2, "§o" + queue.getPlayers().size() + " waiting...");
            if (queue.isPaused()) {
                event.setLine(3, "§6&oPaused");
            } else {
                event.setLine(3, "00:15");
            }
        } else {
            event.setLine(2, "");
            event.setLine(3, "§4Closed");
        }
    }
}
