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

    public RideQueue(String name) {
        setName(name);
    }

    @Override
    public void teleportBatch(Collection<MinecartMember<?>> minecartMembers) {
        setBatchSize(minecartMembers.size());
        if (isOpened() && !isPaused()) {
            List<Player> teleportedPlayers = new ArrayList<>();
            for (Player player : getPlayers()) {
                for (MinecartMember<?> minecartMember : minecartMembers) {
                    if (minecartMember.getAvailableSeatCount(player) > 0 && minecartMember.addPassengerForced(player)) {
                        DiscovQueue.getInstance().getPlayerManager().removePlayer(player);
                        teleportedPlayers.add(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7 >> §aYour wait is over! §7<<"));
                        break;
                    }
                }
            }

            for (Player player : teleportedPlayers) {
                removePlayer(player);
            }

            updatePlayerSeconds();
            updateSigns();
        }
    }
}
