package nl.parrotlync.discovqueue;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import nl.parrotlync.discovqueue.command.QueueCommandExecutor;
import nl.parrotlync.discovqueue.listener.QueueListener;
import nl.parrotlync.discovqueue.listener.QueueSignAction;
import nl.parrotlync.discovqueue.manager.PlayerManager;
import nl.parrotlync.discovqueue.manager.QueueManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class DiscovQueue extends JavaPlugin {
    private static DiscovQueue instance;
    private final QueueManager queueManager;
    private final PlayerManager playerManager;
    private final QueueSignAction queueSignAction;

    public DiscovQueue() {
        queueManager = new QueueManager();
        playerManager = new PlayerManager();
        queueSignAction = new QueueSignAction();
        instance = this;
    }

    @Override
    public void onEnable() {
        queueManager.load();
        Objects.requireNonNull(getCommand("queue")).setExecutor(new QueueCommandExecutor());
        getServer().getPluginManager().registerEvents(new QueueListener(), this);
        SignAction.register(queueSignAction);
        getLogger().info("DiscovQueue is now enabled!");
    }

    @Override
    public void onDisable() {
        queueManager.save();
        SignAction.unregister(queueSignAction);
        getServer().getScheduler().cancelTasks(this);
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
