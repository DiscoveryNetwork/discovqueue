package nl.parrotlync.queue.model;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.io.Serializable;
import java.util.Objects;

public class QueueSign implements Serializable {
    private Integer xLocation;
    private Integer yLocation;
    private Integer zLocation;
    private String world;

    public QueueSign(Sign sign) {
        Location location = sign.getLocation();
        this.xLocation = (int) location.getX();
        this.yLocation = (int) location.getY();
        this.zLocation = (int) location.getZ();
        this.world = Objects.requireNonNull(location.getWorld()).getName();
    }

    public Integer getX() {
        return xLocation;
    }

    public Integer getY() {
        return yLocation;
    }

    public Integer getZ() {
        return zLocation;
    }

    public String getWorld() {
        return world;
    }
}
