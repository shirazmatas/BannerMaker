package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.banner.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.util.ItemBuilder;
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

import static club.kid7.bannermaker.configuration.Language.tl;

public class CreateAlphabetGUI {
    private static final String[] GUI_ALPHABET_DESIGN_MODE = {
        "xbbbbbbbb",
        " bbbbbbbb",
        " dddddddd",
        " dddddddd",
        " t       ",
        "p   c    "
    };

    public static void show(Player player) {
        PlayerData playerData = BannerMaker.getInstance().getPlayerDataMap().get(player);

        Component titleComponent = tl("gui.title.prefix").append(tl("gui.title.alphabet-and-number"));
        String title = LegacyComponentSerializer.legacySection().serialize(titleComponent);
        InventoryGui createAlphabetGUI = new InventoryGui(BannerMaker.getInstance(), player, title, GUI_ALPHABET_DESIGN_MODE);
        createAlphabetGUI.setCloseAction(close ->{
            return false;
        });


        final AlphabetBanner currentAlphabetBanner = playerData.getCurrentAlphabetBanner();
        if (currentAlphabetBanner == null) {
            ChooseAlphabetGUI.show(player);
            return;
        }

        // Slot 0 (0,0):Preview
        createAlphabetGUI.addElement(new StaticGuiElement('x', currentAlphabetBanner.toItemStack()));

        // Base color selection (Slots 1-17, row 0 and 1)
        GuiElementGroup bannerGroup = new GuiElementGroup('b');
        for (int i = 0; i < 16; i++) {
            final ItemStack banner = new ItemBuilder(DyeColorRegistry.getBannerMaterial(i)).build();
            bannerGroup.addElement(new StaticGuiElement('e',banner,click -> {
                currentAlphabetBanner.setBaseColor(DyeColorRegistry.getDyeColor(banner.getType()));
                CreateAlphabetGUI.show(player);
                return true;
            }));
        }
        createAlphabetGUI.addElement(bannerGroup);

        // Dye color selection (Slots 19-35, row 2 and 3)
        GuiElementGroup dyeGroup = new GuiElementGroup('d');
        for (int i = 0; i < 16; i++) {
            final ItemStack dye = new ItemBuilder(DyeColorRegistry.getDyeMaterial(i)).build();
            dyeGroup.addElement(new StaticGuiElement('e',dye,click -> {
                currentAlphabetBanner.setDyeColor(DyeColorRegistry.getDyeColor(dye.getType()));
                CreateAlphabetGUI.show(player);
                return true;
            }));
        }
        createAlphabetGUI.addElement(dyeGroup);

        // Toggle border
        ItemStack btnBorderedBanner = new ItemBuilder(Material.WHITE_BANNER)
            .name(tl(NamedTextColor.GREEN, "gui.toggle-border"))
            .pattern(new Pattern(DyeColor.BLACK, PatternType.BORDER)).build();
        createAlphabetGUI.addElement(new StaticGuiElement('t',btnBorderedBanner,click -> {
            currentAlphabetBanner.setBordered(! currentAlphabetBanner.isBordered());
            CreateAlphabetGUI.show(player);
            return true;
        }));

        // Banner info
        ItemStack btnBannerInfo = new ItemBuilder(Material.LIME_WOOL).name(tl(NamedTextColor.GREEN, "gui.banner-info")).build();
        createAlphabetGUI.addElement(new StaticGuiElement('c',btnBannerInfo,click -> {
            playerData.setViewInfoBanner(currentAlphabetBanner.toItemStack()); // TODO: Replace with smarter refresh as shown in latest update
            playerData.setCurrentRecipePage(1);
            BannerInfoGUI.show(player);
            return true;
        }));

        // Back button
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(tl(NamedTextColor.RED, "gui.back")).build();
        createAlphabetGUI.addElement(new StaticGuiElement('p',btnBackToMenu,click -> {
            ChooseAlphabetGUI.show(player);
            return true;
        }));

        createAlphabetGUI.show(player);
    }
}
