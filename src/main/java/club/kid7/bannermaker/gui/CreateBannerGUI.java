package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.gui.mainmenu.MainMenu;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.service.BannerRepository;
import club.kid7.bannermaker.service.MessageService;
import club.kid7.bannermaker.util.BannerUtil;
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
        // Old Logic
//        ChestGui gui = new ChestGui(6, title);

        // Retrieve current banner being edited
        ItemStack currentBanner = playerData.getCurrentEditBanner();
        // new logic
        InventoryGui createBannerGUI = new InventoryGui(BannerMaker.getInstance(), player, title, GUI_DESIGN_MODE);

//        gui.setOnGlobalClick(event -> event.setCancelled(true));

//        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
//        gui.addPane(mainPane);
        // Back button -> p
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(tl(NamedTextColor.RED, "gui.back")).build();
//        mainPane.addItem(new GuiItem(btnBackToMenu, event -> {
//            MainMenuGUI.show(player);
//            event.setCancelled(true);
//        }), 0, 5);
        createBannerGUI.addElement('p', btnBackToMenu, click -> {
            MainMenu.show(player);
            return true;
        });

        if (currentBanner == null) { // If no previous banner we are in setup phase
            // choose base color (using the same layout logic as color selection)
            GuiElementGroup colorGroup = new GuiElementGroup('d');
            for (int i = 0; i < 16; i++) {
                final int colorIndex = i;
                ItemStack banner = new ItemBuilder(DyeColorRegistry.getBannerMaterial(colorIndex)).build();
                // 舊邏輯: i + 1 + (i / 8) -> Slot 1-8, 10-17
                // AI Translated: Old logic: i + 1 + (i / 8) -> Slot 1-8, 10-17
//                int slot = i + 1 + (i / 8);
//                mainPane.addItem(new GuiItem(banner, event -> {
//                    playerData.setCurrentEditBanner(banner);
//                    CreateBannerGUI.show(player); // 重新開啟以進入編輯模式
//                    // AI Translated: Reopen to enter edit mode
//                    event.setCancelled(true);
//                }), slot % 9, slot / 9);
                colorGroup.addElement(new StaticGuiElement('e', banner, click -> {
                    playerData.setCurrentEditBanner(banner);
                    CreateBannerGUI.show(player);
                    return true;
                }));
            }
            createBannerGUI.addElement(colorGroup);
//            gui.show(player);
            createBannerGUI.show(player);
            return;
        }

        // --- Edit Mode ---
        // Current banner being edited = x
//        mainPane.addItem(new GuiItem(currentBanner), 0, 0);
        createBannerGUI.addElement(new StaticGuiElement('x', currentBanner));
        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 6) {
            ItemStack warning = new ItemBuilder(Material.OAK_SIGN)
                .name(tl(NamedTextColor.RED, "gui.uncraftable-warning"))
                .lore(tl("gui.more-than-6-patterns")).build();
//            mainPane.addItem(new GuiItem(warning), 0, 1); // 修正為 (0, 1)
            createBannerGUI.addElement(new StaticGuiElement('w', warning));
        }

        // Color selectors = d
        // AI Translated: Color selection (i=0-15)
        // 放置在 Slot 1-8 (Row 0) 和 Slot 10-17 (Row 1)
        // AI Translated: Placed in Slot 1-8 (Row 0) and Slot 10-17 (Row 1)
        GuiElementGroup dyeGroup = new GuiElementGroup('d');
        for (int colorIndex = 0; colorIndex < 16; colorIndex++) {
            ItemStack dye = new ItemBuilder(getDyeMaterial(getDyeColor(colorIndex))).build();
            // OLD LOGIC
//            int slot = colorIndex + 1 + (colorIndex / 8);
//            mainPane.addItem(new GuiItem(dye, event -> {
//                playerData.setSelectedColor(getDyeColor(dye.getType()));
//                CreateBannerGUI.show(player); // 重新開啟以刷新圖案
//                // AI Translated: Reopen to refresh patterns
//                event.setCancelled(true);
//            }), slot % 9, slot / 9);

            // New Logic
            dyeGroup.addElement(new StaticGuiElement('e', dye, click -> {
                playerData.setSelectedColor(getDyeColor(dye.getType()));
                CreateBannerGUI.show(player);
                return true;
            }));
        }
        createBannerGUI.addElement(dyeGroup);


        // AI Translated: Slot 18 (0,2): Selected color and preview mode toggle
        // Selected color and toggle preview mode.
        DyeColor selectedColor = playerData.getSelectedColor();
        // AI Translated: Pattern preview logic
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
        // Old Logic
//        mainPane.addItem(new GuiItem(previewDye, event -> {
//            playerData.toggleInSimplePreviewMode();
//            CreateBannerGUI.show(player); // 重新開啟以刷新圖案
//            // AI Translated: Reopen to refresh patterns
//            event.setCancelled(true);
//        }), 0, 2); // 修正為 (0, 2)
        // New Logic
        createBannerGUI.addElement(new StaticGuiElement('s',previewDye, click -> {
            playerData.toggleInSimplePreviewMode();
            CreateBannerGUI.show(player);
            return true;
        }));


        // 圖案選擇 (i=0-23)
        // AI Translated: Pattern selection (i=0-23)
        // 放置在 Slot 19-26 (Row 2), 28-35 (Row 3), 37-44 (Row 4)
        // AI Translated: Placed in Slot 19-26 (Row 2), 28-35 (Row 3), 37-44 (Row 4)
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

            // 舊邏輯: i + 19 + (i / 8)
            // AI Translated: Old logic: i + 19 + (i / 8)
            // Old logic
