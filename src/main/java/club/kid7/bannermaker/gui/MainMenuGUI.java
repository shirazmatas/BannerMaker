package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.service.MessageService;
import club.kid7.bannermaker.util.InventoryMenuUtil;
import club.kid7.bannermaker.util.ItemBuilder;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.List;

import static club.kid7.bannermaker.configuration.Language.tl;

public class MainMenuGUI {
    private final static String[] GUI_MENU = {
        "bbbbbbbbb",
        "bbbbbbbbb",
        "bbbbbbbbb",
        "bbbbbbbbb",
        "bbbbbbbbb",
        "p   c a n"
    };

    public static void show(Player player) {
        Component titleComponent = tl("gui.title.prefix").append(tl("gui.title.main-menu"));
        // InventoryFramework 標題需要 Legacy String
        // AI Translated: InventoryFramework title requires Legacy String
        String title = LegacyComponentSerializer.legacySection().serialize(titleComponent);

        InventoryGui mainMenuGUI = new InventoryGui(BannerMaker.getInstance(), player, title, GUI_MENU);

        // 1. 旗幟列表分頁面 (Paginated Pane)
        // AI Translated: 1. Banner list paginated pane (Paginated Pane)
        List<ItemStack> banners = BannerMaker.getInstance().getBannerRepository().loadBannerList(player);
        GuiElementGroup group = new GuiElementGroup('b');

        for (ItemStack banner : banners) {
            group.addElement(new StaticGuiElement('e', banner, event -> {
                InventoryMenuUtil.openBannerInfo(player, banner);
                return true;
            }));
        }

        mainMenuGUI.addElements(group);

        // 2. 靜態控制面板 (Static Pane) - 用於放置導航和功能按鈕
        // AI Translated: 2. Static control panel (Static Pane) - used for placing navigation and function buttons
        mainMenuGUI.addElement(new GuiPageElement('p', new ItemStack(Material.ARROW), GuiPageElement.PageAction.PREVIOUS,
                "Go to previous page (%prevpage%)"));
        mainMenuGUI.addElement(new GuiPageElement('n', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT,
            "Go to next page (%nextpage%)"));
        // 初始化導航按鈕

        // 製作旗幟按鈕
        // AI Translated: Craft banner button
        ItemStack btnCreateBanner = new ItemBuilder(Material.LIME_WOOL)
            .name(tl(NamedTextColor.GREEN, "gui.create-banner"))
            .build();
        mainMenuGUI.addElement(new StaticGuiElement('c', btnCreateBanner, click -> {
            CreateBannerGUI.show(player); // TODO: Change?
            return true;
        }));

        // 製作字母按鈕 (若啟用)
        // AI Translated: Craft alphabet button (if enabled)
        if (BannerMaker.getInstance().isEnableAlphabetAndNumber()) {
            ItemStack btnCreateAlphabet = AlphabetBanner.get("A");
            ItemBuilder btnBuilder = new ItemBuilder(btnCreateAlphabet);
            btnBuilder.name(tl(NamedTextColor.GREEN, "gui.alphabet-and-number"));
            mainMenuGUI.addElement(new StaticGuiElement('a', btnBuilder.build(), click ->{
                ChooseAlphabetGUI.show(player);
                return true;
            }));
            // AI Translated: Slot 51 is the 7th cell of the last row (index 6)
        }
        //InventoryGui mainMenuGUI = InventoryGui.get(InventoryHolder holder);
        mainMenuGUI.show(player);
    }
}
