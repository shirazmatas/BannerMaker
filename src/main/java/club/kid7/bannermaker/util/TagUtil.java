package club.kid7.bannermaker.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class TagUtil {

    /**
     * Creates a key-value pair TagResolver.
     * If the value is a Component, use Placeholder.component (will not be escaped).
     * Otherwise, use Placeholder.unparsed (will be escaped, preventing injection).
     *
     * @param key   Tag name (without <>)
     * @param value Tag value
     * @return TagResolver
     */
    public static TagResolver tag(String key, Object value) {
        if (value instanceof Component) {
            return Placeholder.component(key, (Component) value);
        }
        return Placeholder.unparsed(key, String.valueOf(value));
    }
}
