package nl.parrotlync.queue.command;

import nl.parrotlync.queue.DiscovQueue;
import nl.parrotlync.queue.event.PlayerQueueJoinEvent;
import nl.parrotlync.queue.event.PlayerQueueLeaveEvent;
import nl.parrotlync.queue.model.RideQueue;
import nl.parrotlync.queue.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class QueueCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("queue.use")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                return help(sender);
            }

            if (args[0].equalsIgnoreCase("join") && args.length == 2) {
                Player player = (Player) sender;
                RideQueue queue = DiscovQueue.getInstance().getQueueManager().getQueue(args[1]);
                Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueJoinEvent(queue, player));
                return true;
            }

            if (args[0].equalsIgnoreCase("leave")) {
                Player player = (Player) sender;
                RideQueue queue = DiscovQueue.getInstance().getPlayerManager().getQueue(player);
                Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueLeaveEvent(queue, player));
                return true;
            }
        }

        if (sender.hasPermission("queue.operate")) {
            if (args[0].equalsIgnoreCase("toggle") && args.length == 3) {
                RideQueue queue = DiscovQueue.getInstance().getQueueManager().getQueue(args[2]);
                String msg;

                if (args[1].equalsIgnoreCase("status")) {
                    queue.toggleOpened();
                    if (queue.isOpened()) {
                        msg = "§2Open";
                    } else {
                        msg = "§4Closed";
                    }
                    ChatUtil.sendMessage(sender, "§3" + queue.getName() + " §7is now " + msg, true);
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
        
        if (sender.hasPermission("queue.manage")) {
            if (args[0].equalsIgnoreCase("create") && args.length == 2) {
                Boolean result = DiscovQueue.getInstance().getQueueManager().createQueue(args[1]);
                if (result) {
                    ChatUtil.sendMessage(sender, "§7Queue created. Please use §o/q location " + args[1] + " §7to set the teleport location.", true);
                    return true;
                }
                return false;
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
                List<RideQueue> queues = DiscovQueue.getInstance().getQueueManager().getQueues();
                StringBuilder msg = new StringBuilder("§7Queue list: §3");
                for (RideQueue queue : queues) {
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
                DiscovQueue.getInstance().getQueueManager().getQueue(args[1]).getBatch();
                return true;
            }
        }

        return help(sender);
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
                ChatUtil.sendMessage(sender, "§3/queue create <name> §7Create a queue", false);
                ChatUtil.sendMessage(sender, "§3/queue remove <name> §7Remove a queue", false);
                ChatUtil.sendMessage(sender, "§3/queue list §7List all queues", false);
            }
        } else {
            ChatUtil.sendMessage(sender, "§cYou do not have permission to do that!", true);
        }
        return true;
    }
}
