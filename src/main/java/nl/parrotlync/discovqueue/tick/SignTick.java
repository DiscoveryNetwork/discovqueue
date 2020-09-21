package nl.parrotlync.discovqueue.tick;

import nl.parrotlync.discovqueue.DiscovQueue;
import nl.parrotlync.discovqueue.model.Queue;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class SignTick {
    private static BukkitTask task;

    public static void start(long delay, long interval) {
        task = Bukkit.getScheduler().runTaskTimer(DiscovQueue.getInstance(), new Runnable() {

            @Override
            public void run() {
                List<Queue> queues = DiscovQueue.getInstance().getQueueManager().getQueues();
                for (Queue queue : queues) {
                    if (queue.isOpened()) {
                        queue.updateSigns();
                    }
                }
            }
        }, delay, interval);
    }
}
