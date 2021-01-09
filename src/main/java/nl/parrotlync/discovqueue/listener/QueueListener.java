package nl.parrotlync.discovqueue.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.discovqueue.DiscovQueue;
import nl.parrotlync.discovqueue.event.PlayerQueueJoinEvent;
import nl.parrotlync.discovqueue.event.PlayerQueueLeaveEvent;
import nl.parrotlync.discovqueue.model.Queue;
import nl.parrotlync.discovqueue.model.SignType;
import nl.parrotlync.discovqueue.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class QueueListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String header = event.getLine(0).toLowerCase();
        if (header.equals("[queue]") || header.equals("[qreceiver]")) {
            if (event.getPlayer().hasPermission("discovqueue.manage")) {
                Queue queue = DiscovQueue.getInstance().getQueueManager().getQueue(event.getLine(1));
                if (queue != null) {
                    Sign sign = (Sign) event.getBlock().getState();
                    if (header.equals("[queue]")) {
                        queue.addSign(sign, SignType.QUEUE);
                    }
                    if (header.equals("[qreceiver]")) {
                        queue.addSign(sign, SignType.RECEIVER);
                    }
                    ChatUtil.sendMessage(event.getPlayer(), "§aSign registered.", true);
                } else {
                    event.getPlayer().sendMessage("§cA queue with that name doesn't exist!");
                    event.setCancelled(true);
                }
            } else {
                event.getPlayer().sendMessage("§cYou don't have permission to do that!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getBlock().getState();
            String header = ChatColor.stripColor(sign.getLine(0)).toLowerCase();
            if (header.equals("[queue]") || header.equals("[qreceiver]")) {
                Queue queue = DiscovQueue.getInstance().getQueueManager().getQueue(ChatColor.stripColor(sign.getLine(1)));
                if (queue != null) {
                    if (event.getPlayer().hasPermission("discovqueue.manage")) {
                        queue.removeSign(sign);
                        ChatUtil.sendMessage(event.getPlayer(), "§cSign unregistered.", true);
                    } else {
                        event.getPlayer().sendMessage("§cYou don't have permission to do that!");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                if (ChatColor.stripColor(sign.getLine(0)).toLowerCase().equals("[queue]")) {
                    Queue queue = DiscovQueue.getInstance().getQueueManager().getQueue(ChatColor.stripColor(sign.getLine(1)));
                    if (queue != null) {
                        if (event.getPlayer().hasPermission("discovqueue.use")) {
                            if (DiscovQueue.getInstance().getPlayerManager().hasPlayer(event.getPlayer())) {
                                Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueLeaveEvent(event.getPlayer(), queue));
                            } else {
                                Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueJoinEvent(event.getPlayer(), queue));
                            }
                        } else {
                            event.getPlayer().sendMessage("§cYou don't have permission to do that!");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQueueJoin(PlayerQueueJoinEvent event) {
        if (!DiscovQueue.getInstance().getPlayerManager().hasPlayer(event.getPlayer())) {
            if (event.getQueue().isOpened()) {
                if (!event.getQueue().isLocked()) {
                    event.getQueue().addPlayer(event.getPlayer());
                    DiscovQueue.getInstance().getPlayerManager().addPlayer(event.getPlayer(), event.getQueue());
                    ChatUtil.sendMessage(event.getPlayer(), "§7You have joined the queue for §3" + event.getQueue().getName(), true);
                } else {
                    String msg = "§cWe're sorry, but the queue for §3" + event.getQueue().getName() + " §cis currently locked.";
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
                }
            } else {
                String msg = "§cWe're sorry, but the queue for §3" + event.getQueue().getName() + " §cis currently closed.";
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
            }
        } else {
            ChatUtil.sendMessage(event.getPlayer(),"§cYou are already in a queue! Type §o/q leave §cor click the sign again to leave.", true);
        }
    }

    @EventHandler
    public void onPlayerQueueLeave(PlayerQueueLeaveEvent event) {
        if (DiscovQueue.getInstance().getPlayerManager().hasPlayer(event.getPlayer())) {
            event.getQueue().removePlayer(event.getPlayer());
            DiscovQueue.getInstance().getPlayerManager().removePlayer(event.getPlayer());
            ChatUtil.sendMessage(event.getPlayer(), "§7You have left the queue for §3" + event.getQueue().getName(), true);
        }
    }
}
