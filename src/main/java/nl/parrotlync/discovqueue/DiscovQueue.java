package nl.parrotlync.discovqueue;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import nl.parrotlync.discovqueue.command.QueueCommandExecutor;
import nl.parrotlync.discovqueue.listener.QueueListener;
import nl.parrotlync.discovqueue.listener.QueueSignAction;
import nl.parrotlync.discovqueue.manager.PlayerManager;
import nl.parrotlync.discovqueue.manager.QueueManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscovQueue extends JavaPlugin {
    private static DiscovQueue instance;
    private final QueueManager queueManager;
    private final PlayerManager playerManager;

    public DiscovQueue() {
        queueManager = new QueueManager();
        playerManager = new PlayerManager();
        instance = this;
    }

    @Override
    public void onEnable() {
        queueManager.load();
        getCommand("queue").setExecutor(new QueueCommandExecutor());
        getServer().getPluginManager().registerEvents(new QueueListener(), this);
        SignAction.register(new QueueSignAction());
        getLogger().info("DiscovQueue is now enabled!");
    }

    @Override
    public void onDisable() {
        queueManager.save();
        getLogger().info("DiscovQueue is now disabled!");
    }

    public static DiscovQueue getInstance() {
        return instance;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
