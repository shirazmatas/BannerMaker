package club.kid7.bannermaker.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.inventory.ItemStack;

// 訊息組件工具類，用於將 Bukkit 物品轉換為 Adventure 的文本組件和懸停事件
// AI Translated: Message component utility class, used to convert Bukkit items to Adventure text components and hover events
public class MessageComponentUtil {

    // 獲取物品的可翻譯組件
    // AI Translated: Get the translatable component of the item
    // 透過物品的本地化鍵 (translation key) 來創建一個可翻譯的文本組件
    // AI Translated: Create a translatable text component through the item's localization key (translation key)
    public static TranslatableComponent getTranslatableComponent(ItemStack itemStack) {
        return Component.translatable(itemStack.translationKey());
    }

    // 獲取物品的懸停事件
    // AI Translated: Get the hover event of the item
    // 暫時僅使用物品類型和數量，不包含 NBT 數據 (如附魔、名稱等)
    // AI Translated: Currently only using item type and amount, not containing NBT data (such as enchantments, names, etc.)
    // TODO: 解決 BukkitAdapter 依賴問題後，恢復完整的 NBT 支援。
    // AI Translated: TODO: After resolving the BukkitAdapter dependency issue, restore complete NBT support.
    public static HoverEvent<HoverEvent.ShowItem> getHoverEventItem(ItemStack itemStack) {
        return itemStack.asHoverEvent();
    }
}
