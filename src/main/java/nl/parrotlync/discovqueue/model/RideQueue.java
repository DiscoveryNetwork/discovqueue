package nl.parrotlync.discovqueue.model;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.parrotlync.discovqueue.DiscovQueue;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RideQueue extends Queue {

    public RideQueue(String name, int size, int interval) {
        this.name = name;
        this.size = size;
        this.interval = interval;
        this.count = interval;
        runTimer();
    }

    public void enterBatch(Collection<MinecartMember<?>> minecartMembers) {
        List<Player> teleportedPlayers = new ArrayList<>();
        if (opened && !paused && players.size() != 0) {
            for (Player player : players) {
                for (MinecartMember<?> minecartMember : minecartMembers) {
                    if (minecartMember.getAvailableSeatCount(player) > 0 && minecartMember.addPassengerForced(player)) {
                        DiscovQueue.getInstance().getPlayerManager().removePlayer(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7 >> §aYour wait is over! §7<<"));
                        teleportedPlayers.add(player);
                    }
                }
            }

            for (Player player : teleportedPlayers) {
                players.remove(player);
            }

            updateSigns();
            resetCount();
        }
    }
}
