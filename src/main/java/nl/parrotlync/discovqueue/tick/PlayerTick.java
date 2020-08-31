package nl.parrotlync.discovqueue.tick;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.discovqueue.DiscovQueue;
import nl.parrotlync.discovqueue.model.RideQueue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class PlayerTick {
    private static BukkitTask task;

    public static void start(long delay, long interval) {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(DiscovQueue.getInstance(), new Runnable() {

            @Override
            public void run() {
                List<RideQueue> queues = DiscovQueue.getInstance().getQueueManager().getQueues();
                for (RideQueue queue : queues) {
                    for (Player player : queue.getPlayers()) {
                        String msg;
                        if (!queue.isPaused()) {
                            int secondsLeft = 0;
                            if (DiscovQueue.getInstance().getPlayerManager().getSeconds(player) != 0) {
                                secondsLeft = DiscovQueue.getInstance().getPlayerManager().getSeconds(player) - 1;
                            }
                            DiscovQueue.getInstance().getPlayerManager().setSeconds(player, secondsLeft);
                            int minutes = secondsLeft / 60;
                            int seconds = secondsLeft % 60;
                            msg = "§7You are §c#" + (queue.getPlayers().indexOf(player) + 1) + " §7in line for §c§l" +
                                    queue.getName() + " §7| ETA: " + String.format("%02d:%02d", minutes, seconds);
                        } else if (!queue.isPaused()) {
                            msg = "§7You are §c#" + (queue.getPlayers().indexOf(player) + 1) + " §7in line for §c§l" +
                                    queue.getName() + " §7| ETA: §8---";
                        } else {
                            msg = "§7You are §c#" + (queue.getPlayers().indexOf(player) + 1) + " §7in line for §c§l" +
                                    queue.getName() + " §7| §6Queue Paused!";
                        }
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
                    }
                }
            }
        }, delay, interval);
    }

    public static void stop() { task.cancel(); }
}
