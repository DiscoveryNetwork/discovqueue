package nl.parrotlync.discovqueue.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtil {

    public static void sendMessage(CommandSender sender, String msg, boolean withPrefix) {
        if (withPrefix) {
            msg = "§8[§aQueue§8] " + msg;
        }
        sender.sendMessage(msg);
    }

    public static void broadcast(String msg, boolean withPrefix) {
        if (withPrefix) {
            msg = "§8[§aQueue§8] " + msg;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(msg);
        }
    }
}
