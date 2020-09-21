package nl.parrotlync.discovqueue.model.storable;

import nl.parrotlync.discovqueue.model.GenericQueue;
import nl.parrotlync.discovqueue.model.Queue;
import nl.parrotlync.discovqueue.model.RideQueue;
import org.bukkit.block.Sign;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueueStorable implements Serializable {
    private String name;
    private QueueLocationStorable location;
    private QueueType type;
    private Integer batchSize;
    private Integer interval;
    private List<QueueSignStorable> signs = new ArrayList<>();

    public QueueStorable(Queue queue) {
        this.name = queue.getName();
        this.location = new QueueLocationStorable(queue.getLocation());
        this.batchSize = queue.getBatchSize();
        this.interval = queue.getInterval();
        for (Sign sign : queue.getSigns()) {
            signs.add(new QueueSignStorable(sign, queue.getSignType(sign)));
        }

        if (queue instanceof RideQueue) { this.type = QueueType.RIDE; }
        else if (queue instanceof GenericQueue) { this.type = QueueType.GENERIC; }
    }

    public String getName() {
        return name;
    }

    public QueueLocationStorable getLocation() {
        return location;
    }

    public List<QueueSignStorable> getSigns() {
        return signs;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public Integer getInterval() {
        return interval;
    }

    public QueueType getType() { return type; }
}
