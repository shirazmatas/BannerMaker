package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.service.BannerRepository;
import club.kid7.bannermaker.service.MessageService;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.ItemBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiStateElement;
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
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Objects;

import static club.kid7.bannermaker.configuration.Language.tl;
import static club.kid7.bannermaker.registry.DyeColorRegistry.getDyeColor;
import static club.kid7.bannermaker.registry.DyeColorRegistry.getDyeMaterial;

public class CreateBannerGUI {
    private static final String[] GUI_DESIGN_MODE = {
        "xdddddddd",
        "wdddddddd",
        "sbbbbbbbb",
        " bbbbbbbb",
        " bbbbbbbb",
        "p d u m c"
    };
    private static final String[] GUI_BASE_COLOR = {
        " dddddddd",
        " dddddddd",
        "         ",
        "         ",
        "         ",
        "p        ",
    };

    public static void show(Player player) {
        PlayerData playerData = BannerMaker.getInstance().getPlayerDataMap().get(player);

        Component titleComponent = tl("gui.title.prefix").append(tl("gui.title.create-banner"));
        String title = LegacyComponentSerializer.legacySection().serialize(titleComponent);
        // Retrieve current banner being edited
        ItemStack currentBanner = playerData.getCurrentEditBanner();
        // new logic
        InventoryGui createBannerGUI = new InventoryGui(BannerMaker.getInstance(), player, title, GUI_DESIGN_MODE);
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(tl(NamedTextColor.RED, "gui.back")).build();
        createBannerGUI.addElement('p', btnBackToMenu, click -> {
            MainMenuGUI.show(player);
            return true;
        });

        if (currentBanner == null) { // If no previous banner we are in setup phase
            // choose base color (using the same layout logic as color selection)
            GuiElementGroup colorGroup = new GuiElementGroup('d');
            for (int i = 0; i < 16; i++) {
                final int colorIndex = i;
                ItemStack banner = new ItemBuilder(DyeColorRegistry.getBannerMaterial(colorIndex)).build();
                colorGroup.addElement(new StaticGuiElement('e', banner, click -> {
                    playerData.setCurrentEditBanner(banner);
                    CreateBannerGUI.show(player);
                    return true;
                }));
            }
            createBannerGUI.addElement(colorGroup);
            createBannerGUI.show(player);
            return;
        }

        // --- Edit Mode ---
        // Current banner being edited = x
        createBannerGUI.addElement(new StaticGuiElement('x', currentBanner));
        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 6) {
            ItemStack warning = new ItemBuilder(Material.OAK_SIGN)
                .name(tl(NamedTextColor.RED, "gui.uncraftable-warning"))
                .lore(tl("gui.more-than-6-patterns")).build();
            createBannerGUI.addElement(new StaticGuiElement('w', warning));
        }

        // Color selectors = d
        GuiElementGroup dyeGroup = new GuiElementGroup('d');
        for (int colorIndex = 0; colorIndex < 16; colorIndex++) {
            ItemStack dye = new ItemBuilder(getDyeMaterial(getDyeColor(colorIndex))).build();
            // New Logic
            dyeGroup.addElement(new StaticGuiElement('e', dye, click -> {
                playerData.setSelectedColor(getDyeColor(dye.getType()));
                CreateBannerGUI.show(player);
                return true;
            }));
        }
        createBannerGUI.addElement(dyeGroup);
        DyeColor selectedColor = playerData.getSelectedColor();
        // Pattern preview logic
        final ItemStack baseBannerForPreview;
        final DyeColor selectedColorForPreview;
        if (playerData.isInSimplePreviewMode()) {
            baseBannerForPreview = new ItemBuilder(Material.WHITE_BANNER).build();
            selectedColorForPreview = DyeColor.BLACK;
        } else {
            baseBannerForPreview = new ItemBuilder(DyeColorRegistry.getBannerMaterial(currentBanner.getType())).build();
            selectedColorForPreview = selectedColor;
        }
        ItemStack previewDye = new ItemBuilder(getDyeMaterial(selectedColor))
            .name(tl(NamedTextColor.BLUE, "gui.selected-pattern-color"))
            .addLore(Component.text("[", NamedTextColor.YELLOW).append(tl("gui.click.left")).append(Component.text("] ", NamedTextColor.YELLOW)).append(tl(NamedTextColor.GREEN, "gui.toggle-preview-mode"))).build();
        // New Logic
        createBannerGUI.addElement(new StaticGuiElement('s',previewDye, click -> {
            playerData.toggleInSimplePreviewMode();
            CreateBannerGUI.show(player);
            return true;
        }));

