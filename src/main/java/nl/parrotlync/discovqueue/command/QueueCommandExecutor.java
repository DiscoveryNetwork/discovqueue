package nl.parrotlync.discovqueue.command;

import nl.parrotlync.discovqueue.DiscovQueue;
import nl.parrotlync.discovqueue.event.PlayerQueueJoinEvent;
import nl.parrotlync.discovqueue.event.PlayerQueueLeaveEvent;
import nl.parrotlync.discovqueue.model.Queue;
import nl.parrotlync.discovqueue.model.QueueType;
import nl.parrotlync.discovqueue.util.ChatUtil;
import org.bukkit.Bukkit;
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
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (sender.hasPermission("discovqueue.use")) {
                if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                    return help(sender);
                }

                if (args[0].equalsIgnoreCase("join") && args.length == 2) {
                    Queue queue = DiscovQueue.getInstance().getQueueManager().getQueue(args[1]);
                    if (queue != null) {
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueJoinEvent(player, queue));
                    } else {
                        sender.sendMessage("§cA queue with that name doesn't exist!");
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("leave")) {
                    Queue queue = DiscovQueue.getInstance().getPlayerManager().getQueue(player);
                    if (queue != null) {
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueLeaveEvent(player, queue));
                    } else {
                        sender.sendMessage("§cA queue with that name doesn't exist!");
                    }
                    return true;
                }
            } else {
                sender.sendMessage("§cYou don't have permission to do that!");
                return true;
            }

            if (player.hasPermission("discovqueue.manage")) {
                if (args[0].equalsIgnoreCase("create") && args.length == 5) {
                    QueueType type = QueueType.valueOf(args[2].toUpperCase());
                    DiscovQueue.getInstance().getQueueManager().createQueue(args[1], Integer.parseInt(args[3]), Integer.parseInt(args[4]), type);
                    ChatUtil.sendMessage(player, "§7Queue created! Now set the teleport location with §3/q location " + args[1], true);
                    return true;
                }

                if (args[0].equalsIgnoreCase("location") && args.length == 2) {
                    Queue queue = DiscovQueue.getInstance().getQueueManager().getQueue(args[1]);
                    if (queue != null) {
                        queue.setLocation(player.getLocation());
                        ChatUtil.sendMessage(player, "§7Location set! Now create a queue sign.", true);
                    } else {
                        sender.sendMessage("§cA queue with that name doesn't exist!");
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
                    if (DiscovQueue.getInstance().getQueueManager().hasQueue(args[1])) {
                        DiscovQueue.getInstance().getQueueManager().removeQueue(args[1]);
                        ChatUtil.sendMessage(player, "§7Queue has been removed.", true);
                    } else {
                        sender.sendMessage("§cA queue with that name doesn't exist!");
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("teleport") && args.length == 2) {
                    Queue queue = DiscovQueue.getInstance().getQueueManager().getQueue(args[1]);
                    if (queue != null) {
                        if (queue.getLocation() != null) {
                            player.teleport(queue.getLocation());
                        } else {
                            sender.sendMessage("§cQueue location is not set!");
                        }
                    } else {
                        sender.sendMessage("§cA queue with that name doesn't exist!");
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("batch") && args.length == 2) {
                    Queue queue = DiscovQueue.getInstance().getQueueManager().getQueue(args[1]);
                    if (queue != null) {
                        queue.teleportBatch();
                    } else {
                        sender.sendMessage("§cA queue with that name doesn't exist!");
                    }
                    return true;
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
            } else {
                sender.sendMessage("§cYou don't have permission to do that!");
                return true;
            }
        } else {
            sender.sendMessage("§cYou need to be a player to use this command!");
            return true;
        }

        if (sender.hasPermission("discovqueue.operate")) {
            if (args[0].equalsIgnoreCase("toggle") && args.length == 3) {
                Queue queue = DiscovQueue.getInstance().getQueueManager().getQueue(args[2]);
                if (queue != null) {
                    if (args[1].equalsIgnoreCase("status")) {
                        if (queue.toggleOpened()) {
                            ChatUtil.broadcast("§7" + queue.getName() + " is now §aopen", "discovqueue.operate", true);
                        } else {
                            ChatUtil.broadcast("§7" + queue.getName() + " is now §cclosed", "discovqueue.operate", true);
                        }
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("locked")) {
                        if (queue.toggleLocked()) {
                            ChatUtil.broadcast("§7" + queue.getName() + " is now §clocked", "discovqueue.operate", true);
                        } else {
                            ChatUtil.broadcast("§7" + queue.getName() + " is now §aunlocked", "discovqueue.operate", true);
                        }
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("paused")) {
                        if (queue.togglePaused()) {
                            ChatUtil.broadcast("§7" + queue.getName() + " is now §cpaused", "discovqueue.operate", true);
                        } else {
                            ChatUtil.broadcast("§7" + queue.getName() + " is now §aresumed", "discovqueue.operate", true);
                        }
                        return true;
                    }
                }
            }
        }

        return help(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("discovqueue.use")) {
                suggestions.add("join");
                suggestions.add("leave");
            }

            if (sender.hasPermission("discovqueue.operate")) {
                suggestions.add("toggle");
            }

            if (sender.hasPermission("discovqueue.manage")) {
                suggestions.add("create");
                suggestions.add("remove");
                suggestions.add("location");
                suggestions.add("list");
                suggestions.add("teleport");
                suggestions.add("batch");
            }
            StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        if (args.length == 2) {
            if (sender.hasPermission("discovqueue.use")) {
                if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("leave")) {
                    for (Queue queue : DiscovQueue.getInstance().getQueueManager().getQueues()) {
                        suggestions.add(queue.getName());
                    }
                }
            }

            if (sender.hasPermission("discovqueue.operate")) {
                if (args[0].equalsIgnoreCase("toggle")) {
                    suggestions.add("status");
                    suggestions.add("locked");
                    suggestions.add("paused");
                }
            }

            if (sender.hasPermission("discovqueue.manage")) {
                if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("location") || args[0].equalsIgnoreCase("batch")) {
                    for (Queue queue : DiscovQueue.getInstance().getQueueManager().getQueues()) {
                        suggestions.add(queue.getName());
                    }
                }
            }
            StringUtil.copyPartialMatches(args[1], suggestions, new ArrayList<>());
        }

        if (args.length == 3) {
            if (sender.hasPermission("discovqueue.operate")) {
                if (args[0].equalsIgnoreCase("toggle")) {
                    for (Queue queue : DiscovQueue.getInstance().getQueueManager().getQueues()) {
                        suggestions.add(queue.getName());
                    }
                }
            }

            if (sender.hasPermission("discovqueue.manage")) {
                if (args[0].equalsIgnoreCase("create")) {
                    for (QueueType type : QueueType.values()) {
                        suggestions.add(type.name().toLowerCase());
                    }
                }
            }
            StringUtil.copyPartialMatches(args[2], suggestions, new ArrayList<>());
        }

        return suggestions;
    }

    private boolean help(CommandSender sender) {
        if (sender.hasPermission("discovqueue.use")) {
            sender.sendMessage("§f+---+ §aQueue §f+---+");
            sender.sendMessage("§3/queue join <name>");
            sender.sendMessage("§3/queue leave");
            if (sender.hasPermission("discovqueue.operate")) {
                sender.sendMessage("§3/queue toggle <status/locked/paused> <name>");
            }
            if (sender.hasPermission("discovqueue.manage")) {
                sender.sendMessage("§3/queue create <name> <type> <size> <interval>");
                sender.sendMessage("§3/queue remove <name>");
                sender.sendMessage("§3/queue teleport <name>");
                sender.sendMessage("§3/queue list");
            }
        } else {
            sender.sendMessage("§cYou do not have permission to do that!");
        }
        return true;
    }
}
