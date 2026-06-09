package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.banner.BannerCost;
import club.kid7.bannermaker.banner.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.gui.mainmenu.MainMenu;
import club.kid7.bannermaker.service.MessageService;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.InventoryUtil;
import club.kid7.bannermaker.util.ItemBuilder;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;

import static club.kid7.bannermaker.configuration.Language.tl;
import static club.kid7.bannermaker.util.TagUtil.tag;
import static java.util.stream.Collectors.toList;

public class BannerInfoGUI {
    private static final String[] BANNER_INFO_DISPLAY = {
        "biw      ",
        "sssssssss",
        "sssssssss",
        "sssssssss",
        "sssssssss",
        "p d c erl"
    };
    public static void show(Player player) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        PlayerData playerData = BannerMaker.getInstance().getPlayerDataMap().get(player);

        final ItemStack banner = playerData.getViewInfoBanner();

        if (!BannerUtil.isBanner(banner)) {
            MainMenu.show(player);
            return;
        }

        Component titleComponent = tl("gui.title.prefix").append(tl("gui.title.banner-info"));
        String title = LegacyComponentSerializer.legacySection().serialize(titleComponent);

        // Create GUI
        InventoryGui bannerInfoGUI = new InventoryGui(BannerMaker.getInstance(), player, title, BANNER_INFO_DISPLAY);

        // Close Behaviour
        bannerInfoGUI.setCloseAction(close ->{
            // save banner??? TODO defined behaviour
            return false;
        });

        // Slot 0 (0,0): Banner preview
        bannerInfoGUI.addElement(new StaticGuiElement('b',banner));

        // Slot 1 (1,0): Number of patterns
        bannerInfoGUI.addElement(buildPatternCount(player, banner));

        //  Slot 2 (2,0): Whether materials are sufficient (if craftable)
        if (BannerUtil.isCraftable(player, banner)) {
            bannerInfoGUI.addElement(buildSufficientMaterial(player,banner));
            bannerInfoGUI.addElement(buildMaterialGroup(banner));
        }

        // Function buttons (bottom row)

        // Back button
        bannerInfoGUI.addElement(buildBackButton(player, banner));

        // Delete the banner (if already saved)
        final String key = BannerUtil.getKey(banner);
        if (key != null) {
            bannerInfoGUI.addElement(buildDeleteButton(player, key, messageService));
        }

        // Get banner
        if (player.hasPermission("bannermaker.getbanner")) {
            if (player.hasPermission("bannermaker.getbanner.free") || player.getGameMode() == GameMode.CREATIVE) {
                bannerInfoGUI.addElement(buildFreeButton(player, banner, messageService));
            } else {
                bannerInfoGUI.addElement(buildCraftButton(player, banner, messageService));
            }
        }

        // Clone and edit
        bannerInfoGUI.addElement(buildCloneButton(player, banner,playerData));

        // Display banner
        if (player.hasPermission("bannermaker.show.nearby") || player.hasPermission("bannermaker.show.all")) {
            bannerInfoGUI.addElement(buildShareButton(player, banner));
        }

        // Generate link to share
        if (player.hasPermission("bannermaker.view")) {
            bannerInfoGUI.addElement(buildLinkButton(player, banner));
        }

