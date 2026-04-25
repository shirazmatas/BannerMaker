package club.kid7.bannermaker.configuration;

import club.kid7.bannermaker.BannerMaker;
import com.google.common.collect.Maps;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

import static club.kid7.bannermaker.configuration.Language.tl;
import static club.kid7.bannermaker.util.TagUtil.tag;

public class DefaultConfig {
    private final BannerMaker bm;
    //須要檢查設定項目的設定
    // AI Translated: Settings that need to be checked for configuration items
    private final String[] defaultConfigs = {"config", "price"};
    private final HashMap<String, FileConfiguration> defaultConfigsResource = Maps.newHashMap();

    public DefaultConfig(BannerMaker bm) {
        this.bm = bm;
    }

    public void checkConfig() {
        for (String configName : defaultConfigs) {
            //當前設定檔
            // AI Translated: Current configuration file
            String configFileName = configName + ".yml";
            FileConfiguration config = ConfigManager.get(configFileName);
            if (config == null) {
                continue;
            }
            //載入預設設定檔（但不儲存於資料夾）
            // AI Translated: Load default configuration file (but not saved in folder)
            try {
                Reader defaultLanguageInputStreamReader = new InputStreamReader(Objects.requireNonNull(bm.getResource(configFileName.replace('\\', '/'))), StandardCharsets.UTF_8);
                defaultConfigsResource.put(configName, YamlConfiguration.loadConfiguration(defaultLanguageInputStreamReader));
            } catch (Exception ignored) {
            }
            FileConfiguration defaultConfigResource = defaultConfigsResource.get(configName);
            //根據預設語言資源檔檢查
            // AI Translated: Check according to the default language resource file
            int newSettingCount = 0;
            for (String key : defaultConfigResource.getKeys(true)) {
                //不直接檢查整個段落
                // AI Translated: Do not directly check the entire section
                if (defaultConfigResource.isConfigurationSection(key)) {
                    continue;
                }
                //若key已存在也不檢查
                // AI Translated: If key already exists, do not check either
                if (config.contains(key)) {
                    continue;
                }
                //若未包含該key，將預設值填入設定檔
                // AI Translated: If the key is not included, fill the default value into the configuration file
                config.set(key, defaultConfigResource.get(key));

                newSettingCount++;
            }
            if (newSettingCount > 0) {
                ConfigManager.save(configFileName);
                bm.getMessageService().send(bm.getServer().getConsoleSender(), tl("config.add-setting", tag("count", newSettingCount)));
            }
        }
    }
}
