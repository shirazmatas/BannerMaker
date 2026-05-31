package club.kid7.bannermaker.gui.common;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.util.ItemBuilder;
import de.themoep.inventorygui.StaticGuiElement;
import net.kyori.adventure.text.format.NamedTextColor;

import static club.kid7.bannermaker.configuration.Language.tl;

public class AlphanumericalSelector extends StaticGuiElement {
    public AlphanumericalSelector(Action action) {
        super(
            SlotType.ALPHA_NUMERIC.getSlotLetter(),
            new ItemBuilder(AlphabetBanner.get("A"))
                .name(tl(NamedTextColor.GREEN, "gui.alphabet-and-number"))
                .build(),
            action
        );
    }
}
