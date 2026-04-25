package club.kid7.bannermaker.configuration;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.util.TagUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LanguageTest {

    private ServerMock server;
    private BannerMaker plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(BannerMaker.class);

        // 強制設定使用英文，避免 Locale.getDefault() 導致測試環境不一致
        // AI Translated: Force setting to use English to avoid inconsistent test environments caused by Locale.getDefault()
        ConfigManager.get("config.yml").set("Language", "en_US");

        // 初始化語言系統 (這會讀取 config.yml)
        // AI Translated: Initialize language system (this will read config.yml)
        new Language(plugin).loadLanguage();
    }

    @AfterEach
    void tearDown() {
        ConfigManager.reset();
        MockBukkit.unmock();
    }

    /**
     * 輔助方法：設定測試用的語言鍵值對
     * AI Translated: Helper method: set language key-value pairs for testing
     */
    private void setLanguageKey(String key, String value) {
        // 直接操作 ConfigManager 管理的當前語言設定檔
        // AI Translated: Directly operate on the current language configuration file managed by ConfigManager
        // 預設是英文，所以是 language/en_US.yml
        // AI Translated: Default is English, so it's language/en_US.yml
        FileConfiguration config = ConfigManager.get("language" + File.separator + "en_US.yml");
        config.set(key, value);
    }

    @Test
    void testLegacyColor() {
        String key = "test.legacy";
        String value = "&cRed Text";
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        // 驗證顏色是否正確解析為紅色
        // AI Translated: Verify if the color is correctly parsed as red
        assertEquals(NamedTextColor.RED, result.color(), "應該解析為紅色");
        // AI Translated: Should be parsed as red
        assertEquals("Red Text", PlainTextComponentSerializer.plainText().serialize(result), "文字內容應該正確");
        // AI Translated: Text content should be correct
    }

    @Test
    void testMiniMessage() {
        String key = "test.minimessage";
        String value = "<red>Red Text</red>"; // MiniMessage 標籤應閉合
        // AI Translated: MiniMessage tags should be closed
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        // 驗證顏色是否正確解析為紅色
        // AI Translated: Verify if the color is correctly parsed as red
        assertEquals(NamedTextColor.RED, result.color(), "應該解析為紅色");
        // AI Translated: Should be parsed as red
        assertEquals("Red Text", PlainTextComponentSerializer.plainText().serialize(result), "文字內容應該正確");
        // AI Translated: Text content should be correct
    }

    @Test
    void testMixedContent() {
        // & 碼會被預處理為 MiniMessage 標籤，兩種格式可共存
        // AI Translated: & codes will be pre-processed as MiniMessage tags, both formats can coexist
        String key = "test.mixed";
        String value = "<green>Green &cText</green>";
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        assertEquals(NamedTextColor.GREEN, result.color(), "開頭應該是綠色");
        // AI Translated: The beginning should be green
        String plainText = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Green Text", plainText, "&c 應該被轉換為顏色而非純文字");
        // AI Translated: &c should be converted to color instead of plain text
        // 子組件應該包含紅色
        // AI Translated: Subcomponents should contain red
        assertTrue(result.children().stream()
            .anyMatch(child -> NamedTextColor.RED.equals(child.color())), "子組件應該包含紅色");
        // AI Translated: Subcomponents should contain red
    }

    @Test
    void testNoColor() {
        String key = "test.plain";
        String value = "Plain Text";
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        assertEquals("Plain Text", PlainTextComponentSerializer.plainText().serialize(result));
    }

    @Test
    void testComplexMiniMessage() {
        String key = "test.complex_mm";
        String value = "<gradient:red:blue>Hello <yellow>World</yellow>!</gradient>";
        setLanguageKey(key, value);

        Component result = Language.tl(key);
        // 驗證是否包含漸層和黃色 (MiniMessage 特性)
        // AI Translated: Verify if it contains gradient and yellow (MiniMessage feature)
        String plainText = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Hello World!", plainText); // 漸層和顏色在純文本中不顯示
        // AI Translated: Gradients and colors are not displayed in plain text
        // 要精確測試漸層需要更深入的 Adventure API 檢查
        // AI Translated: To precisely test gradients requires deeper Adventure API inspection
        // 這裡只簡單驗證主體文字和部分顏色
        // AI Translated: Here we only simply verify the main text and some colors
        // 檢查子組件是否有黃色
        // AI Translated: Check if subcomponents have yellow
        assertTrue(result.children().stream()
            .anyMatch(child -> NamedTextColor.YELLOW.equals(child.color())), "子組件應該包含黃色");
        // AI Translated: Subcomponents should contain yellow
    }

    @Test
    void testComplexLegacy() {
        String key = "test.complex_legacy";
        String value = "&cHello &bWorld &a!"; // 多個 Legacy 顏色
        // AI Translated: Multiple Legacy colors
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        // 驗證純文字內容
        // AI Translated: Verify plain text content
        assertEquals("Hello World !", PlainTextComponentSerializer.plainText().serialize(result));

        // 檢查 Component 再次序列化回 Legacy 格式後的表現
        // AI Translated: Check the performance of the Component after being serialized back into Legacy format
        String serializedLegacy = LegacyComponentSerializer.legacyAmpersand().serialize(result);
        assertTrue(serializedLegacy.startsWith("&cHello"), "Legacy 序列化後應該以紅色 Hello 開頭");
        // AI Translated: After Legacy serialization, it should start with red Hello
        assertTrue(serializedLegacy.contains("&bWorld"), "Legacy 序列化後應該包含水藍色 World");
        // AI Translated: After Legacy serialization, it should contain aqua World
        assertTrue(serializedLegacy.contains("&a!"), "Legacy 序列化後應該包含綠色 !");
        // AI Translated: After Legacy serialization, it should contain green !
    }

    @Test
    void testLegacyWithMiniMessageInside() {
        // & 碼和 MiniMessage 標籤共存，& 碼會被預處理為 MiniMessage 標籤
        // AI Translated: & codes and MiniMessage tags coexist, & codes will be pre-processed as MiniMessage tags
        String key = "test.legacy_with_mm";
        String value = "&cHello <red>World</red>";
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        // &c 會被預處理為 <red>，所以 Hello 也是紅色
        // AI Translated: &c will be pre-processed as <red>, so Hello is also red
        assertEquals(NamedTextColor.RED, result.color(), "開頭應該是紅色");
        // AI Translated: The beginning should be red
        String plainText = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Hello World", plainText, "&c 應該被轉換為顏色");
        // AI Translated: &c should be converted to color
    }

    @Test
    void testMiniMessageWithLegacyInside() {
        // MiniMessage 格式 <red>Hello &bWorld</red>
        // AI Translated: MiniMessage format <red>Hello &bWorld</red>
        // &b 會被預處理為 <aqua>
        // AI Translated: &b will be pre-processed as <aqua>
        String key = "test.mm_with_legacy";
        String value = "<red>Hello &bWorld</red>";
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        assertEquals(NamedTextColor.RED, result.color(), "開頭應該是紅色");
        // AI Translated: The beginning should be red
        String plainText = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Hello World", plainText, "&b 應該被轉換為顏色而非純文字");
        // AI Translated: &b should be converted to color instead of plain text
        // 子組件應該包含水藍色
        // AI Translated: Subcomponents should contain aqua
        assertTrue(result.children().stream()
            .anyMatch(child -> NamedTextColor.AQUA.equals(child.color())), "子組件應該包含水藍色");
        // AI Translated: Subcomponents should contain aqua
    }

    @Test
    void testTagResolver() {
        String key = "test.tag_resolver";
        String value = "Hello <name>!";
        setLanguageKey(key, value);

        Component result = Language.tl(key, TagUtil.tag("name", "World"));

        String plainText = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Hello World!", plainText, "命名佔位符應該被正確替換");
        // AI Translated: Named placeholders should be correctly replaced
    }

    @Test
    void testTagResolverWithLegacyColor() {
        // 模擬語言檔中的實際格式，如 get-banner: 取得旗幟 &r#<name>
        // AI Translated: Simulate actual format in language file, such as get-banner: get banner &r#<name>
        String key = "test.tag_resolver_legacy";
        String value = "&aGet banner &r#<name>";
        setLanguageKey(key, value);

        Component result = Language.tl(key, TagUtil.tag("name", "TestBanner"));

        // 驗證純文字內容：& 碼被處理為顏色、佔位符被替換
        // Validate plain text content: & symbols are processed as colors, placeholders are replaced
        String plainText = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Get banner #TestBanner", plainText, "& 碼和命名佔位符應該共存");
        // AI Translated: & codes and named placeholders should coexist
        // 驗證 &a 被正確轉換為綠色（透過 Legacy 反向序列化確認）
        // AI Translated: Verify &a is correctly converted to green (confirmed through Legacy reverse serialization)
        String legacySerialized = LegacyComponentSerializer.legacyAmpersand().serialize(result);
        assertTrue(legacySerialized.contains("&aGet banner"), "應該包含綠色的 Get banner");
        // AI Translated: Should contain green Get banner
    }
}
