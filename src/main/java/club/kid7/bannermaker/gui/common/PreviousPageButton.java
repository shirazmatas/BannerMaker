package club.kid7.bannermaker.gui.common;

import de.themoep.inventorygui.GuiPageElement;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PreviousPageButton extends GuiPageElement {

    public PreviousPageButton() {
        super(
            SlotType.PREVIOUS_PAGE.getSlotLetter(),
            new ItemStack(Material.ARROW),
            PageAction.NEXT,
            "Go to previous page (%prevpage%)" //TODO: extract in locales if possible
        );
    }
}
