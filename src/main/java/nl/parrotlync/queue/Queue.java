package nl.parrotlync.queue;

import nl.parrotlync.queue.command.QueueCommandExecutor;
import nl.parrotlync.queue.listener.QueueListener;
import nl.parrotlync.queue.manager.PlayerManager;
import nl.parrotlync.queue.manager.QueueManager;
import nl.parrotlync.queue.tick.PlayerTick;
import org.bukkit.plugin.java.JavaPlugin;

public class Queue extends JavaPlugin {
    private QueueManager queueManager;
    private PlayerManager playerManager;
    private static Queue instance;

    public Queue() {
        queueManager = new QueueManager();
        playerManager = new PlayerManager();
        instance = this;
    }

    public static Queue getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        this.getCommand("queue").setExecutor(new QueueCommandExecutor());
        getServer().getPluginManager().registerEvents(new QueueListener(), this);
        queueManager.load();
        PlayerTick.start(0L, 20L);
        getLogger().info("Queue is now enabled!");
    }

    @Override
    public void onDisable() {
        PlayerTick.stop();
        queueManager.save();
        getLogger().info("Queue is now disabled!");
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }
}
