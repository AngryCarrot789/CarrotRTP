package carrot.rtp;

import carrot.rtp.chunks.AreaSafety;
import carrot.rtp.chunks.RandomChunkGenerator;
import carrot.rtp.chunks.WorldSquare;
import carrot.rtp.config.ConfigManager;
import carrot.rtp.cooldown.CooldownMap;
import carrot.rtp.extras.CoolEffects;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CarrotRTP extends JavaPlugin {
    public static CarrotRTP instance;
    public static Logger logger;

    public static List<String> AllowedWorlds = new ArrayList<String>();
    public static int MaxNumberOfBiomeSearches = 100;
    public static String PluginVersion = "1.0.0";
    public static int CooldownInterval = 10;
    public static boolean AllowBiomeRTP = true;
    public static boolean SummonLightning = true;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        ConfigManager.Initialise();
        ReloadFromConfig();

        CooldownMap.Initialise();

        getLogger().info("RandomTeleporter enabled :)");
    }

    @Override
    public void onDisable() {
        getLogger().info("RandomTeleporter disabled :)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            String cmd = command.getName();
            ExecuteCommand(sender, cmd, args);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public void ExecuteCommand(CommandSender sender, String command, String[] args){
        if (IsRTPCommand(command)) {
            ParsedValue<String> argument = ArgumentsParser.ParseString(args, 0);
            if (!argument.failed) {
                if (argument.value.equalsIgnoreCase("version") || argument.value.equalsIgnoreCase("help")) {
                    sender.sendMessage(ChatColor.GREEN + "Version " + PluginVersion);
                    sender.sendMessage(ChatColor.GOLD + "Do '/rtp' to teleport somewhere randomly");
                    sender.sendMessage(ChatColor.GOLD + "You can do '/biomes' to get a list of biomes");
                    sender.sendMessage(ChatColor.GOLD + "and then you can do '/rtp [biome]' (without the []'s)");
                    return;
                }
                if (argument.value.equalsIgnoreCase("reload")) {
                    try {
                        if (sender.isOp() || sender instanceof ConsoleCommandSender) {
                            sender.sendMessage(ChatColor.GOLD + "Reloading config...");
                            ConfigManager.ReloadConfig();
                            ReloadFromConfig();
                            sender.sendMessage(ChatColor.GOLD + "Success!");
                        }
                        else {
                            sender.sendMessage(ChatColor.GOLD + "????? you cant do that");
                        }
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.GOLD + "Failed to reload config");
                        e.printStackTrace();
                    }
                    return;
                }
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("You're not a player");
                return;
            }

            Player player = (Player) sender;
            if (!CooldownMap.CanRTP(player)) {
                if (!player.isOp()) {
                    player.sendMessage(ChatColor.GOLD + "Cooling down... just wait a few seconds");
                    return;
                }
            }

            ParsedValue<WorldSquare> parsedWorld = WorldSquare.ParseFromConfig(ConfigManager.config, player.getWorld().getName());
            if (parsedWorld.failed) {
                sender.sendMessage("That world does not allow random teleporting");
                return;
            }

            if (argument.failed) {
                if (!RandomTPPlayer(parsedWorld.value, player))
                    return;
            }
            else {
                if (!AllowBiomeRTP) {
                    sender.sendMessage(ChatColor.GOLD + "RTP to specific biomes is disabled");
                    return;
                }

                ParsedValue<Biome> biome = ParseBiome(argument.value);
                if (biome.failed) {
                    sender.sendMessage("That biome isn't supported. Doing RTP to plains");
                }
                if (!RandomTPPlayerBiome(parsedWorld.value, biome.value, player))
                    return;

            }
            CooldownMap.Cooldowns.put(player, CooldownInterval);
            if (SummonLightning) {
                CoolEffects.SpawnLightning(player);
            }

            return;
        }

        if (command.equalsIgnoreCase("biomes")) {
            sender.sendMessage(ChatColor.GREEN + "The possible biomes: (in order of likelyhood of RTPing to)");
            sender.sendMessage(ChatColor.GOLD + "ocean" + ChatColor.DARK_AQUA + " (water...)");
            sender.sendMessage(ChatColor.GOLD + "plains" + ChatColor.GREEN + " (open and green)");
            sender.sendMessage(ChatColor.GOLD + "desert" + ChatColor.YELLOW + " (sand)");
            sender.sendMessage(ChatColor.GOLD + "jungle" + ChatColor.DARK_GREEN + " (big trees and vines)");
            sender.sendMessage(ChatColor.GOLD + "taiga" + ChatColor.WHITE + ChatColor.BOLD + " (aka snow biome)");
            sender.sendMessage(ChatColor.GOLD + "forest" + ChatColor.GREEN + " (aka dense oak trees)");
            sender.sendMessage(ChatColor.GOLD + "swampland" + ChatColor.DARK_GREEN + " (shrek)");
            sender.sendMessage(ChatColor.GOLD + "extreme_hills" + ChatColor.GREEN + " (aka plains but lots of hills)");
            sender.sendMessage(ChatColor.GOLD + "taiga_hills" + ChatColor.WHITE + ChatColor.BOLD + " (snowy hills)");
            sender.sendMessage(ChatColor.GOLD + "mushroom_island" + ChatColor.LIGHT_PURPLE + " (mooshrooms!!!)");
        }
    }

    public boolean RandomTPPlayer(WorldSquare area, Player player) {
        Chunk chunk = player.getLocation().getChunk();
        Location start = player.getLocation();
        start.setX(start.getX() + 0.5d);
        start.setZ(start.getZ() + 0.5d);
        for(int i = 0; i < 20; i++) {
            chunk = RandomChunkGenerator.FindRandomChunk(player.getWorld(), area.MinX, area.MinZ, area.MaxX, area.MaxZ);
            chunk.load();
            Biome worldBiome = player.getWorld().getBiome(chunk.getX() << 4, chunk.getZ() << 4);
            if (worldBiome == Biome.OCEAN) {
                continue;
            }
            start.setX((chunk.getX() << 4) + 0.5d);
            start.setZ((chunk.getZ() << 4) + 0.5d);
            Location safe = AreaSafety.GetSafeYLocation(start, area.MinY, area.MaxY);
            int below = AreaSafety.GetBlocksBelow(safe);
            if (below < 6) {
                start.setY(safe.getY());
                player.teleport(start);
                return true;
            }
        }
        player.sendMessage(ChatColor.GOLD + "Failed to find a safe and non-ocean location to RTP to. Try again (there's no cooldown)");
        return false;
    }

    public boolean RandomTPPlayerBiome(WorldSquare area, Biome biome, Player player){
        Chunk chunk = player.getLocation().getChunk();
        Location start = player.getLocation();
        start.setX(start.getX() + 0.5d);
        start.setZ(start.getZ() + 0.5d);
        for(int i = 0; i < 20; i++) {
            chunk = RandomChunkGenerator.FindRandomChunkInBiome(player.getWorld(), biome, area.MinX, area.MinZ, area.MaxX, area.MaxZ);
            chunk.load();
            start.setX((chunk.getX() << 4) + 0.5d);
            start.setZ((chunk.getZ() << 4) + 0.5d);
            Location safe = AreaSafety.GetSafeYLocation(start, area.MinY, area.MaxY);
            int below = AreaSafety.GetBlocksBelow(safe);
            if (below < 6) {
                Biome worldBiome = player.getWorld().getBiome(chunk.getX() << 4, chunk.getZ() << 4);
                if (biome != Biome.OCEAN && worldBiome == Biome.OCEAN){
                    continue;
                }
                if (worldBiome != biome) {
                    player.sendMessage(ChatColor.GOLD + "Couldn't find a location in that biome");
                }
                start.setY(safe.getY());
                player.teleport(start);
                return true;
            }
        }
        player.sendMessage(ChatColor.GOLD + "Failed to find a safe RTP. Try again (there's no cooldown)");
        return false;
    }

    public static ParsedValue<Biome> ParseBiome(String biome){
        if (biome.equalsIgnoreCase("PLAINS"))
            return new ParsedValue<Biome>(Biome.PLAINS, false);
        if (biome.equalsIgnoreCase("EXTREME_HILLS"))
            return new ParsedValue<Biome>(Biome.EXTREME_HILLS, false);
        if (biome.equalsIgnoreCase("DESERT"))
            return new ParsedValue<Biome>(Biome.DESERT, false);
        if (biome.equalsIgnoreCase("JUNGLE"))
            return new ParsedValue<Biome>(Biome.JUNGLE, false);
        if (biome.equalsIgnoreCase("SWAMPLAND"))
            return new ParsedValue<Biome>(Biome.SWAMPLAND, false);
        if (biome.equalsIgnoreCase("FOREST"))
            return new ParsedValue<Biome>(Biome.FOREST, false);
        if (biome.equalsIgnoreCase("TAIGA"))
            return new ParsedValue<Biome>(Biome.TAIGA, false);
        if (biome.equalsIgnoreCase("TAIGA_HILLS"))
            return new ParsedValue<Biome>(Biome.TAIGA_HILLS, false);
        if (biome.equalsIgnoreCase("OCEAN"))
            return new ParsedValue<Biome>(Biome.OCEAN, false);
        if (biome.equalsIgnoreCase("MUSHROOM_ISLAND"))
            return new ParsedValue<Biome>(Biome.MUSHROOM_ISLAND, false);

        return new ParsedValue<Biome>(Biome.PLAINS, true);
    }

    public static boolean IsRTPCommand(String command){
        if (command.equalsIgnoreCase("randomtp"))
            return true;
        if (command.equalsIgnoreCase("rtp"))
            return true;
        if (command.equalsIgnoreCase("wild"))
            return true;
        if (command.equalsIgnoreCase("randomteleport"))
            return true;
        return false;
    }

    public void ReloadFromConfig(){
        PluginVersion = ConfigManager.config.getString("version");
        AllowedWorlds = ConfigManager.config.getStringList("allowedWorlds");
        MaxNumberOfBiomeSearches = ConfigManager.config.getInt("maxNumberOfBiomeSearches");
        CooldownInterval = ConfigManager.config.getInt("cooldownInterval");
        AllowBiomeRTP = ConfigManager.config.getBoolean("allowBiomeRTP");
        SummonLightning = ConfigManager.config.getBoolean("summonLightning");
    }
}