        // Pattern selection
        GuiElementGroup patternGroup = new GuiElementGroup('b');
        for (int i = 0; i < 24; i++) {
            int patternIndex = i;
            if (playerData.isShowMorePatterns()) {
                patternIndex += 24;
            }
            if (patternIndex >= BannerUtil.getPatternTypeList().size()) {
                break;
            }
            PatternType patternType = BannerUtil.getPatternTypeList().get(patternIndex);
            ItemStack patternItem = new ItemBuilder(baseBannerForPreview.clone())
                .pattern(new Pattern(selectedColorForPreview, patternType)).build();
            // New logic
            patternGroup.addElement(new StaticGuiElement('e',patternItem, event -> {
                BannerMeta currentBm = (BannerMeta) currentBanner.getItemMeta();
                Objects.requireNonNull(currentBm).addPattern(new Pattern(selectedColor, patternType));
                currentBanner.setItemMeta(currentBm);
                playerData.setCurrentEditBanner(currentBanner);
                CreateBannerGUI.show(player);
                return true;
            }));
        }
        createBannerGUI.addElement(patternGroup);
        // More patterns button
        // Flip to new patterns TODO: Use InventoryGUI Pagination instead
        ItemStack btnMorePattern = new ItemBuilder(Material.NETHER_STAR).name(tl(NamedTextColor.GREEN, "gui.more-patterns")).build();
        // New logic
        createBannerGUI.addElement(new StaticGuiElement('m', btnMorePattern, click -> {
            playerData.setShowMorePatterns(!playerData.isShowMorePatterns()); // TODO: Replace with toggle
            CreateBannerGUI.show(player);
            return true;
        }));
        // Create/save banner
        ItemStack btnCreate = new ItemBuilder(Material.LIME_WOOL).name(tl(NamedTextColor.GREEN, "gui.create")).build();
        // new logic
        createBannerGUI.addElement(new StaticGuiElement('c', btnCreate, click -> {
            BannerRepository bannerRepository = BannerMaker.getInstance().getBannerRepository();
            boolean saved = bannerRepository.saveBanner(player, currentBanner);
            MessageService messageService = BannerMaker.getInstance().getMessageService();
            if (saved) {
                messageService.send(player, tl(NamedTextColor.GREEN, "io.save-success"));
            } else {
                messageService.send(player, tl(NamedTextColor.RED, "io.save-failed"));
            }
            playerData.setCurrentEditBanner(null);
            MainMenuGUI.show(player);
            return true;
        }));

        // Delete currently editing banner
        ItemStack btnDelete = new ItemBuilder(Material.BARRIER).name(tl(NamedTextColor.RED, "gui.delete")).build();
        // New logic
        createBannerGUI.addElement(new StaticGuiElement('d', btnDelete, click -> {
            playerData.setCurrentEditBanner(null);
            CreateBannerGUI.show(player);
            return true;
        }));
        // Remove last pattern
        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 0) {
            ItemStack btnRemovePattern = new ItemBuilder(Material.BARRIER).name(tl(NamedTextColor.RED, "gui.remove-last-pattern")).build();
            createBannerGUI.addElement(new StaticGuiElement('u', btnRemovePattern, click -> {
                BannerMeta bm = (BannerMeta) currentBanner.getItemMeta();
                bm.removePattern(bm.numberOfPatterns() - 1);
                currentBanner.setItemMeta(bm);
                playerData.setCurrentEditBanner(currentBanner);
                CreateBannerGUI.show(player);
                return true;
            }));
        }
        createBannerGUI.show(player);
    }
}
