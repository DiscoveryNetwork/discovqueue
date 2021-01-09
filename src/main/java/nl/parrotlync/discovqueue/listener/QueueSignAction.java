package nl.parrotlync.discovqueue.listener;

import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import nl.parrotlync.discovqueue.DiscovQueue;
import nl.parrotlync.discovqueue.model.RideQueue;
import nl.parrotlync.discovqueue.util.ChatUtil;

public class QueueSignAction extends SignAction {

    @Override
    public boolean match(SignActionEvent info) {
        return info.isType("queue");
    }

    @Override
    public void execute(SignActionEvent info) {
        if (info.isPowered() && info.isAction(SignActionType.GROUP_ENTER)) {
            RideQueue queue = (RideQueue) DiscovQueue.getInstance().getQueueManager().getQueue(info.getLine(2));
            if (info.getLine(3).equalsIgnoreCase("teleport")) {
                queue.teleportBatch();
            } else if (info.getLine(3).equalsIgnoreCase("enter")) {
                queue.enterBatch(info.getMembers());
            } else {
                int seconds = Integer.parseInt(info.getLine(3));
                queue.setInterval(seconds);
            }
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        if (event.isCartSign()) { return false; }
        if (event.getPlayer().hasPermission("discovqueue.manage")) {
            if (DiscovQueue.getInstance().getQueueManager().getQueue(event.getLine(2)) instanceof RideQueue) {
                if (event.getLine(3).equalsIgnoreCase("teleport")) {
                    ChatUtil.sendMessage(event.getPlayer(), "§7Teleporter sign created!", true);
                    return true;
                }

                if (event.getLine(3).equalsIgnoreCase("enter")) {
                    ChatUtil.sendMessage(event.getPlayer(), "§7Enter sign created!", true);
                    return true;
                }

                try {
                    Integer.parseInt(event.getLine(3));
                    ChatUtil.sendMessage(event.getPlayer(), "§7Interval updater created!", true);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return false;
    }
}
