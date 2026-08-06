package club.kid7.bannermaker.service;

import club.kid7.bannermaker.configuration.ConfigManager;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.PersistentDataUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BannerRepository {

    //儲存旗幟
    // AI Translated: Save banner
    public boolean saveBanner(Player player, ItemStack banner) {
        //只處理旗幟
        // AI Translated: Only handle banners
        if (!BannerUtil.isBanner(banner)) {
            return false;
        }
        //設定檔
        // AI Translated: Configuration file
        String fileName = getFileName(player);
        FileConfiguration config = ConfigManager.get(fileName);
        //索引值（時間戳記，不會重複）
        // AI Translated: Index value (timestamp, won't repeat)
        String key = String.valueOf(System.currentTimeMillis());
        //旗幟資訊
        // AI Translated: Banner info
        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());
        //儲存
        // AI Translated: Save
        config.set(key + ".color", Objects.requireNonNull(DyeColorRegistry.getDyeColor(banner.getType())).toString());
        List<String> patternList = new ArrayList<>();

        final Registry<PatternType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);
        for (Pattern pattern : bm.getPatterns()) {
            patternList.add(registry.getKeyOrThrow(pattern.getPattern()).asMinimalString() + "," + pattern.getColor());
        }
        if (!patternList.isEmpty()) {
            config.set(key + ".patterns", patternList);
        }
        ConfigManager.save(fileName);
        return true;
    }

    //讀取旗幟清單
    // AI Translated: Load banner list
    public List<ItemStack> loadBannerList(Player player) {
        return loadBannerList(player, 0);
    }

    public List<ItemStack> loadBannerList(Player player, int page) {
        List<ItemStack> bannerList = new ArrayList<>();
        //設定檔
        // AI Translated: Configuration file
        String fileName = getFileName(player);
        ConfigManager.load(fileName);
        //強制重新讀取，以避免選單內容未即時更新
        // AI Translated: Force reload to avoid menu content not updating in real time
        ConfigManager.reload(fileName);
        FileConfiguration config = ConfigManager.get(fileName);
        //起始索引值
        // AI Translated: Starting index value
        int startIndex = Math.max(0, (page - 1) * 45);
        //旗幟
        // AI Translated: Banner
        Set<String> keySet = config.getKeys(false);
        List<String> keyList = new ArrayList<>(keySet);
        //載入該頁旗幟，若無指定頁碼，則載入全部
        // AI Translated: Load banners on that page; if no page number is specified, load all
        for (int i = startIndex; i < keyList.size() && (i < startIndex + 45 || page == 0); i++) {
            String key = keyList.get(i);
            //嘗試讀取旗幟
            // AI Translated: Try to load banner
            ItemStack banner = loadBanner(player, key);
            if (banner == null) {
                continue;
            }
            bannerList.add(banner);
        }
        return bannerList;
    }

    //讀取旗幟
    // AI Translated: Load banner
    private ItemStack loadBanner(Player player, String key) {
        //設定檔
        // AI Translated: Configuration file
        String fileName = getFileName(player);
        FileConfiguration config = ConfigManager.get(fileName);
        //檢查是否為正確格式
        // AI Translated: Check if it is in the correct format
        if ((!config.isInt(key + ".color") && !config.isString(key + ".color"))
            || (config.contains(key + ".patterns") && !config.isList(key + ".patterns"))) {
            return null;
        }
        ItemStack banner;
        //嘗試以新格式讀取
        // AI Translated: Try to read in new format
        try {
            //建立旗幟
            // AI Translated: Create banner
            if (config.isInt(key + ".color")) {
                // FIXME: 維持舊版相容性
                // AI Translated: Maintain compatibility with old version
                banner = new ItemStack(DyeColorRegistry.getBannerMaterial(config.getInt(key + ".color")));
            } else {
                banner = new ItemStack(DyeColorRegistry.getBannerMaterial(DyeColor.valueOf(config.getString(key + ".color"))));
            }
            BannerMeta bm = (BannerMeta) banner.getItemMeta();
            //新增Patterns
            // AI Translated: Add Patterns
            if (config.contains(key + ".patterns")) {
                List<String> patternsList = config.getStringList(key + ".patterns");
                final Registry<PatternType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);

                for (String str : patternsList) {
                    final String[] split = str.split(",");
                    String strPattern = split[0];
                    String strColor = split[1];

                    final PatternType patternType = registry.getOrThrow(Objects.requireNonNull(NamespacedKey.fromString(strPattern), "pattern type key"));
                    Pattern pattern = new Pattern(DyeColor.valueOf(strColor), patternType);
                    Objects.requireNonNull(bm).addPattern(pattern);
                }
            }
            //將 key 藏於 PersistentData
            // AI Translated: Hide key in PersistentData
            PersistentDataUtil.set(bm, "banner-key", key);
            banner.setItemMeta(bm);
        } catch (Exception e) {
            banner = null;
        }

        return banner;
    }

    //刪除旗幟
    // AI Translated: Delete banner
    public boolean removeBanner(Player player, String key) {
        //設定檔
        // AI Translated: Configuration file
        String fileName = getFileName(player);
        FileConfiguration config = ConfigManager.get(fileName);
        //移除
        // AI Translated: Remove
        config.set(key, null);
        //儲存
        // AI Translated: Save
        ConfigManager.save(fileName);
        return true;
    }

    //取得旗幟總數
    // AI Translated: Get total number of banners
    public int getBannerCount(Player player) {
        List<ItemStack> bannerList = loadBannerList(player);
        return bannerList.size();
    }

    //旗幟檔案路徑
    // AI Translated: Banner file path
    private String getFileName(Player player) {
        return getFileName(player.getUniqueId().toString());
    }

    private String getFileName(String configFileName) {
        return "banner" + File.separator + configFileName + ".yml";
    }
}
