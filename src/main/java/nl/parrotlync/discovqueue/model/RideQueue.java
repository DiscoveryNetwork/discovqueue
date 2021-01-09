package nl.parrotlync.discovqueue.model;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.discovqueue.DiscovQueue;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class RideQueue extends Queue {

    public RideQueue(String name, int size, int interval) {
        this.name = name;
        this.size = size;
        this.interval = interval;
        this.count = interval;
    }

    public void enterBatch(Collection<MinecartMember<?>> minecartMembers) {
        if (opened && !paused) {
            List<Player> batch = getPlayers();
            for (Player player : batch) {
                for (MinecartMember<?> minecartMember : minecartMembers) {
                    if (minecartMember.getAvailableSeatCount(player) > 0 && minecartMember.addPassengerForced(player)) {
                        DiscovQueue.getInstance().getPlayerManager().removePlayer(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7 >> §aYour wait is over! §7<<"));
                        players.remove(player);
                    }
                }
            }
            updateSigns();
            count = interval;
            if (players.size() == 0) { stopTimer(); }
        }
    }
}
