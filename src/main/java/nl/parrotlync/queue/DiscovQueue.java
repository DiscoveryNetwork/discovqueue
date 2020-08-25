package nl.parrotlync.queue;

import nl.parrotlync.queue.command.QueueCommandExecutor;
import nl.parrotlync.queue.listener.QueueListener;
import nl.parrotlync.queue.manager.PlayerManager;
import nl.parrotlync.queue.manager.QueueManager;
import nl.parrotlync.queue.tick.PlayerTick;
import nl.parrotlync.queue.tick.SignTick;
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

    public static DiscovQueue getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        this.getCommand("queue").setExecutor(new QueueCommandExecutor());
        getServer().getPluginManager().registerEvents(new QueueListener(), this);
        queueManager.load();
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

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }
}
