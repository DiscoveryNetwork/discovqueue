package nl.parrotlync.discovqueue.model.storable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;
import java.util.Objects;

public class LocationStorable implements Serializable {
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final String world;

    public LocationStorable(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.world = Objects.requireNonNull(location.getWorld()).getName();
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(this.world);
        return new Location(world, x, y, z, yaw, pitch);
    }
}
