package club.kid7.bannermaker.gui.mainmenu;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.gui.ChooseAlphabetGUI;
import club.kid7.bannermaker.gui.CreateBannerGUI;
import club.kid7.bannermaker.util.InventoryMenuUtil;
import club.kid7.bannermaker.util.ItemBuilder;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.GuiPageElement.PageAction;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static club.kid7.bannermaker.configuration.Language.tl;
import static java.util.stream.Collectors.toList;

public class MainMenu {
    private final static String[] MENU_DISPLAY_PATTERN = {
        "bbbbbbbbb",
        "bbbbbbbbb",
        "bbbbbbbbb",
        "bbbbbbbbb",
        "bbbbbbbbb",
        "p   c a n"
    };

    public static void show(Player player) {
        InventoryGui mainMenuDisplay = new InventoryGui(BannerMaker.getInstance(), player, buildFormatedMainMenuTitle(), MENU_DISPLAY_PATTERN);

        mainMenuDisplay.addElement(buildBannerGroup(player));
        mainMenuDisplay.addElement(buildPreviousPageButton());
        mainMenuDisplay.addElement(buildNextPageButton());
        mainMenuDisplay.addElement(buildCreateButton(player));

        if (BannerMaker.getInstance().isEnableAlphabetAndNumber()) {
            mainMenuDisplay.addElement(buildAlphanumericalSelector(player));
        }
        mainMenuDisplay.show(player);
    }

    private static StaticGuiElement buildAlphanumericalSelector(Player player) {
        ItemStack alphanumericalSelector = new ItemBuilder(AlphabetBanner.get("A"))
            .name(tl(NamedTextColor.GREEN, "gui.alphabet-and-number"))
            .build();
        return new StaticGuiElement('a', alphanumericalSelector, click -> {
            ChooseAlphabetGUI.show(player);
            return true;
        });
    }

    private static StaticGuiElement buildCreateButton(Player player) {
        ItemStack createBannerButton = new ItemBuilder(Material.LIME_WOOL)
            .name(tl(NamedTextColor.GREEN, "gui.create-banner"))
            .build();
        return new StaticGuiElement('c', createBannerButton, click -> {
            CreateBannerGUI.show(player);
            return true;
        });
    }

    private static GuiPageElement buildNextPageButton() {
        return new GuiPageElement(
            'n',
            new ItemStack(Material.ARROW),
            PageAction.NEXT,
            "Go to next page (%nextpage%)" //TODO: extract in locales if possible
        );
    }

    private static GuiPageElement buildPreviousPageButton() {
        return new GuiPageElement(
            'p',
            new ItemStack(Material.ARROW),
            PageAction.PREVIOUS,
            "Go to previous page (%prevpage%)" //TODO: extract in locales if possible
        );
    }

    private static GuiElementGroup buildBannerGroup(Player player) {
        GuiElementGroup bannersGroup = new GuiElementGroup('b');
        bannersGroup.addElements(buildBanners(player));
        return bannersGroup;
    }

    private static List<GuiElement> buildBanners(Player player) {
        List<ItemStack> banners = BannerMaker.getInstance().getBannerRepository().loadBannerList(player);
        return banners.stream().map(banner -> buildBannerDisplay(player, banner)).collect(toList());
    }

    private static @NotNull StaticGuiElement buildBannerDisplay(Player player, ItemStack banner) {
        return new StaticGuiElement('e', banner, event -> {
            InventoryMenuUtil.openBannerInfo(player, banner);
            return true;
        });
    }

    private static String buildFormatedMainMenuTitle() {
        Component titleComponent = tl("gui.title.prefix").append(tl("gui.title.main-menu"));
        return LegacyComponentSerializer.legacySection().serialize(titleComponent);
    }
}
