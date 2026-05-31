package club.kid7.bannermaker.gui.mainmenu;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class BannerClickedEvent extends Event {
    private final ItemStack banner;
    private static final HandlerList handlers = new HandlerList();

    public BannerClickedEvent(ItemStack banner) {
        this.banner = banner;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ItemStack getBanner() {
        return banner;
    }
}