        bannerInfoGUI.show(player);
    }

    private static StaticGuiElement buildCraftButton(Player player, ItemStack banner, MessageService messageService) {
        ItemStack btnGetBanner = new ItemBuilder(Material.LIME_WOOL).name(tl(NamedTextColor.GREEN, "gui.get-this-banner")).build();
        btnGetBanner = new ItemBuilder(btnGetBanner).addLore(Component.text("[", NamedTextColor.YELLOW).append(tl("gui.click.left").append(Component.text("] ", NamedTextColor.YELLOW)).append(tl(NamedTextColor.GREEN, "gui.get-banner-by-craft")))).build();
        return new StaticGuiElement('c', btnGetBanner, click -> {
            boolean success = BannerMaker.getInstance().getBannerService().craft(player, banner);
            if (success) {
                messageService.send(player, tl(NamedTextColor.GREEN, "gui.get-banner", tag("name", BannerUtil.getName(banner))));
            } else {
                messageService.send(player, tl(NamedTextColor.RED, "gui.materials.not-enough"));
            }
            BannerInfoGUI.show(player);
            return true;
        });
    }

    private static StaticGuiElement buildFreeButton(Player player, ItemStack banner, MessageService messageService) {
        ItemStack btnGetBanner = new ItemBuilder(Material.LIME_WOOL).name(tl(NamedTextColor.GREEN, "gui.get-this-banner")).build();
        btnGetBanner = new ItemBuilder(btnGetBanner).addLore(Component.text("[", NamedTextColor.YELLOW).append(tl("gui.click.left").append(Component.text("] ", NamedTextColor.YELLOW)).append(tl(NamedTextColor.GREEN, "gui.get-banner-for-free")))).build();
        return new StaticGuiElement('c', btnGetBanner, click -> {
            InventoryUtil.give(player, banner);
            messageService.send(player, tl(NamedTextColor.GREEN, "gui.get-banner", tag("name", BannerUtil.getName(banner))));
            BannerInfoGUI.show(player);
            return true;
        });
    }

    private static StaticGuiElement buildLinkButton(Player player, ItemStack banner) {
        ItemStack btnGenerateCommand = new ItemBuilder(Material.COMMAND_BLOCK).name(tl(NamedTextColor.BLUE, "gui.get-share-command")).build();
        return new StaticGuiElement('l', btnGenerateCommand, click -> {
            BannerMaker.getInstance().getBannerService().sendShareCommand(player, banner);
            player.closeInventory();
            return true;
        });
    }

    private static StaticGuiElement buildShareButton(Player player, ItemStack banner) {
        ItemStack btnShow = new ItemBuilder(Material.BELL).name(tl(NamedTextColor.BLUE, "gui.show-banner")).build();
        if (player.hasPermission("bannermaker.show.nearby")) {
            btnShow = new ItemBuilder(btnShow).addLore(Component.text("[", NamedTextColor.YELLOW).append(tl("gui.click.left")).append(Component.text("] ", NamedTextColor.YELLOW)).append(tl(NamedTextColor.GREEN, "gui.show-to-nearby"))).build();
        }
        if (player.hasPermission("bannermaker.show.all")) {
            btnShow = new ItemBuilder(btnShow).addLore(Component.text("[", NamedTextColor.YELLOW).append(tl("gui.click.right")).append(Component.text("] ", NamedTextColor.YELLOW)).append(tl(NamedTextColor.GREEN, "gui.show-to-all"))).build();
        }
        return new StaticGuiElement('r', btnShow, click -> {
            if (click.getType().isLeftClick() && player.hasPermission("bannermaker.show.nearby")) {
                BannerMaker.getInstance().getBannerService().showToNearby(player, banner, 16);
            } else if (click.getType().isRightClick() && player.hasPermission("bannermaker.show.all")) {
                BannerMaker.getInstance().getBannerService().showToAll(player, banner);
            }
            player.closeInventory();
            return true;
        });
    }

    private static StaticGuiElement buildCloneButton(Player player, ItemStack banner, PlayerData playerData) {
        ItemStack btnCloneAndEdit = new ItemBuilder(Material.WRITABLE_BOOK).name(tl(NamedTextColor.BLUE, "gui.clone-and-edit")).build();
        return new StaticGuiElement('e',btnCloneAndEdit, click -> {
            playerData.setCurrentEditBanner(banner);
            CreateBannerGUI.show(player);
            return true;
        });
    }

    private static StaticGuiElement buildDeleteButton(Player player, String key, MessageService messageService) {
        ItemStack btnDelete = new ItemBuilder(Material.BARRIER).name(tl(NamedTextColor.RED, "gui.delete")).build();
        return new StaticGuiElement('d',btnDelete, click -> {
            BannerMaker.getInstance().getBannerRepository().removeBanner(player, key);
            messageService.send(player, tl(NamedTextColor.GREEN, "io.remove-banner", tag("key", key)));
            MainMenu.show(player);
            return true;
        });
    }

    private static StaticGuiElement buildBackButton(Player player, ItemStack banner) {
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(tl(NamedTextColor.RED, "gui.back")).build();
        return new StaticGuiElement('p',btnBackToMenu, click -> {
            if (AlphabetBanner.isAlphabetBanner(banner)){
                CreateAlphabetGUI.show(player);
            } else {
                MainMenu.show(player);
            }
            return true;
        });
    }

    private static GuiElementGroup buildMaterialGroup(ItemStack banner) {
        GuiElementGroup materialsGroup = new GuiElementGroup('s');
        materialsGroup.addElements(buildMaterials(banner));
        return materialsGroup;
    }

    private static List<GuiElement> buildMaterials(ItemStack banner) {
        List<ItemStack> materialList = BannerCost.getMaterials(banner);
        return materialList.stream().map(BannerInfoGUI::buildMaterialDisplay).collect(toList());
    }

    private static @NotNull StaticGuiElement buildMaterialDisplay(ItemStack material) {
        return new StaticGuiElement('e', material);
    }

    private static StaticGuiElement buildSufficientMaterial(Player player, ItemStack banner) {
        ItemStack enoughMaterials;
        if (BannerCost.hasEnoughMaterials(player.getInventory(), banner)) {
            enoughMaterials = new ItemBuilder(Material.OAK_SIGN).name(tl(NamedTextColor.GREEN, "gui.materials.enough")).build();
        } else {
            enoughMaterials = new ItemBuilder(Material.OAK_SIGN).name(tl(NamedTextColor.RED, "gui.materials.not-enough")).build();
        }
        return new StaticGuiElement('w', enoughMaterials);
    }

    private static StaticGuiElement buildPatternCount(Player player, ItemStack banner) {
        int patternCount = ((BannerMeta) Objects.requireNonNull(banner.getItemMeta())).numberOfPatterns();
        Component patternCountComp;
        if (patternCount > 0) {
            patternCountComp = Component.text(patternCount + " ").append(tl("gui.pattern-s"));
        } else {
            patternCountComp = tl("gui.no-patterns");
        }
        ItemStack signPatternCount;
        if (BannerUtil.isCraftable(player, banner)) {
            signPatternCount = new ItemBuilder(Material.OAK_SIGN).name(Component.empty().color(NamedTextColor.GREEN).append(patternCountComp)).build();
        } else {
            signPatternCount = new ItemBuilder(Material.OAK_SIGN)
                .name(Component.empty().color(NamedTextColor.GREEN).append(patternCountComp))
                .lore(tl(NamedTextColor.RED, "gui.uncraftable")).build();
        }
        return new StaticGuiElement('i', signPatternCount);
    }
}
