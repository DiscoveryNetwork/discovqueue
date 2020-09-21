package nl.parrotlync.discovqueue.model.storable;

import nl.parrotlync.discovqueue.model.SignType;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.io.Serializable;
import java.util.Objects;

public class QueueSignStorable implements Serializable {
    private Integer xLocation;
    private Integer yLocation;
    private Integer zLocation;
    private SignType type;
    private String world;

    public QueueSignStorable(Sign sign, SignType type) {
        Location location = sign.getLocation();
        this.xLocation = (int) location.getX();
        this.yLocation = (int) location.getY();
        this.zLocation = (int) location.getZ();
        this.type = type;
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

    public SignType getType() { return type; }
}
