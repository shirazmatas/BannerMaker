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
     * 取得帶有 ".yml" 副檔名的檔案名稱
     * AI Translated: Get the file name with the ".yml" extension
     * 並統一將路徑分隔符號轉換為 "/"
     * AI Translated: and uniformly convert path separators to "/"
     *
     * @param fileName 檔案名稱
     * AI Translated: File name
     * @return 帶有副檔名的檔案名稱
     * AI Translated: File name with extension
     */
    private static String getFileName(String fileName) {
        fileName = fileName.replace('\\', '/');
        if (!fileName.endsWith(".yml")) {
            fileName += ".yml";
        }
        return fileName;
    }

    /**
     * 檢查 ConfigManager 是否已載入該檔案
     * AI Translated: Check if ConfigManager has loaded the file
     *
     * @param fileName 要檢查的檔案
     * AI Translated: The file to check
     * @return 如果檔案已載入則返回 true，否則返回 false
     * AI Translated: Returns true if the file is loaded, otherwise false
     */
    public static boolean isFileLoaded(String fileName) {
        fileName = getFileName(fileName);
        return configs.containsKey(fileName);
    }

    /**
     * 將檔案設定載入到記憶體中
     * AI Translated: Load file settings into memory
     *
     * @param fileName 要載入的檔案
     * AI Translated: The file to load
     */
    public static void load(String fileName) {
        fileName = getFileName(fileName);
        BannerMaker plugin = BannerMaker.getInstance();
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                plugin.saveResource(fileName, false);
            } catch (Exception e) {
                // 忽略錯誤，可能是因為 jar 中沒有對應的資源檔（例如玩家資料）
                // AI Translated: Ignore errors, possibly because there is no corresponding resource file in the jar (e.g., player data)
                // 但如果是語言檔或設定檔，這可能是一個問題，所以在 debug 模式下或是測試時這很有用
                // AI Translated: But if it's a language file or configuration file, this might be a problem, so it's useful in debug mode or during testing
                plugin.getLogger().warning("Could not save resource: " + fileName + " (" + e.getMessage() + ")");
            }
        }
        if (!isFileLoaded(fileName)) {
            configs.put(fileName, YamlConfiguration.loadConfiguration(file));
        }
    }

    /**
     * 取得指定檔案的 FileConfiguration，如果尚未載入則載入它。
     * AI Translated: Get the FileConfiguration of the specified file, loading it if it hasn't been loaded yet.
     *
     * @param fileName 要讀取資料的檔案
     * AI Translated: The file to read data from
     * @return 檔案設定 (FileConfiguration)
     * AI Translated: File configuration (FileConfiguration)
     */
    public static FileConfiguration get(String fileName) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            load(fileName);
        }
        return configs.get(fileName);
    }

    /**
     * 在指定路徑設定資料。如果路徑已存在，它將被覆蓋。
     * AI Translated: Set data at the specified path. If the path already exists, it will be overwritten.
     *
     * @param fileName 要更新的檔案
     * AI Translated: The file to update
     * @param path     要設定的路徑
     * AI Translated: The path to set
     * @param value    要設定的值
     * AI Translated: The value to set
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
     * 從 FileConfiguration 中移除一個路徑。
     * AI Translated: Remove a path from FileConfiguration.
     *
     * @param fileName 要更新的檔案
     * AI Translated: The file to update
     * @param path     要移除的路徑
     * AI Translated: The path to remove
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
     * 檢查檔案是否包含指定路徑。
     * AI Translated: Check if the file contains the specified path.
     *
     * @param fileName 要檢查的檔案
     * AI Translated: The file to check
     * @param path     要檢查的路徑
     * AI Translated: The path to check
     * @return 如果路徑存在則返回 true，否則返回 false。
     * AI Translated: Returns true if the path exists, otherwise false.
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
     * 從插件資料夾重新載入設定檔。
     * AI Translated: Reload the configuration file from the plugin folder.
     *
     * @param fileName 要重新載入的檔案
     * AI Translated: The file to reload
     */
    public static void reload(String fileName) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            BannerMaker.getInstance().getLogger().warning("Config not loaded: " + fileName);
            return;
        }
        BannerMaker plugin = BannerMaker.getInstance();
        File file = new File(plugin.getDataFolder(), fileName);
        try {
            configs.get(fileName).load(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Could not reload config: " + fileName, e);
        }
    }

    /**
     * 儲存設定到檔案。
     * AI Translated: Save settings to file.
     *
     * @param fileName 要儲存的檔案
     * AI Translated: The file to save
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
     * 重新載入所有記憶體中的設定檔。
     * AI Translated: Reload all configuration files in memory.
     */
    public static void reloadAll() {
        for (String fileName : configs.keySet()) {
            reload(fileName);
        }
    }

    /**
     * 清除所有已載入的設定檔（主要用於單元測試）。
     * AI Translated: Clear all loaded configuration files (mainly for unit tests).
     */
    public static void reset() {
        configs.clear();
    }
}
