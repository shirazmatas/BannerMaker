package club.kid7.bannermaker.service;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static club.kid7.bannermaker.configuration.Language.tl;

public class MessageService {

    // 傳統顏色代碼 '&' 序列化器，用於兼容舊版訊息或確保特定情況下的處理
    // AI Translated: Traditional color code '&' serializer, used for compatibility with old messages or to ensure processing in specific cases
    private final LegacyComponentSerializer legacySerializer;
    private BukkitAudiences audiences;

    /**
     * 建構 MessageService 實例。
     * AI Translated: Construct MessageService instance.
     *
     * @param plugin JavaPlugin 實例，用於初始化 Adventure Audiences。
     * AI Translated: JavaPlugin instance, used to initialize Adventure Audiences.
     */
    public MessageService(JavaPlugin plugin) {
        // 配置 LegacyComponentSerializer 支援 '&' 符號
        // AI Translated: Configure LegacyComponentSerializer to support '&' symbol
        this.legacySerializer = LegacyComponentSerializer.builder().character('&').build();
        // 在建構時就初始化 audiences
        // AI Translated: Initialize audiences upon construction
        this.audiences = BukkitAudiences.create(plugin);
    }

    /**
     * 關閉 Adventure Audiences。應在插件 onDisable 時呼叫。
     * AI Translated: Close Adventure Audiences. Should be called when the plugin is onDisable.
     */
    public void closeAudiences() {
        if (this.audiences != null) {
            this.audiences.close();
            this.audiences = null;
        }
    }

    /**
     * 將 Adventure Component 發送給 CommandSender，自動加上語系檔中的 prefix。
     * AI Translated: Send Adventure Component to CommandSender, automatically adding the prefix from the language file.
     * 對 Player 使用 JSON 序列化 + Spigot API，確保 ClickEvent / HoverEvent 正確保留。
     * AI Translated: Use JSON serialization + Spigot API for Players to ensure ClickEvent / HoverEvent are correctly preserved.
     * Workaround: adventure-platform-bukkit 4.4.1 僅支援至 MC 1.21.6，
     * AI Translated: Workaround: adventure-platform-bukkit 4.4.1 only supports up to MC 1.21.6,
     * 在 Paper 1.21.7+ 上透過 BukkitAudiences 發送時會遺失互動事件。
     * AI Translated: interactive events are lost when sending via BukkitAudiences on Paper 1.21.7+.
     *
     * @param sender  訊息接收者。
     * AI Translated: Message recipient.
     * @param message 訊息 Component。
     * AI Translated: Message Component.
     */
    public void send(CommandSender sender, Component message) {
        Component prefixed = tl("general.prefix").append(message);
        // Workaround: adventure-platform-bukkit 4.4.1 在 Paper 1.21.7+ 會遺失 ClickEvent / HoverEvent，
        // AI Translated: Workaround: adventure-platform-bukkit 4.4.1 will lose ClickEvent / HoverEvent on Paper 1.21.7+,
        // 改用 JSON 中介 + Spigot API 繞過此問題
        // AI Translated: switch to JSON intermediary + Spigot API to bypass this issue
        if (sender instanceof Player player) {
            try {
                String json = GsonComponentSerializer.gson().serialize(prefixed);
                BaseComponent[] components = ComponentSerializer.parse(json);
                player.spigot().sendMessage(components);
                return;
            } catch (Exception ignored) {
                // JSON 序列化失敗時回退至 BukkitAudiences
                // AI Translated: Roll back to BukkitAudiences when JSON serialization fails
            }
        }
        if (audiences == null) {
            sender.sendMessage(legacySerializer.serialize(prefixed));
            return;
        }
        audiences.sender(sender).sendMessage(prefixed);
    }

    /**
     * 將 '&' 顏色代碼格式的訊息發送給 CommandSender，自動加上語系檔中的 prefix。
     * AI Translated: Send messages in '&' color code format to CommandSender, automatically adding the prefix from the language file.
     *
     * @param sender  訊息接收者。
     * AI Translated: Message recipient.
     * @param message 訊息字串，支援 '&' 顏色格式。
     * AI Translated: Message string, supports '&' color format.
     */
    public void send(CommandSender sender, String message) {
        send(sender, legacySerializer.deserialize(message));
    }

    /**
     * 將 MiniMessage 或 '&' 顏色代碼格式化為傳統的 '§' 顏色字串。
     * AI Translated: Format MiniMessage or '&' color codes into traditional '§' color strings.
     * 用於兼容舊版 API 或不支援 Component 的地方。
     * AI Translated: Used for compatibility with old version APIs or places that do not support Components.
     *
     * @param message 訊息字串。
     * AI Translated: Message string.
     * @return 帶有 '§' 顏色代碼的字串。
     * AI Translated: String with '§' color codes.
     */
    public String formatToString(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
