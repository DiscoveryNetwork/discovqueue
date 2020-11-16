package nl.parrotlync.discovqueue.manager;

import nl.parrotlync.discovqueue.DiscovQueue;
import nl.parrotlync.discovqueue.model.GenericQueue;
import nl.parrotlync.discovqueue.model.Queue;
import nl.parrotlync.discovqueue.model.RideQueue;
import nl.parrotlync.discovqueue.model.storable.QueueLocationStorable;
import nl.parrotlync.discovqueue.model.storable.QueueSignStorable;
import nl.parrotlync.discovqueue.model.storable.QueueStorable;
import nl.parrotlync.discovqueue.model.storable.QueueType;
import nl.parrotlync.discovqueue.util.DataUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueueManager {
    private HashMap<String, Queue> queues = new HashMap<>();
    private String path = "plugins/DiscovQueue/queues.data";

    public Queue getQueue(String name) {
        return queues.get(name.toLowerCase());
    }

    public Queue createQueue(String name, String type) {
        if (queues.get(name.toLowerCase()) == null) {
            if (type.equalsIgnoreCase("generic")) {
                Queue queue = new GenericQueue(name);
                queues.put(name.toLowerCase(), queue);
                return queue;
            } else if (type.equalsIgnoreCase("ride")) {
                Queue queue = new RideQueue(name);
                queues.put(name.toLowerCase(), queue);
                return queue;
            }
        }
        return null;
    }

    public Boolean removeQueue(String name) {
        if (queues.get(name.toLowerCase()) != null) {
            queues.remove(name.toLowerCase());
            return true;
        }
        return false;
    }

    public List<Queue> getQueues() {
        return new ArrayList<>(queues.values());
    }

    public void load() {
        List<QueueStorable> storedQueues = DataUtil.loadObjectFromPath(path);
        if (storedQueues != null) {
            for (QueueStorable storedQueue : storedQueues) {
                Queue queue;
                if (storedQueue.getType() == QueueType.GENERIC) {
                    queue = new GenericQueue(storedQueue.getName());
                } else if (storedQueue.getType() == QueueType.RIDE) {
                    queue = new RideQueue(storedQueue.getName());
                } else {
                    continue;
                }
                queue.setBatchSize(storedQueue.getBatchSize());
                queue.setInterval(storedQueue.getInterval());
                // Teleport location
                QueueLocationStorable location = storedQueue.getLocation();
                World world = Bukkit.getWorld(location.getWorld());
                queue.setLocation(new Location(world, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()));
                // Sign location
                for (QueueSignStorable sign : storedQueue.getSigns()) {
                    World sWorld = Bukkit.getWorld(sign.getWorld());
                    if (sWorld != null && sWorld.getBlockAt(sign.getX(), sign.getY(), sign.getZ()) != null) {
                        if (sWorld.getBlockAt(sign.getX(), sign.getY(), sign.getZ()).getState() instanceof Sign) {
                            queue.addSign((Sign) sWorld.getBlockAt(sign.getX(), sign.getY(), sign.getZ()).getState(), sign.getType());
                        }
                    }
                }
                queue.updateSigns();
                queues.put(storedQueue.getName().toLowerCase(), queue);
            }
            DiscovQueue.getInstance().getLogger().info("Loaded " + queues.size() + " queues from file.");
        } else {
            queues = new HashMap<>();
            DiscovQueue.getInstance().getLogger().info("Didn't find any existing queues.");
        }
    }

    public void save() {
        List<QueueStorable> queueStorableList = new ArrayList<>();
        for (String name : queues.keySet()) {
            Queue queue = queues.get(name);
            if (queue.getSigns().size() != 0 && queue.getLocation() != null) {
                QueueStorable queueStorable = new QueueStorable(queues.get(name));
                queueStorableList.add(queueStorable);
            }
        }
        DataUtil.saveObjectToPath(queueStorableList, path);
        DiscovQueue.getInstance().getLogger().info(queueStorableList.size() + " queues have been saved.");
    }
}
