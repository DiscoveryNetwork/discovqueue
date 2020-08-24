package nl.parrotlync.queue.util;

import org.bukkit.command.CommandSender;

public class ChatUtil {

    public static void sendMessage(CommandSender sender, String msg, boolean withPrefix) {
        if (withPrefix) {
            msg = "§8[§aQueue§8] " + msg;
        }
        sender.sendMessage(msg);
    }
}
