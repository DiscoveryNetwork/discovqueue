package nl.parrotlync.queue.listener;

import nl.parrotlync.queue.Queue;
import nl.parrotlync.queue.event.PlayerQueueJoinEvent;
import nl.parrotlync.queue.event.PlayerQueueLeaveEvent;
import nl.parrotlync.queue.model.RideQueue;
import nl.parrotlync.queue.util.ChatUtil;
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
        if (event.getLine(0).equalsIgnoreCase("[queue]")) {
            if (event.getPlayer().hasPermission("queue.manage")) {
                RideQueue queue = Queue.getInstance().getQueueManager().getQueue(event.getLine(1));
                if (queue != null) {
                    queue.setSign((Sign) event.getBlock().getState());
                    ChatUtil.sendMessage(event.getPlayer(), "§aSign registered.", true);
                    queue.updateSign();
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
            if (sign.getLine(0).equalsIgnoreCase("[§2Queue§0]")) {
                RideQueue queue = Queue.getInstance().getQueueManager().getQueue(sign.getLine(1).replace("§l", ""));
                if (!player.hasPermission("queue.manage")) {
                    event.setCancelled(true);
                    ChatUtil.sendMessage(player, "§cYou don't have permission to do that!", true);
                } else {
                    queue.setSign(null);
                    ChatUtil.sendMessage(player, "§cSign unregistered.", true);
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
                if (sign.getLine(0).equalsIgnoreCase("[§2Queue§0]")) {
                    RideQueue queue = Queue.getInstance().getQueueManager().getQueue(sign.getLine(1).replace("§l", ""));
                    if (!Queue.getInstance().getPlayerManager().hasPlayer(player)) {
                        if (queue.isOpened() && !queue.isLocked()) {
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueJoinEvent(queue, player));
                        }
                    } else {
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueLeaveEvent(queue, player));
                        ChatUtil.sendMessage(player, "§7You have left the queue for §3" + queue.getName() + " §7queue.", true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQueueJoin(PlayerQueueJoinEvent event) {
        Player player = event.getPlayer();
        if (!Queue.getInstance().getPlayerManager().hasPlayer(player)) {
            RideQueue queue = event.getQueue();
            Queue.getInstance().getPlayerManager().addPlayer(player, queue);
            queue.addPlayer(player);
            ChatUtil.sendMessage(player, "§7You have joined the queue for §3" + queue.getName() + " §7queue.", true);
            queue.updateSign();
        } else {
            ChatUtil.sendMessage(player, "§cYou are already in a queue! Type §o/queue leave §cto leave.", true);
        }
    }

    @EventHandler
    public void onPlayerQueueLeave(PlayerQueueLeaveEvent event) {
        Player player = event.getPlayer();
        if (Queue.getInstance().getPlayerManager().hasPlayer(player)) {
            RideQueue queue = event.getQueue();
            Queue.getInstance().getPlayerManager().removePlayer(player);
            queue.removePlayer(player);
            queue.updateSign();
        }
    }
}
