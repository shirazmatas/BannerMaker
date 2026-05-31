package club.kid7.bannermaker.gui.mainmenu;

import club.kid7.bannermaker.gui.common.SlotType;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class Banner extends StaticGuiElement {
    public Banner(ItemStack banner) {
        super(
            SlotType.LISTED_ELEMENT.getSlotLetter(),
            banner,
            event -> {
                Event bannerClickedEvent = new BannerClickedEvent(banner);
                Bukkit.getServer().getPluginManager().callEvent(bannerClickedEvent);
                return true;
            }
        );
    }
}
