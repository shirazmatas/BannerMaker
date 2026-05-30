package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.gui.mainmenu.MainMenu;
import club.kid7.bannermaker.service.MessageService;
import club.kid7.bannermaker.util.ItemBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static club.kid7.bannermaker.configuration.Language.tl;

public class ChooseAlphabetGUI {

    public static void show(Player player) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        PlayerData playerData = BannerMaker.getInstance().getPlayerDataMap().get(player);

        Component titleComponent = tl("gui.title.prefix").append(tl("gui.title.alphabet-and-number"));
        String title = LegacyComponentSerializer.legacySection().serialize(titleComponent);
        ChestGui gui = new ChestGui(6, title);
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
        gui.addPane(mainPane);

        // 清除當前編輯中的字母旗幟
        // AI Translated: Clear the currently editing alphabet banner
        playerData.setCurrentAlphabetBanner(null);

        boolean alphabetBorder = playerData.isAlphabetBannerBordered();
        char[] alphabetArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789?!.".toCharArray();

        // 填充字母物品 (i=0-53)
        // AI Translated: Fill alphabet items (i=0-53)
        // 放置在 Slot 0-53 (整個介面)
        // AI Translated: Placed in Slot 0-53 (entire interface)
        for (int i = 0; i < alphabetArray.length && i < 54; i++) {
            char alphabet = alphabetArray[i];
            final AlphabetBanner alphabetBanner = new AlphabetBanner(String.valueOf(alphabet), DyeColor.WHITE, DyeColor.BLACK, alphabetBorder);
            ItemStack alphabetItem = alphabetBanner.toItemStack();
            mainPane.addItem(new GuiItem(alphabetItem, event -> {
                playerData.setCurrentAlphabetBanner(alphabetBanner);
                CreateAlphabetGUI.show(player);
                event.setCancelled(true);
            }), i % 9, i / 9);
        }

        // Slot 49 (4,5): 切換邊框
        // AI Translated: Slot 49 (4,5): Toggle border
        // 注意：這會覆蓋掉字母列表在 Slot 49 的項目（如果有的話）
        // AI Translated: Note: This will overwrite the item in Slot 49 of the alphabet list (if any)
        // 字母列表最多到 '.' (index 38)，所以 Slot 49 是安全的。
        // AI Translated: The alphabet list goes up to '.' (index 38), so Slot 49 is safe.
        ItemStack btnBorderedBanner = new ItemBuilder(Material.WHITE_BANNER)
            .name(tl(NamedTextColor.GREEN, "gui.toggle-border"))
            .pattern(new Pattern(DyeColor.BLACK, PatternType.BORDER)).build();
        mainPane.addItem(new GuiItem(btnBorderedBanner, event -> {
            playerData.setAlphabetBannerBordered(!playerData.isAlphabetBannerBordered());
            ChooseAlphabetGUI.show(player); // 刷新以顯示變更
            // AI Translated: Refresh to show changes
            event.setCancelled(true);
        }), 4, 5); // 修正為 (4, 5)
        // AI Translated: Corrected to (4, 5)

        // Slot 45 (0,5): 返回按鈕
        // AI Translated: Slot 45 (0,5): Back button
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(tl(NamedTextColor.RED, "gui.back")).build();
        mainPane.addItem(new GuiItem(btnBackToMenu, event -> {
            MainMenu.show(player);
            event.setCancelled(true);
        }), 0, 5); // 修正為 (0, 5)
        // AI Translated: Corrected to (0, 5)

        gui.show(player);
    }
}
