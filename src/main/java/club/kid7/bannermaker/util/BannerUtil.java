package club.kid7.bannermaker.util;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BannerUtil {
    /**
     * Check if ItemStack is a banner
     *
     * @param itemStack The item to check
     * @return Whether it is a banner
     */
    static public boolean isBanner(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return isBanner(itemStack.getType());
    }

    /**
     * Check if Material is a banner
     *
     * @param material The material to check
     * @return Whether it is a banner
     */
    static public boolean isBanner(Material material) {
        if (material == null) {
            return false;
        }
        return XTag.BANNERS.isTagged(XMaterial.matchXMaterial(material));
    }

    /**
     * Determine if it is a loom recipe
     *
     * @param patternRecipe The recipe to check, produced by getPatternRecipe()
     * @return Whether it is a loom recipe
     */
    static public boolean isLoomRecipe(HashMap<Integer, ItemStack> patternRecipe) {
        for (Map.Entry<Integer, ItemStack> entry : patternRecipe.entrySet()) {
            ItemStack itemStack = entry.getValue();
            // If it contains a banner pattern item, it is directly regarded as a loom recipe
            if (isBannerPatternItemStack(itemStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if it is a banner pattern item
     *
     * @param itemStack The item to check
     * @return Whether it is a banner pattern item
     */
    static public boolean isBannerPatternItemStack(ItemStack itemStack) {
        return itemStack.getType().toString().endsWith("_BANNER_PATTERN");
    }

    /**
     * Whether it can be crafted
     *
     * @param player Player
     * @param banner Banner
     * @return Whether it can be crafted
     */
    static public boolean isCraftable(Player player, ItemStack banner) {
        //Only check banners
        if (!isBanner(banner)) {
            return false;
        }
        int patternCount = ((BannerMeta) Objects.requireNonNull(banner.getItemMeta())).numberOfPatterns();

        // If complex crafting is enabled, additionally check if the player has the corresponding permission
        if (BannerMaker.getInstance().isEnableComplexBannerCraft()) {
            if (player.hasPermission("bannermaker.getbanner.complex-craft")) {
                return patternCount <= 12;
            }
        }

        return patternCount <= 6;
    }

    /**
     * Get the banner's key in the player's archive
     *
     * @param banner The banner to check
     * @return String
     */
    static public String getKey(ItemStack banner) {
        //只處理旗幟
        // AI Translated: Only handle banners
        if (!isBanner(banner)) {
            return null;
        }
        ItemMeta itemMeta = Objects.requireNonNull(banner.getItemMeta());
        return PersistentDataUtil.get(itemMeta, "banner-key");
    }

    /**
     * Get the banner name or try to get the KEY if there is no name
     *
     * @param banner The banner to check
     * @return String
     */
    static public String getName(ItemStack banner) {
        //只處理旗幟
        // AI Translated: Only handle banners
        if (!isBanner(banner)) {
            return null;
        }
        //先試著取得自訂名稱
        // AI Translated: First try to get the custom name
        if (banner.hasItemMeta() && Objects.requireNonNull(banner.getItemMeta()).hasDisplayName()) {
            return banner.getItemMeta().getDisplayName();
        }
        //嘗試取得key
        // AI Translated: Try to get the key
        String key = BannerUtil.getKey(banner);
        if (key != null) {
            return key;
        }
        //若都沒有，回傳空字串
        // AI Translated: If neither, return an empty string
        return "";
    }

    /**
     * Gets a list of all banner pattern types currently supported by the server (excluding BASE).
     * The results are sorted by namespaced key for use in GUI display and selection.
     *
     * @return List of available PatternTypes
     */
    public static List<PatternType> getPatternTypeList() {
        final Registry<PatternType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);

        return registry.stream()
            .sorted(Comparator.comparing(p -> registry.getKeyOrThrow(p).asMinimalString()))
            .filter(pattern -> !registry.getKeyOrThrow(pattern).asMinimalString().equals("base"))
            .toList();
    }


    static public String serialize(ItemStack banner) {
        //只檢查旗幟
        // AI Translated: Only check banners
        if (!isBanner(banner)) {
            return null;
        }
        DyeColor color = Objects.requireNonNull(DyeColorRegistry.getDyeColor(banner.getType()));
        int colorCode = DyeColorRegistry.getValue(color);
        StringBuilder dataStringBuilder = new StringBuilder(String.valueOf(colorCode));

        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

        final Registry<PatternType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);

        for (Pattern pattern : bm.getPatterns()) {
            final NamespacedKey patternKey = registry.getKey(pattern.getPattern());
            if (patternKey == null) {
                return null; // can't serialize inline patterns
            }

            dataStringBuilder
                .append(";")
                .append(patternKey.asMinimalString())
                .append(":")
                .append(DyeColorRegistry.getValue(pattern.getColor()));
        }
        String dataString = dataStringBuilder.toString();

        return SerializationUtil.objectToBase64(dataString);
    }

    static public ItemStack deserialize(String bannerString) {
        try {
            String dataString = SerializationUtil.objectFromBase64(bannerString);
            String[] dataArray = dataString.split(";");

            ItemStack banner = new ItemStack(DyeColorRegistry.getBannerMaterial(Integer.parseInt(dataArray[0])));

            BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

            final Registry<PatternType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);

            for (int i = 1; i < dataArray.length; i++) {
                String[] patternData = dataArray[i].split(":");
                final NamespacedKey patternTypeKey = NamespacedKey.fromString(patternData[0]);
                if (patternTypeKey == null) {
                    continue;
                }

                PatternType patternType = registry.get(patternTypeKey);
                DyeColor patternColor = DyeColorRegistry.getDyeColor(Integer.parseInt(patternData[1]));
                Pattern pattern = new Pattern(patternColor, Objects.requireNonNull(patternType));
                bm.addPattern(pattern);
            }
            banner.setItemMeta(bm);
            return banner;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