//            int slot = i + 19 + (i / 8);
//            mainPane.addItem(new GuiItem(patternItem, event -> {
//                BannerMeta currentBm = (BannerMeta) currentBanner.getItemMeta();
//                Objects.requireNonNull(currentBm).addPattern(new Pattern(selectedColor, patternType));
//                currentBanner.setItemMeta(currentBm);
//                playerData.setCurrentEditBanner(currentBanner);
//                CreateBannerGUI.show(player); // 重新開啟以反映變更
//                // AI Translated: Reopen to reflect changes
//                event.setCancelled(true);
//            }), slot % 9, slot / 9);
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


        // Slot 51 (6,5): 更多圖案按鈕
        // AI Translated: Slot 51 (6,5): More patterns button
        // Flip to new patterns TODO: Use InventoryGUI Pagination instead
        ItemStack btnMorePattern = new ItemBuilder(Material.NETHER_STAR).name(tl(NamedTextColor.GREEN, "gui.more-patterns")).build();
//        mainPane.addItem(new GuiItem(btnMorePattern, event -> {
//            playerData.setShowMorePatterns(!playerData.isShowMorePatterns());
//            CreateBannerGUI.show(player); // 重新開啟以顯示更多圖案
//            // AI Translated: Reopen to show more patterns
//            event.setCancelled(true);
//        }), 6, 5);
        // New logic
        createBannerGUI.addElement(new StaticGuiElement('m', btnMorePattern, click -> {
            playerData.setShowMorePatterns(!playerData.isShowMorePatterns()); // TODO: Replace with toggle
            CreateBannerGUI.show(player);
            return true;
        }));
        // Slot 53 (8,5): 建立/儲存旗幟
        // AI Translated: Slot 53 (8,5): Create/save banner
        ItemStack btnCreate = new ItemBuilder(Material.LIME_WOOL).name(tl(NamedTextColor.GREEN, "gui.create")).build();
//        mainPane.addItem(new GuiItem(btnCreate, event -> {
//            BannerRepository bannerRepository = BannerMaker.getInstance().getBannerRepository();
//            boolean saved = bannerRepository.saveBanner(player, currentBanner);
//            MessageService messageService = BannerMaker.getInstance().getMessageService();
//            if (saved) {
//                messageService.send(player, tl(NamedTextColor.GREEN, "io.save-success"));
//            } else {
//                messageService.send(player, tl(NamedTextColor.RED, "io.save-failed"));
//            }
//            playerData.setCurrentEditBanner(null);
//            MainMenuGUI.show(player); // 返回主選單
//            // AI Translated: Return to main menu
//            event.setCancelled(true);
//        }), 8, 5);
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
            MainMenu.show(player);
            return true;
        }));

        // Slot 47 (2,5): 刪除當前編輯旗幟
        // AI Translated: Slot 47 (2,5): Delete currently editing banner
        ItemStack btnDelete = new ItemBuilder(Material.BARRIER).name(tl(NamedTextColor.RED, "gui.delete")).build();
//        mainPane.addItem(new GuiItem(btnDelete, event -> {
//            playerData.setCurrentEditBanner(null);
//            CreateBannerGUI.show(player); // 重新開啟以回到底色選擇
//            // AI Translated: Reopen to go back to base color selection
//            event.setCancelled(true);
//        }), 2, 5); // 修正為 (2, 5)
        // New logic
        createBannerGUI.addElement(new StaticGuiElement('d', btnDelete, click -> {
            playerData.setCurrentEditBanner(null);
            CreateBannerGUI.show(player);
            return true;
        }));

        // Slot 49 (4,5): 移除上一個圖案
        // AI Translated: Slot 49 (4,5): Remove last pattern
        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 0) {
            ItemStack btnRemovePattern = new ItemBuilder(Material.BARRIER).name(tl(NamedTextColor.RED, "gui.remove-last-pattern")).build();
//            mainPane.addItem(new GuiItem(btnRemovePattern, event -> {
//                BannerMeta bm = (BannerMeta) currentBanner.getItemMeta();
//                bm.removePattern(bm.numberOfPatterns() - 1);
//                currentBanner.setItemMeta(bm);
//                playerData.setCurrentEditBanner(currentBanner);
//                CreateBannerGUI.show(player); // 重新開啟以反映變更
//                // AI Translated: Reopen to reflect changes
//                event.setCancelled(true);
//            }), 4, 5); // 修正為 (4, 5)
            createBannerGUI.addElement(new StaticGuiElement('u', btnRemovePattern, click -> {
                BannerMeta bm = (BannerMeta) currentBanner.getItemMeta();
                bm.removePattern(bm.numberOfPatterns() - 1);
                currentBanner.setItemMeta(bm);
                playerData.setCurrentEditBanner(currentBanner);
                CreateBannerGUI.show(player);
                return true;
            }));
            // AI Translated: Corrected to (4, 5)
        }

//        gui.show(player);
        createBannerGUI.show(player);
    }
}
