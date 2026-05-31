package club.kid7.bannermaker.gui.mainmenu;

import club.kid7.bannermaker.gui.common.SlotType;
import club.kid7.bannermaker.util.ItemBuilder;
import de.themoep.inventorygui.StaticGuiElement;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import static club.kid7.bannermaker.configuration.Language.tl;

public class CreateBannerButton extends StaticGuiElement {
    public CreateBannerButton(Action action) {
        super(
            SlotType.CREATE.getSlotLetter(),
            new ItemBuilder(Material.LIME_WOOL)
                .name(tl(NamedTextColor.GREEN, "gui.create-banner"))
                .build(),
            action
        );
    }
}
