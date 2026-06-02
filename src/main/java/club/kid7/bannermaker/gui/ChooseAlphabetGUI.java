package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.gui.mainmenu.MainMenu;
import club.kid7.bannermaker.service.MessageService;
import club.kid7.bannermaker.util.ItemBuilder;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static club.kid7.bannermaker.configuration.Language.tl;

public class ChooseAlphabetGUI {
    private static final String[] GUI_ALPHABET_DISPLAY = {
        "ccccccccc",
        "ccccccccc",
        "ccccccccc",
        "ccccccccc",
        "ccccccccc",
        "p   t    "
    };
    public static void show(Player player) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        PlayerData playerData = BannerMaker.getInstance().getPlayerDataMap().get(player);

        Component titleComponent = tl("gui.title.prefix").append(tl("gui.title.alphabet-and-number"));
        String title = LegacyComponentSerializer.legacySection().serialize(titleComponent);
        InventoryGui chooseAlphabetGUI = new InventoryGui(BannerMaker.getInstance(), player, title, GUI_ALPHABET_DISPLAY);
        // Clear the currently editing alphabet banner
        playerData.setCurrentAlphabetBanner(null);
        chooseAlphabetGUI.setCloseAction(close ->{
            // save banner??? TODO defined behaviour
            return false;
        });
        chooseAlphabetGUI.addElement(buildAlphabetGroup(player, playerData));
        chooseAlphabetGUI.addElement(buildBackButton(player));
        chooseAlphabetGUI.addElement(buildToggleButton(player, playerData));
        chooseAlphabetGUI.show(player);
    }

    private static StaticGuiElement buildToggleButton(Player player, PlayerData playerData) {
        ItemStack btnBorderedBanner = new ItemBuilder(Material.WHITE_BANNER)
            .name(tl(NamedTextColor.GREEN, "gui.toggle-border"))
            .pattern(new Pattern(DyeColor.BLACK, PatternType.BORDER)).build();
        return new StaticGuiElement('t',btnBorderedBanner, click -> {
            playerData.toggleDefaultAlphabetBordered();
            ChooseAlphabetGUI.show(player); // TODO replace with smarter group
            return true;
        });
    }

    private static GuiElementGroup buildAlphabetGroup(Player player, PlayerData playerData) {
        GuiElementGroup alphabetGroup = new GuiElementGroup('c');
        alphabetGroup.addElements(buildAlphabet(player, playerData));
        return alphabetGroup;
    }

    private static StaticGuiElement buildBackButton(Player player) {
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(tl(NamedTextColor.RED, "gui.back")).build();
        return new StaticGuiElement('p',btnBackToMenu, click -> {
            MainMenu.show(player);
            return true;
        });
    }

    private static List<GuiElement> buildAlphabet(Player player, PlayerData playerData) {
        List<String> characters = AlphabetBanner.SUPPORTED_CHARACTERS;
        boolean alphabetBorder = playerData.isAlphabetBannerBordered();
        List<GuiElement> alphabetElements = new java.util.ArrayList<>();
        for (int i = 0; i < characters.size() & i < 45; i++) {
            final AlphabetBanner alphabetBanner = new AlphabetBanner(characters.get(i), DyeColor.WHITE, DyeColor.BLACK, alphabetBorder);
            ItemStack alphabetItem = alphabetBanner.toItemStack();
            StaticGuiElement alphabetElement = buildAlphabetDisplay(player, playerData, alphabetItem, alphabetBanner);
            alphabetElements.add(alphabetElement);
        }
        return alphabetElements;
    }

    private static @NotNull StaticGuiElement buildAlphabetDisplay(Player player, PlayerData playerData, ItemStack alphabetItem, AlphabetBanner alphabetBanner) {
        return new StaticGuiElement('e', alphabetItem, event -> {
            playerData.setCurrentAlphabetBanner(alphabetBanner);
            CreateAlphabetGUI.show(player);
            return true;
        });
    }
}
