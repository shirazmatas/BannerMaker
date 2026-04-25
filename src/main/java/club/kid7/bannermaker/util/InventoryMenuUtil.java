package club.kid7.bannermaker.util;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.gui.BannerInfoGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryMenuUtil {

    public static void openBannerInfo(Player player, ItemStack banner) {
        if (!BannerUtil.isBanner(banner)) {
            return;
        }
        PlayerData playerData = BannerMaker.getInstance().getPlayerDataMap().get(player);
        //設定查看旗幟
        // AI Translated: Set banner to view
        playerData.setViewInfoBanner(banner);
        //重置頁數
        // AI Translated: Reset page number
        playerData.setCurrentRecipePage(1);
        //開啟選單
        // AI Translated: Open menu
        BannerInfoGUI.show(player);
    }
}
