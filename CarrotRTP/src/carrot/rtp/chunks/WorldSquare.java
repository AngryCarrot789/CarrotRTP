package carrot.rtp.chunks;

import carrot.rtp.ParsedValue;
import org.bukkit.configuration.file.FileConfiguration;

public class WorldSquare {
    public int MinX;
    public int MinZ;
    public int MaxX;
    public int MaxZ;
    public int MinY;
    public int MaxY;

    private WorldSquare(int minX, int minZ, int maxX, int maxZ, int minY, int maxY) {
        MinX = minX;
        MinZ = minZ;
        MaxX = maxX;
        MaxZ = maxZ;
        MinY = minY;
        MaxY = maxY;
    }

    public static ParsedValue<WorldSquare> ParseFromConfig(FileConfiguration config, String worldName) {
        try {
            if (!config.contains(worldName)){
                return new ParsedValue<WorldSquare>(null, true);
            }
            int minX = config.getInt(worldName + ".minX");
            int minZ = config.getInt(worldName + ".minZ");
            int maxX = config.getInt(worldName + ".maxX");
            int maxZ = config.getInt(worldName + ".maxZ");
            int minY = config.getInt(worldName + ".minY");
            int maxY = config.getInt(worldName + ".maxY");
            return new ParsedValue<WorldSquare>(new WorldSquare(minX, minZ,maxX, maxZ, minY, maxY), false);
        }
        catch (Exception e){
            return new ParsedValue<WorldSquare>(null, true);
        }
    }
}
