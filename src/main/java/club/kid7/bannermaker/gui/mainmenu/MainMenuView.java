package club.kid7.bannermaker.gui.mainmenu;

import club.kid7.bannermaker.gui.common.AlphanumericalSelector;
import club.kid7.bannermaker.gui.common.NextPageButton;
import club.kid7.bannermaker.gui.common.PreviousPageButton;
import de.themoep.inventorygui.GuiElement.Action;
import de.themoep.inventorygui.InventoryGui;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.Consumer;

import static club.kid7.bannermaker.configuration.Language.tl;

public class MainMenuView extends InventoryGui {
    private final static String[] MENU_DISPLAY_PATTERN = {
        "bbbbbbbbb",
        "bbbbbbbbb",
        "bbbbbbbbb",
        "bbbbbbbbb",
        "bbbbbbbbb",
        "p   c a n"
    };

    private final HumanEntity player;

    public MainMenuView(Plugin plugin, HumanEntity player) {
        super(
            plugin,
            player,
            LegacyComponentSerializer.legacySection().serialize(
                tl("gui.title.prefix").append(tl("gui.title.main-menu"))
            ),
            MENU_DISPLAY_PATTERN);
        this.player = player;
    }

    public void initializeView(
        List<ItemStack> savedBanners,
        Action createBannerCallback,
        Action alphanumericalCallback,
        Consumer<ItemStack> loadBannerCallback) {
        this.addElements(
            new BannerGroup(savedBanners, loadBannerCallback),
            new CreateBannerButton(createBannerCallback),
            new PreviousPageButton(),
            new NextPageButton(),
            new AlphanumericalSelector(alphanumericalCallback)
        );
        this.show(player);
    }
}
