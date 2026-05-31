package club.kid7.bannermaker.gui.mainmenu;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.gui.ChooseAlphabetGUI;
import club.kid7.bannermaker.gui.CreateBannerGUI;
import club.kid7.bannermaker.util.InventoryMenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MainMenuController {
    public static void show(Player player) {
        MainMenuView mainMenuView = new MainMenuView(BannerMaker.getInstance(), player);

        List<ItemStack> savedBanners = BannerMaker.getInstance().getBannerRepository().loadBannerList(player);
        mainMenuView.initializeView(
            savedBanners,
            event -> {
                CreateBannerGUI.show(player);
                return true;
            },
            event -> {
                ChooseAlphabetGUI.show(player);
                return true;
            },
            banner -> {
                InventoryMenuUtil.openBannerInfo(player, banner);
            }
        );
    }
}
