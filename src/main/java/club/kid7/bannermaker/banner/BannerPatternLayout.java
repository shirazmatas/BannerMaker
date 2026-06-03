package club.kid7.bannermaker.banner;

import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.util.BannerUtil;
import com.google.common.collect.Maps;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Banner Pattern Layout: Responsible for generating 3x3 recipe grids for GUI display,
 * determining if a recipe is a loom recipe, and listing available PatternTypes.
 * <p>
 * Note: The generated 3x3 grid is for pre-1.14 vanilla crafting recipe layouts.
 * **Modern vanilla has switched to using the loom**;
 * This class is only for visual representation in BannerInfoGUI and is not a real recipe executable in a crafting table.
 * Extracted from {@link BannerUtil} to follow the Single Responsibility Principle.
 */
public class BannerPatternLayout {

    private BannerPatternLayout() {
        // Utility class
    }

    /**
     * Used for the 3x3 recipe display in {@link #getPatternRecipe(ItemStack, int)}.
     * Accumulated during contributor execution: which slot the banner is placed in,
     * which slots the dyes are in, and extra items (like CREEPER_HEAD) are written directly into the recipe map.
     */
    private static final class RecipeContext {
        private final DyeColor color;
        private final HashMap<Integer, ItemStack> recipe;
        int bannerSlot = 4;
        List<Integer> dyeSlots = Collections.emptyList();

        RecipeContext(DyeColor color, HashMap<Integer, ItemStack> recipe) {
            this.color = color;
            this.recipe = recipe;
        }

        RecipeContext bannerAt(int slot) {
            this.bannerSlot = slot;
            return this;
        }

        RecipeContext dyeAt(Integer... slots) {
            this.dyeSlots = Arrays.asList(slots);
            return this;
        }

        RecipeContext putAt(int slot, Material material) {
            recipe.put(slot, new ItemStack(material));
            return this;
        }

        /** Extra dye is only needed if the color is not black; BLACK is already the base and doesn't need additional color. */
        RecipeContext dyeAtIfNotBlack(int slot) {
            if (color != DyeColor.BLACK) {
                this.dyeSlots = Collections.singletonList(slot);
            }
            return this;
        }
    }

    @FunctionalInterface
    private interface PatternRecipeContributor {
        void contribute(RecipeContext ctx);
    }

    /** Banner in default position (4), pure dye placed in specified slots. */
    private static PatternRecipeContributor dyes(Integer... slots) {
        return ctx -> ctx.dyeAt(slots);
    }

    /** Banner in specified position, dyes in subsequent slots. */
    private static PatternRecipeContributor bannerAndDyes(int bannerSlot, Integer... dyeSlots) {
        return ctx -> ctx.bannerAt(bannerSlot).dyeAt(dyeSlots);
    }

    /** Special item in slot 1, additional dye in slot 7 if not black. */
    private static PatternRecipeContributor specialItem(Material item) {
        return ctx -> ctx.putAt(1, item).dyeAtIfNotBlack(7);
    }

    /** Loom pattern item in slot 7, dye in slot 5. */
    private static PatternRecipeContributor loomPatternItem(Material item) {
        return ctx -> ctx.putAt(7, item).dyeAt(5);
    }

    /** BRICKS version sensitive: 1.21.2+ uses FIELD_MASONED_BANNER_PATTERN, otherwise BRICK; others same as specialItem. */
    private static PatternRecipeContributor bricksRecipeContributor() {
        return ctx -> {
            Material fieldMasoned = Material.matchMaterial("FIELD_MASONED_BANNER_PATTERN");
            ctx.putAt(1, fieldMasoned != null ? fieldMasoned : Material.BRICK).dyeAtIfNotBlack(7);
        };
    }

