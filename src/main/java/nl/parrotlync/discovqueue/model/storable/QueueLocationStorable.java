package nl.parrotlync.discovqueue.model.storable;

import org.bukkit.Location;

import java.io.Serializable;
import java.util.Objects;

public class QueueLocationStorable implements Serializable {
    private double xLocation;
    private double yLocation;
    private double zLocation;
    private float yaw;
    private float pitch;
    private String world;

    public QueueLocationStorable(Location location) {
        this.xLocation = location.getX();
        this.yLocation = location.getY();
        this.zLocation = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.world = Objects.requireNonNull(location.getWorld()).getName();
    }

    public double getX() {
        return xLocation;
    }

    public double getY() {
        return yLocation;
    }

    public double getZ() {
        return zLocation;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public String getWorld() {
        return world;
    }
}
