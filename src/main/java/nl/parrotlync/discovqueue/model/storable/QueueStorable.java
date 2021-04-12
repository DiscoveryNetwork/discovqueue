package nl.parrotlync.discovqueue.model.storable;

import nl.parrotlync.discovqueue.model.Queue;
import nl.parrotlync.discovqueue.model.QueueType;
import nl.parrotlync.discovqueue.model.RideQueue;
import nl.parrotlync.discovqueue.model.SignType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import java.io.Serializable;
import java.util.HashMap;

public class QueueStorable implements Serializable {
    private final String name;
    private final int size;
    private final int interval;
    private final QueueType type;
    private final LocationStorable location;
    private final HashMap<LocationStorable, SignType> signs = new HashMap<>();

    public QueueStorable(Queue queue) {
        this.name = queue.getName();
        this.size = queue.getSize();
        this.interval = queue.getInterval();
        this.location = new LocationStorable(queue.getLocation());
        for (Sign sign : queue.getSigns().keySet()) {
            signs.put(new LocationStorable(sign.getLocation()), queue.getSigns().get(sign));
        }
        if (queue instanceof RideQueue) { type = QueueType.RIDE; }
        else { type = null; }
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getInterval() {
        return interval;
    }

    public Location getLocation() {
        return location.getLocation();
    }

    public QueueType getType() {
        return type;
    }

    public HashMap<Sign, SignType> getSigns() {
        HashMap<Sign, SignType> signs = new HashMap<>();
        for (LocationStorable loc : this.signs.keySet()) {
            World world = loc.getLocation().getWorld();
            assert world != null;
            if (world.getBlockAt(loc.getLocation()).getState() instanceof Sign) {
                signs.put((Sign) world.getBlockAt(loc.getLocation()).getState(), this.signs.get(loc));
            }
        }
        return signs;
    }
}
