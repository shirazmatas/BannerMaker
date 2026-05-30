package club.kid7.bannermaker.configuration;

import club.kid7.bannermaker.BannerMaker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigManager {
    private static final Map<String, FileConfiguration> configs = new HashMap<>();

    /**
     * Get the file name with the ".yml" extension
     * and uniformly convert path separators to "/"
     * @param fileName File name
     * @return File name with extension
     */
    private static String getFileName(String fileName) {
        fileName = fileName.replace('\\', '/');
        if (!fileName.endsWith(".yml")) {
            fileName += ".yml";
        }
        return fileName;
    }

    /**
     * ConfigManager Check if ConfigManager has loaded the file
     *
     * @param fileName The file to check
     * @return Returns true if the file is loaded, otherwise false
     */
    public static boolean isFileLoaded(String fileName) {
        fileName = getFileName(fileName);
        return configs.containsKey(fileName);
    }

    /**
     * Load file settings into memory
     *
     * @param fileName The file to load
     */
    public static void load(String fileName) {
        fileName = getFileName(fileName);
        BannerMaker plugin = BannerMaker.getInstance();

        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            //  Ensure parent directory exists
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try {
                // Try to save resource from JAR
                plugin.saveResource(fileName, false);
            } catch (Exception e) {
                // If the resource is not in the JAR, create a blank file
                try {
                    file.createNewFile();
                } catch (Exception ex) {
                    plugin.getLogger().warning("Could not create config file: " + fileName + " (" + ex.getMessage() + ")");
                }
            }
        }
        if (!isFileLoaded(fileName)) {
            configs.put(fileName, YamlConfiguration.loadConfiguration(file));
        }
    }

    /**
     * Get the FileConfiguration of the specified file, loading it if it hasn't been loaded yet.
     *
     * @param fileName The file to read data from
     * @return File configuration (FileConfiguration)
     */
    public static FileConfiguration get(String fileName) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            load(fileName);
        }
        return configs.get(fileName);
    }

    /**
     * Set data at the specified path. If the path already exists, it will be overwritten.
     *
     * @param fileName The file to update
     * @param path The path to set
     * @param value  The value to set
     */
    public static void set(String fileName, String path, Object value) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            BannerMaker.getInstance().getLogger().warning("Config not loaded: " + fileName);
            return;
        }
        configs.get(fileName).set(path, value);
    }

    /**
     * Remove a path from FileConfiguration.
     *
     * @param fileName The file to update
     * @param path     The path to remove
     */
    public static void remove(String fileName, String path) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            BannerMaker.getInstance().getLogger().warning("Config not loaded: " + fileName);
            return;
        }
        configs.get(fileName).set(path, null);
    }

    /**
     * Check if the file contains the specified path.
     *
     * @param fileName The file to check
     * @param path     The path to check
     * @return Returns true if the path exists, otherwise false.
     */
    public static boolean contains(String fileName, String path) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            BannerMaker.getInstance().getLogger().warning("Config not loaded: " + fileName);
            return false;
        }
        return configs.get(fileName).contains(path);
    }

    /**
     * Reload the configuration file from the plugin folder.
     *
     * @param fileName The file to reload
     */
    public static void reload(String fileName) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            BannerMaker.getInstance().getLogger().warning("Config not loaded: " + fileName);
            return;
        }
        BannerMaker plugin = BannerMaker.getInstance();
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            // If the file does not exist, try to reload it to create it (if needed)
            load(fileName);
            return;
        }
        try {
            configs.get(fileName).load(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Could not reload config: " + fileName, e);
        }
    }

    /**
     * Save settings to file.
     *
     * @param fileName The file to save
     */
    public static void save(String fileName) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            BannerMaker.getInstance().getLogger().warning("Config not loaded: " + fileName);
            return;
        }
        BannerMaker plugin = BannerMaker.getInstance();
        File file = new File(plugin.getDataFolder(), fileName);
        try {
            configs.get(fileName).save(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config: " + fileName, e);
        }
    }

    /**
     * Reload all configuration files in memory.
     */
    public static void reloadAll() {
        for (String fileName : configs.keySet()) {
            reload(fileName);
        }
    }

    /**
     * Clear all loaded configuration files (mainly for unit tests).
     */
    public static void reset() {
        configs.clear();
    }
}
