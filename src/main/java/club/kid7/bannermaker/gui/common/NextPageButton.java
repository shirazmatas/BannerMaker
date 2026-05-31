package club.kid7.bannermaker.gui.common;

import de.themoep.inventorygui.GuiPageElement;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NextPageButton extends GuiPageElement {
    public NextPageButton() {
        super(
            SlotType.NEXT_PAGE.getSlotLetter(),
            new ItemStack(Material.ARROW),
            PageAction.NEXT,
            "Go to next page (%nextpage%)" //TODO: extract in locales if possible
        );
    }
}
