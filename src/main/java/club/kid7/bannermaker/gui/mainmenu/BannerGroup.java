package club.kid7.bannermaker.gui.mainmenu;

import club.kid7.bannermaker.gui.common.SlotType;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BannerGroup extends GuiElementGroup implements Listener {
    private final Consumer<ItemStack> loadBannerDetails;

    public BannerGroup(List<ItemStack> savedBanners, Consumer<ItemStack> loadBannerCallback) {
        super(SlotType.LISTED_ELEMENT.getSlotLetter());
        List<GuiElement> banners = savedBanners.stream()
            .map(Banner::new)
            .collect(Collectors.toList());
        this.addElements(banners);
        this.loadBannerDetails = loadBannerCallback;
    }

    @EventHandler
    public void onBannerClicked(BannerClickedEvent event) {
        loadBannerDetails.accept(event.getBanner());
    }
}