    /** PatternType → 3x3 配方位置的查表。 */
    private static final Map<Object, PatternRecipeContributor> RECIPE_CONTRIBUTORS = Map.ofEntries(
        // 1 染料、預設 banner 位置
        Map.entry(PatternType.SQUARE_BOTTOM_LEFT, dyes(6)),
        Map.entry(PatternType.SQUARE_BOTTOM_RIGHT, dyes(8)),
        Map.entry(PatternType.SQUARE_TOP_LEFT, dyes(0)),
        Map.entry(PatternType.SQUARE_TOP_RIGHT, dyes(2)),
        // 3 染料、預設 banner 位置
        Map.entry(PatternType.STRIPE_BOTTOM, dyes(6, 7, 8)),
        Map.entry(PatternType.STRIPE_TOP, dyes(0, 1, 2)),
        Map.entry(PatternType.STRIPE_LEFT, dyes(0, 3, 6)),
        Map.entry(PatternType.STRIPE_RIGHT, dyes(2, 5, 8)),
        Map.entry(PatternType.TRIANGLES_BOTTOM, dyes(3, 5, 7)),
        Map.entry(PatternType.TRIANGLES_TOP, dyes(1, 3, 5)),
        Map.entry(PatternType.DIAGONAL_LEFT, dyes(0, 1, 3)),
        Map.entry(PatternType.DIAGONAL_RIGHT, dyes(1, 2, 5)),
        Map.entry(PatternType.DIAGONAL_UP_LEFT, dyes(3, 6, 7)),
        Map.entry(PatternType.DIAGONAL_UP_RIGHT, dyes(5, 7, 8)),
        Map.entry(PatternType.SMALL_STRIPES, dyes(0, 2, 3, 5)),
        Map.entry(PatternType.RHOMBUS, dyes(1, 3, 5, 7)),
        Map.entry(PatternType.BORDER, dyes(0, 1, 2, 3, 5, 6, 7, 8)),
        // banner 在非預設位置
        Map.entry(PatternType.STRIPE_CENTER, bannerAndDyes(3, 1, 4, 7)),
        Map.entry(PatternType.STRIPE_MIDDLE, bannerAndDyes(1, 3, 4, 5)),
        Map.entry(PatternType.STRIPE_DOWNRIGHT, bannerAndDyes(1, 0, 4, 8)),
        Map.entry(PatternType.STRIPE_DOWNLEFT, bannerAndDyes(1, 2, 4, 6)),
        Map.entry(PatternType.CROSS, bannerAndDyes(1, 0, 2, 4, 6, 8)),
        Map.entry(PatternType.STRAIGHT_CROSS, bannerAndDyes(0, 1, 3, 4, 5, 7)),
        Map.entry(PatternType.TRIANGLE_BOTTOM, bannerAndDyes(7, 4, 6, 8)),
        Map.entry(PatternType.TRIANGLE_TOP, bannerAndDyes(1, 0, 2, 4)),
        Map.entry(PatternType.CIRCLE, bannerAndDyes(1, 4)),
        Map.entry(PatternType.HALF_VERTICAL, bannerAndDyes(5, 0, 1, 3, 4, 6, 7)),
        Map.entry(PatternType.HALF_HORIZONTAL, bannerAndDyes(7, 0, 1, 2, 3, 4, 5)),
        Map.entry(PatternType.HALF_VERTICAL_RIGHT, bannerAndDyes(3, 1, 2, 4, 5, 7, 8)),
        Map.entry(PatternType.HALF_HORIZONTAL_BOTTOM, bannerAndDyes(1, 3, 4, 5, 6, 7, 8)),
        Map.entry(PatternType.GRADIENT, bannerAndDyes(1, 0, 2, 4, 7)),
        Map.entry(PatternType.GRADIENT_UP, bannerAndDyes(7, 1, 4, 6, 8)),
        // 特殊物品 + 條件染料
        Map.entry(PatternType.CURLY_BORDER, specialItem(Material.VINE)),
        Map.entry(PatternType.CREEPER, specialItem(Material.CREEPER_HEAD)),
        Map.entry(PatternType.SKULL, specialItem(Material.WITHER_SKELETON_SKULL)),
        Map.entry(PatternType.FLOWER, specialItem(Material.OXEYE_DAISY)),
        Map.entry(PatternType.MOJANG, specialItem(Material.ENCHANTED_GOLDEN_APPLE)),
        Map.entry(PatternType.BRICKS, bricksRecipeContributor()),
        // Loom 旗幟圖形物品
        Map.entry(PatternType.PIGLIN, loomPatternItem(Material.PIGLIN_BANNER_PATTERN)),
        Map.entry(PatternType.GLOBE, loomPatternItem(Material.GLOBE_BANNER_PATTERN)),
        Map.entry(PatternType.FLOW, loomPatternItem(Material.FLOW_BANNER_PATTERN)),
        Map.entry(PatternType.GUSTER, loomPatternItem(Material.GUSTER_BANNER_PATTERN))
    );

