package nl.parrotlync.queue.manager;

import nl.parrotlync.queue.model.QueueLocation;
import nl.parrotlync.queue.model.QueueSign;
import nl.parrotlync.queue.model.QueueStorable;
import nl.parrotlync.queue.model.RideQueue;
import nl.parrotlync.queue.util.DataUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueueManager {
    private HashMap<String, RideQueue> queues = new HashMap<>();
    private String path = "plugins/Queue/queues.data";

    public RideQueue getQueue(String name) {
        return queues.get(name.toLowerCase());
    }

    public Boolean createQueue(String name) {
        if (queues.get(name.toLowerCase()) == null) {
            queues.put(name.toLowerCase(), new RideQueue(name));
            return true;
        }
        return false;
    }

    public Boolean removeQueue(String name) {
        if (queues.get(name.toLowerCase()) != null) {
            queues.remove(name);
            return true;
        }
        return false;
    }

    public List<RideQueue> getQueues() {
        List<RideQueue> queueList = new ArrayList<>();
        for (String name : queues.keySet()) {
            queueList.add(queues.get(name));
        }
        return queueList;
    }

    public void load() {
        List<QueueStorable> storedQueues = DataUtil.loadObjectFromPath(path);
        if (storedQueues != null) {
            for (QueueStorable storedQueue : storedQueues) {
                RideQueue queue = new RideQueue(storedQueue.getName());
                queue.setBatchSize(storedQueue.getBatchSize());
                queue.setInterval(storedQueue.getInterval());
                // Teleport location
                QueueLocation location = storedQueue.getLocation();
                World world = Bukkit.getWorld(location.getWorld());
                queue.setLocation(new Location(world, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()));
                // Sign location
                QueueSign sign = storedQueue.getSign();
                World sWorld = Bukkit.getWorld(sign.getWorld());
                queue.setSign((Sign) sWorld.getBlockAt(sign.getX(), sign.getY(), sign.getZ()).getState());
                queue.updateSign();
                queues.put(storedQueue.getName().toLowerCase(), queue);
            }
        } else {
            queues = new HashMap<>();
        }
    }

    public void save() {
        List<QueueStorable> queueStorableList = new ArrayList<>();
        for (String name : queues.keySet()) {
            RideQueue queue = queues.get(name);
            if (queue.getSign() != null && queue.getLocation() != null) {
                QueueStorable queueStorable = new QueueStorable(queues.get(name));
                queueStorableList.add(queueStorable);
            }
        }
        DataUtil.saveObjectToPath(queueStorableList, path);
    }
}
