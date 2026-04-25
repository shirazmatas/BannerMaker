package club.kid7.bannermaker.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class TagUtil {

    /**
     * 創建一個鍵值對 TagResolver。
     * AI Translated: Create a key-value pair TagResolver.
     * 如果 value 是 Component，使用 Placeholder.component (不會被轉義)。
     * AI Translated: If the value is a Component, use Placeholder.component (will not be escaped).
     * 否則，使用 Placeholder.unparsed (會被轉義，防止注入)。
     * AI Translated: Otherwise, use Placeholder.unparsed (will be escaped, preventing injection).
     *
     * @param key   標籤名稱 (不含 <>)
     * AI Translated: Tag name (without <>)
     * @param value 標籤值
     * AI Translated: Tag value
     * @return TagResolver
     */
    public static TagResolver tag(String key, Object value) {
        if (value instanceof Component) {
            return Placeholder.component(key, (Component) value);
        }
        return Placeholder.unparsed(key, String.valueOf(value));
    }
}
