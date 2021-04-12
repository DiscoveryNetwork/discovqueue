package nl.parrotlync.discovqueue.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ChatUtil {

    public static void sendMessage(CommandSender sender, String msg, boolean withPrefix) {
        if (withPrefix) {
            msg = "§8[§aQueue§8] " + msg;
        }
        sender.sendMessage(msg);
    }

    public static void broadcast(String msg, String permission, boolean withPrefix) {
        if (withPrefix) {
            msg = "§8[§aQueue§8] " + msg;
        }
        Bukkit.broadcast(msg, permission);
    }
}
