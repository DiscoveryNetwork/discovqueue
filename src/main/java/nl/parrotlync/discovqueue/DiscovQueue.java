package nl.parrotlync.discovqueue;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import nl.parrotlync.discovqueue.command.QueueCommandExecutor;
import nl.parrotlync.discovqueue.listener.QueueListener;
import nl.parrotlync.discovqueue.listener.QueueSignAction;
import nl.parrotlync.discovqueue.manager.PlayerManager;
import nl.parrotlync.discovqueue.manager.QueueManager;
import nl.parrotlync.discovqueue.tick.PlayerTick;
import nl.parrotlync.discovqueue.tick.SignTick;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscovQueue extends JavaPlugin {
    private QueueManager queueManager;
    private PlayerManager playerManager;
    private static DiscovQueue instance;

    public DiscovQueue() {
        queueManager = new QueueManager();
        playerManager = new PlayerManager();
        instance = this;
    }

    @Override
    public void onEnable() {
        this.getCommand("queue").setExecutor(new QueueCommandExecutor());
        getServer().getPluginManager().registerEvents(new QueueListener(), this);
        queueManager.load();
        SignAction.register(new QueueSignAction());
        PlayerTick.start(0L, 20L);
        SignTick.start(0L, 20L);
        getLogger().info("DiscovQueue is now enabled!");
    }

    @Override
    public void onDisable() {
        PlayerTick.stop();
        queueManager.save();
        getLogger().info("DiscovQueue is now disabled!");
    }

    public static DiscovQueue getInstance() {
        return instance;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }
}
