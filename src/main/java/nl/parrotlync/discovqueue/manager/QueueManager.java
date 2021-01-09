package nl.parrotlync.discovqueue.manager;

import nl.parrotlync.discovqueue.DiscovQueue;
import nl.parrotlync.discovqueue.model.Queue;
import nl.parrotlync.discovqueue.model.QueueType;
import nl.parrotlync.discovqueue.model.RideQueue;
import nl.parrotlync.discovqueue.model.storable.QueueStorable;
import nl.parrotlync.discovqueue.util.DataUtil;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueueManager {
    private final HashMap<String, Queue> queues = new HashMap<>();
    private final String path = "plugins/DiscovQueue/queues.data";

    public void createQueue(String name, int size, int interval, QueueType type) {
        if (queues.get(name.toLowerCase()) == null) {
            if (type == QueueType.RIDE) {
                queues.put(name.toLowerCase(), new RideQueue(name, size, interval));
            }
        }
    }

    public boolean hasQueue(String name) {
        return queues.containsKey(name.toLowerCase());
    }

    public void removeQueue(String name) {
        Queue queue = queues.get(name.toLowerCase());
        queue.stopTimer();
        queues.remove(name.toLowerCase());
    }

    public Queue getQueue(String name) {
        return queues.get(name.toLowerCase());
    }

    public List<Queue> getQueues() {
        return new ArrayList<>(queues.values());
    }

    public void load() {
        List<QueueStorable> storableList = DataUtil.loadObjectFromPath(path);
        if (storableList != null) {
            for (QueueStorable storable : storableList) {
                Queue queue;
                if (storable.getType() == QueueType.RIDE) {
                    queue = new RideQueue(storable.getName(), storable.getSize(), storable.getInterval());
                } else {
                    continue;
                }

                queue.setLocation(storable.getLocation());
                for (Sign sign : storable.getSigns().keySet()) {
                    queue.addSign(sign, storable.getSigns().get(sign));
                }
                queue.updateSigns();
                queues.put(queue.getName().toLowerCase(), queue);
            }
            DiscovQueue.getInstance().getLogger().info("Loaded " + queues.size() + " queue(s) from data storage.");
        }
    }

    public void save() {
        List<QueueStorable> storableList = new ArrayList<>();
        for (String name : queues.keySet()) {
            Queue queue = queues.get(name);
            if (queue.getLocation() != null && queue.getSigns().size() != 0) {
                storableList.add(new QueueStorable(queue));
            }
        }
        DataUtil.saveObjectToPath(storableList, path);
    }
}
