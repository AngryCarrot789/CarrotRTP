package carrot.rtp.config;

import carrot.rtp.CarrotRTP;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class ConfigManager {
    public static FileConfiguration config;
    public static File configFile;

    private ConfigManager(){

    }

    public static void Initialise() {
        try {
            CarrotRTP.logger.info("Loading config...");
            configFile = new File(CarrotRTP.instance.getDataFolder(), "config.yml");

            if (!configFile.exists()) {
                CarrotRTP.logger.info("Config file or directory doesn't exist. Creating...");
                configFile.getParentFile().mkdirs();
                CarrotRTP.logger.info("Config directory created. Creating config file...");
                CarrotRTP.instance.saveResource("config.yml", false);
                CarrotRTP.logger.info("Successfully created config file");
            }

            config = new YamlConfiguration();

            try {
                CarrotRTP.logger.info("Loading config file...");
                config.load(configFile);
            }
            catch (IOException io) {
                CarrotRTP.logger.info("IOException when loading config");
                io.printStackTrace();
            }
            catch (InvalidConfigurationException e) {
                CarrotRTP.logger.info("InvalidConfigurationException when loading config");
                e.printStackTrace();
            }
        }
        catch (Exception e){
            CarrotRTP.logger.info("Failed to setup config file");
            e.printStackTrace();
        }
    }

    public static void ReloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        CarrotRTP.logger.info("Successfully reloaded config");
    }
}
