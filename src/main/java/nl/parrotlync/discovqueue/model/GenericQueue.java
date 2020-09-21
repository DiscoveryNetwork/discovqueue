package nl.parrotlync.discovqueue.model;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.discovqueue.DiscovQueue;
import org.bukkit.entity.Player;

import java.util.List;

public class GenericQueue extends Queue {

    public GenericQueue(String name) {
        setName(name);
    }

    @Override
    public void teleportBatch() {
        if (isOpened() && !isPaused() && getLocation() != null && getBatchSize() != null) {
            String msg = "§7 >> §aYour wait is over! §7<<";
            List<Player> batch = getBatch();

            for (Player player : batch) {
                DiscovQueue.getInstance().getPlayerManager().removePlayer(player);
                removePlayer(player);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
                player.teleport(getLocation());
            }

            updatePlayerSeconds();
            updateSigns();
        }
    }
}