    /**
     * Gets a list of all banner pattern types currently supported by the server (excluding BASE).
     * The results are sorted by namespaced key for use in GUI display and selection.
     *
     * @return List of available PatternTypes
     */
    public static List<PatternType> getPatternTypeList() {
        return Registry.BANNER_PATTERN.stream()
            .sorted(Comparator.comparing(p -> p.getKey().toString()))
            .filter(pattern -> !pattern.getKey().getKey().equals("base"))
            .collect(Collectors.toList());
    }

    /**
     * Determines if the recipe is a loom recipe.
     *
     * @param patternRecipe The recipe to check, produced by getPatternRecipe()
     * @return Whether it is a loom recipe
     */
    public static boolean isLoomRecipe(HashMap<Integer, ItemStack> patternRecipe) {
        for (Map.Entry<Integer, ItemStack> entry : patternRecipe.entrySet()) {
            ItemStack itemStack = entry.getValue();
            // If it contains a banner pattern item, it's considered a loom recipe
            if (BannerUtil.isBannerPatternItemStack(itemStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Constructs a 3x3 recipe icon for display in the GUI for the specified banner's {@code step} pattern layer.
     * Step 0 displays "Material Preparation"; from step 1 onwards, each step displays the banner from the previous layer
     * and the placement of materials required for the current pattern.
     * <p>
     * Note: The 3x3 grid is for pre-1.14 vanilla crafting recipe layouts.
     * **Modern vanilla has switched to using the loom**;
     * This method is only for visual representation in BannerInfoGUI and is not a real recipe executable in a crafting table.
     *
     * @param banner The complete banner (containing all patterns)
     * @param step   Recipe step (1-based); usually 1 ~ number of patterns
     * @return A map of item placements in a 9-slot grid (key is 0–8 slot index)
     */
    public static HashMap<Integer, ItemStack> getPatternRecipe(final ItemStack banner, int step) {
        HashMap<Integer, ItemStack> recipe = Maps.newHashMap();
        // Fill with air
        for (int i = 0; i < 10; i++) {
            recipe.put(i, new ItemStack(Material.AIR));
        }
        // Only process banners
        if (!BannerUtil.isBanner(banner)) {
            return recipe;
        }
        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());
        int totalStep = bm.numberOfPatterns() + 1;
        // Color
        DyeColor baseColor = DyeColorRegistry.getDyeColor(banner.getType());
        if (step == 1) {
            // Step 1: Banner crafting
            // Wool
            ItemStack wool = new ItemStack(DyeColorRegistry.getWoolMaterial(baseColor));
            for (int i = 0; i < 6; i++) {
                recipe.put(i, wool.clone());
            }
            // Stick
            ItemStack stick = new ItemStack(Material.STICK);
            recipe.put(7, stick);
        } else if (step <= totalStep) {
            // Add Pattern
            // Current banner
            ItemStack prevBanner = new ItemStack(DyeColorRegistry.getBannerMaterial(baseColor));
            BannerMeta pbm = (BannerMeta) Objects.requireNonNull(prevBanner.getItemMeta());
            // Add to current Pattern
            for (int i = 0; i < step - 2; i++) {
                pbm.addPattern(bm.getPattern(i));
            }
            prevBanner.setItemMeta(pbm);
            // Current Pattern
            Pattern pattern = bm.getPattern(step - 2);
            // Required dye
            DyeColor dyeColor = pattern.getColor();
            ItemStack dyeItem = DyeColorRegistry.getDyeItemStack(dyeColor, 1);
            // Recipe layout determined by RECIPE_CONTRIBUTORS lookup table; patternType declared as Object
            // to avoid PatternType class <-> interface binary compatibility issues
            Object patternType = pattern.getPattern();
            RecipeContext ctx = new RecipeContext(dyeColor, recipe);
            PatternRecipeContributor contributor = RECIPE_CONTRIBUTORS.get(patternType);
            if (contributor != null) {
                contributor.contribute(ctx);
            }
            // Place banner and dye
            recipe.put(ctx.bannerSlot, prevBanner);
            for (int i : ctx.dyeSlots) {
                recipe.put(i, dyeItem.clone());
            }
        }
        // Crafting result
        // Current banner
        ItemStack currentBanner = new ItemStack(DyeColorRegistry.getBannerMaterial(baseColor));
        BannerMeta cbm = (BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta());
        // Add to current Pattern
        for (int i = 0; i < step - 1; i++) {
            cbm.addPattern(bm.getPattern(i));
        }
        currentBanner.setItemMeta(cbm);
        recipe.put(9, currentBanner);

        return recipe;
    }
}
