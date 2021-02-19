package nl.parrotlync.discovqueue.task;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.discovqueue.model.Queue;
import org.bukkit.entity.Player;

public class QueueTimer implements Runnable {
    private final Queue queue;

    public QueueTimer(Queue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        int count = queue.getCount();
        queue.updateSigns();
        if (queue.isOpened() && queue.getPlayers().size() != 0) {
            for (Player player : queue.getPlayers()) {
                int index = queue.getPlayers().indexOf(player);
                String msg;

                if (!queue.isPaused()) {
                    int secondsLeft;

                    if (index < queue.getSize()) {
                        secondsLeft = count;
                    } else {
                        secondsLeft = count + (queue.getInterval() * (index / queue.getSize()));
                    }

                    int minutes = secondsLeft / 60;
                    int seconds = secondsLeft % 60;
                    msg = "§7You are §c#" + (index + 1) + " §7in line for §c§l" + queue.getName() + " §7| ETA: " + String.format("%02d:%02d", minutes, seconds);

                } else {
                    msg = "§7You are §c#" + (index + 1) + " §7in line for §c§l" + queue.getName() + " §7| §6Queue Paused!";
                }
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
            }

            if (count > 0) {
                queue.setCount(count - 1);
                if (count == 1) { queue.activateReceivers(); }
            }
        }
    }
}
