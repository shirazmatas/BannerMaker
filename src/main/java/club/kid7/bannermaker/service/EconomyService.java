package club.kid7.bannermaker.service;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.configuration.ConfigManager;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.MaterialUtil;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EconomyService {

    /**
     * 檢查經濟系統是否可用
     * AI Translated: Check if the economy system is available
     *
     * @return 是否可用
     * AI Translated: Whether it is available
     */
    public boolean isAvailable() {
        return BannerMaker.getInstance().getEconomy() != null;
    }

    /**
     * 取得旗幟的價格
     * AI Translated: Get the price of the banner
     *
     * @param banner 旗幟
     * AI Translated: Banner
     * @return 價格
     * AI Translated: Price
     */
    public double getPrice(ItemStack banner) {
        if (!BannerUtil.isBanner(banner)) {
            return 0;
        }
        if (!isAvailable()) {
            return 0;
        }
        FileConfiguration config = ConfigManager.get("config");
        if (config == null) {
            return 0;
        }
        double price = config.getDouble("Economy.Price", 0);

        List<ItemStack> materials = BannerUtil.getMaterials(banner);
        for (ItemStack material : materials) {
            price += getMaterialPrice(material) * material.getAmount();
        }

        return price;
    }

    /**
     * 格式化金額
     * AI Translated: Format amount
     *
     * @param amount 金額
     * AI Translated: Amount
     * @return 格式化後的字串
     * AI Translated: Formatted string
     */
    public String format(double amount) {
        return BannerMaker.getInstance().getEconomy().format(amount);
    }

    /**
     * 檢查玩家是否有足夠的錢
     * AI Translated: Check if the player has enough money
     *
     * @param player 玩家
     * AI Translated: Player
     * @param amount 金額
     * AI Translated: Amount
     * @return 是否足夠
     * AI Translated: Whether it is enough
     */
    public boolean has(Player player, double amount) {
        return BannerMaker.getInstance().getEconomy().has(player, amount);
    }

    /**
     * 從玩家扣款
     * AI Translated: Withdraw money from player
     *
     * @param player 玩家
     * AI Translated: Player
     * @param amount 金額
     * AI Translated: Amount
     * @return 交易回應
     * AI Translated: Transaction response
     */
    public EconomyResponse withdraw(Player player, double amount) {
        return BannerMaker.getInstance().getEconomy().withdrawPlayer(player, amount);
    }

    private double getMaterialPrice(ItemStack itemStack) {
        String priceConfigFileName = "price";
        FileConfiguration priceConfig = ConfigManager.get(priceConfigFileName);
        if (priceConfig == null) {
            return 0;
        }
        //物品資料
        // AI Translated: Item data
        Material type = itemStack.getType();
        //預設路徑
        // AI Translated: Default path
        String configPath = type.toString();
        //特殊路徑
        // AI Translated: Special path
        if (MaterialUtil.isWool(type)) {
            DyeColor woolColor = DyeColorRegistry.getDyeColor(type);
            if (woolColor != null) {
                configPath = "WOOL." + woolColor.name();
            }
        } else if (MaterialUtil.isDye(type)) {
            DyeColor dyeColor = DyeColorRegistry.getDyeColor(type);
            if (dyeColor != null) {
                configPath = "DYE." + dyeColor.name();
            }
        }
        //檢查設定
        // AI Translated: Check settings
        if (!priceConfig.contains(configPath)) {
            priceConfig.set(configPath, 0);
            ConfigManager.save(priceConfigFileName);
        }

        //取得金額
        // AI Translated: Get amount
        return priceConfig.getDouble(configPath, 0);
    }
}
