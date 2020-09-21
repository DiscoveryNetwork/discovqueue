package nl.parrotlync.discovqueue.command;

import nl.parrotlync.discovqueue.DiscovQueue;
import nl.parrotlync.discovqueue.event.PlayerQueueJoinEvent;
import nl.parrotlync.discovqueue.event.PlayerQueueLeaveEvent;
import nl.parrotlync.discovqueue.model.GenericQueue;
import nl.parrotlync.discovqueue.model.Queue;
import nl.parrotlync.discovqueue.model.RideQueue;
import nl.parrotlync.discovqueue.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class QueueCommandExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("discovqueue.use")) {
            if (args.length == 0) {
                ChatUtil.sendMessage(sender, "§6DiscovQueue-1.12.2-v2.0.1 §7(§aParrotLync§7) - Use /queue help", false);
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
                return help(sender);
            }

            if (args[0].equalsIgnoreCase("join") && args.length == 2) {
                Player player = (Player) sender;
                Queue queue = DiscovQueue.getInstance().getQueueManager().getQueue(args[1]);
                Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueJoinEvent(queue, player));
                return true;
            }

            if (args[0].equalsIgnoreCase("leave")) {
                Player player = (Player) sender;
                Queue queue = DiscovQueue.getInstance().getPlayerManager().getQueue(player);
                Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueLeaveEvent(queue, player));
                return true;
            }
        }

        if (sender.hasPermission("discovqueue.operate")) {
            if (args[0].equalsIgnoreCase("toggle") && args.length == 3) {
                Queue queue = DiscovQueue.getInstance().getQueueManager().getQueue(args[2]);
                String msg;

                if (args[1].equalsIgnoreCase("status")) {
                    queue.toggleOpened();
                    if (queue.isOpened()) {
                        msg = "§2Open";
                    } else {
                        msg = "§4Closed";
                    }
                    ChatUtil.broadcast("§3" + queue.getName() + " §7is now " + msg, true);
                    return true;
                }

                if (args[1].equalsIgnoreCase("locked")) {
                    queue.toggleLocked();
                    if (queue.isLocked()) {
                        msg = "§4Locked";
                    } else {
                        msg = "§2Unlocked";
                    }
                    ChatUtil.sendMessage(sender, "§3" + queue.getName() + " §7is now " + msg, true);
                    return true;
                }

                if (args[1].equalsIgnoreCase("paused")) {
                    queue.togglePaused();
                    if (queue.isPaused()) {
                        msg = "§4Paused";
                    } else {
                        msg = "§2Unpaused";
                    }
                    ChatUtil.sendMessage(sender, "§3" + queue.getName() + " §7is now " + msg, true);
                    return true;
                }
            }
        }
        
        if (sender.hasPermission("discovqueue.manage")) {
            if (args[0].equalsIgnoreCase("create") && args.length == 3) {
                Queue queue = DiscovQueue.getInstance().getQueueManager().createQueue(args[2], args[1]);
                if (queue instanceof RideQueue) {
                    ChatUtil.sendMessage(sender, "§7Queue created. Please use §o/q interval " + args[2] + " <seconds> §7to set the interval.", true);
                } else if (queue instanceof GenericQueue) {
                    ChatUtil.sendMessage(sender, "§7Queue created. Please use §o/q location " + args[2] + " §7to set the teleport location.", true);
                }
                return false;
            }

            if (args[0].equalsIgnoreCase("tp") && args.length == 2) {
                Player player = (Player) sender;
                Location location = DiscovQueue.getInstance().getQueueManager().getQueue(args[1]).getLocation();
                if (location != null) {
                    player.teleport(location);
                    ChatUtil.sendMessage(sender, "§7Teleporting...", true);
                } else {
                    ChatUtil.sendMessage(sender, "§cLocation is not set for this queue!", true);
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("location") && args.length == 2) {
                Player player = (Player) sender;
                DiscovQueue.getInstance().getQueueManager().getQueue(args[1]).setLocation(player.getLocation());
                ChatUtil.sendMessage(sender, "§7Location set. Please use §o/q size " + args[1] + " <size> §7to set the batch size.", true);
                return true;
            }

            if (args[0].equalsIgnoreCase("size") && args.length == 3) {
                DiscovQueue.getInstance().getQueueManager().getQueue(args[1]).setBatchSize(Integer.parseInt(args[2]));
                ChatUtil.sendMessage(sender, "§7Size set. Please use §o/q interval " + args[1] + " <interval> §7to set the interval.", true);
                return true;
            }

            if (args[0].equalsIgnoreCase("interval") && args.length == 3) {
                DiscovQueue.getInstance().getQueueManager().getQueue(args[1]).setInterval(Integer.parseInt(args[2]));
                ChatUtil.sendMessage(sender, "§7Almost done! Please create a Queue sign.", true);
                return true;
            }

            if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
                Boolean result = DiscovQueue.getInstance().getQueueManager().removeQueue(args[1]);
                if (result) {
                    ChatUtil.sendMessage(sender, "§7Queue has been removed.", true);
                    return true;
                }
                return false;
            }

            if (args[0].equalsIgnoreCase("list")) {
                List<Queue> queues = DiscovQueue.getInstance().getQueueManager().getQueues();
                StringBuilder msg = new StringBuilder("§7Queue list: §3");
                for (Queue queue : queues) {
                    if (queues.indexOf(queue) == 0) {
                        msg.append(queue.getName());
                    } else {
                        msg.append(", ").append(queue.getName());
                    }
                }
                ChatUtil.sendMessage(sender, msg.toString(), true);
                return true;
            }

            if (args[0].equalsIgnoreCase("batch") && args.length == 2) {
                DiscovQueue.getInstance().getQueueManager().getQueue(args[1]).teleportBatch();
                return true;
            }
        }

        return help(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("join");
            suggestions.add("leave");
            if (sender.hasPermission("queue.operate")) {
                suggestions.add("toggle");
            }
            if (sender.hasPermission("queue.manage")) {
                suggestions.add("create");
                suggestions.add("remove");
                suggestions.add("list");
                suggestions.add("tp");
            }
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<String>());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("tp")) {
                for (Queue queue : DiscovQueue.getInstance().getQueueManager().getQueues()) {
                    suggestions.add(queue.getName());
                }
            } else if (args[0].equalsIgnoreCase("toggle")) {
                suggestions.add("status");
                suggestions.add("locked");
                suggestions.add("paused");
            } else if (args[0].equalsIgnoreCase("create")) {
                suggestions.add("generic");
                suggestions.add("ride");
            }
            return StringUtil.copyPartialMatches(args[1], suggestions, new ArrayList<String>());
        }

        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("toggle")) {
                for (Queue queue : DiscovQueue.getInstance().getQueueManager().getQueues()) {
                    suggestions.add(queue.getName());
                }
            }
        }

        return suggestions;
    }

    private boolean help(CommandSender sender) {
        if (sender.hasPermission("queue.use")) {
            ChatUtil.sendMessage(sender, "§f+---+ §aQueue §f+---+", false);
            ChatUtil.sendMessage(sender, "§3/queue join <name> §7Join a queue", false);
            ChatUtil.sendMessage(sender, "§3/queue leave §7Leave your current queue", false);
            if (sender.hasPermission("queue.operate")) {
                ChatUtil.sendMessage(sender, "§3/queue toggle <status/locked/paused> <name> §7Toggle a queue value", false);
            }
            if (sender.hasPermission("queue.manage")) {
                ChatUtil.sendMessage(sender, "§3/queue create <generic/ride> <name> §7Create a queue", false);
                ChatUtil.sendMessage(sender, "§3/queue remove <name> §7Remove a queue", false);
                ChatUtil.sendMessage(sender, "§3/queue tp <name> §7Teleport to a queue location", false);
                ChatUtil.sendMessage(sender, "§3/queue list §7List all queues", false);
            }
        } else {
            ChatUtil.sendMessage(sender, "§cYou do not have permission to do that!", true);
        }
        return true;
    }
}
