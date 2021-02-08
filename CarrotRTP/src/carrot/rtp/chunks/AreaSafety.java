package carrot.rtp.chunks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class AreaSafety {
    public static Location GetSafeYLocation(Location start, int startHeight, int maxHeight) {
        World world = start.getWorld();
        double x = start.getX();
        double z = start.getZ();
        start.setY(startHeight);
        Location newLocation = new Location(world, x, start.getY(), z);
        try {
            for (double y = newLocation.getY(); y < maxHeight; y += 2) {
                newLocation.setY(y);
                if (CanPlayerFit(newLocation)) {
                    if (!IsBelowLava(newLocation, 10)) {
                        newLocation.setY(newLocation.getY());
                        return newLocation;
                    }
                }
            }

            newLocation.setY(258);
            return newLocation;
        } catch (Exception e) {
            return start;
        }
    }

    public static boolean CanPlayerFit(Location location) {
        if (location == null) {
            return false;
        }

        Block block = location.getBlock();
        if (IsBlockAir(block)) {
            Location above = new Location(block.getWorld(), location.getX(), location.getY() + 1, location.getZ());
            Location below = new Location(block.getWorld(), location.getX(), location.getY() + 1, location.getZ());
            return IsBlockAir(above.getBlock()) && IsBlockAir(below.getBlock());
        }

        return false;
    }

    public static boolean IsBlockAir(Block block) {
        if (block.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean IsBelowLava(Location location, int checkBelow) {
        double y = location.getY();
        Location copy = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        for (double i = Math.max(y - checkBelow, 2); i < y; i++) {
            copy.setY(i);
            if (copy.getBlock().getType() == Material.LAVA) {
                return true;
            }
        }
        return false;
    }

    public static int GetBlocksBelow(Location location) {
        int count = 0;
        for (double i = location.getY(); i > 0; i--) {
            location.setY(i);
            if (!IsBlockAir(location.getBlock())) {
                count++;
            }
            else {
                return count;
            }
        }
        return count;
    }
}
