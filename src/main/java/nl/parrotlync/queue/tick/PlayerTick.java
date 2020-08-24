package nl.parrotlync.queue.tick;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.queue.Queue;
import nl.parrotlync.queue.model.RideQueue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class PlayerTick {
    private static BukkitTask task;

    public static void start(long delay, long interval) {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Queue.getInstance(), new Runnable() {

            @Override
            public void run() {
                List<RideQueue> queues = Queue.getInstance().getQueueManager().getQueues();
                for (RideQueue queue : queues) {
                    if (queue.getSecondsLeft() != 0) {
                        queue.setSecondsLeft(queue.getSecondsLeft() - 1);
                    }
                    for (Player player : queue.getPlayers()) {
                        String msg;
                        if (queue.getSecondsLeft() != null && !queue.isPaused()) {
                            int secondsLeft = ((queue.getPlayers().indexOf(player) / queue.getBatchSize()) * queue.getInterval()) + queue.getSecondsLeft();
                            Integer minutes = secondsLeft / 60;
                            Integer seconds = secondsLeft % 60;
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
