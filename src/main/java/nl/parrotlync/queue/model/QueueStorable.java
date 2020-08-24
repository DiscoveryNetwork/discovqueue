package nl.parrotlync.queue.model;

import java.io.Serializable;

public class QueueStorable implements Serializable {
    private String name;
    private QueueLocation location;
    private QueueSign sign;
    private Integer batchSize;
    private Integer interval;

    public QueueStorable(RideQueue queue) {
        this.name = queue.getName();
        this.location = new QueueLocation(queue.getLocation());
        this.sign = new QueueSign(queue.getSign());
        this.batchSize = queue.getBatchSize();
        this.interval = queue.getInterval();
    }

    public String getName() {
        return name;
    }

    public QueueLocation getLocation() {
        return location;
    }

    public QueueSign getSign() {
        return sign;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public Integer getInterval() {
        return interval;
    }
}
