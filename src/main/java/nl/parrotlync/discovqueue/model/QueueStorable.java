package nl.parrotlync.discovqueue.model;

import org.bukkit.block.Sign;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueueStorable implements Serializable {
    private String name;
    private QueueLocation location;
    private List<QueueSign> signs = new ArrayList<>();
    private Integer batchSize;
    private Integer interval;

    public QueueStorable(RideQueue queue) {
        this.name = queue.getName();
        this.location = new QueueLocation(queue.getLocation());
        this.batchSize = queue.getBatchSize();
        this.interval = queue.getInterval();
        for (Sign sign : queue.getSigns()) {
            signs.add(new QueueSign(sign, queue.getSignType(sign)));
        }
    }

    public String getName() {
        return name;
    }

    public QueueLocation getLocation() {
        return location;
    }

    public List<QueueSign> getSigns() {
        return signs;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public Integer getInterval() {
        return interval;
    }
}
