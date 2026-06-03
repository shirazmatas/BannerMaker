package club.kid7.bannermaker.banner;


import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.InventoryUtil;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Banner cost calculation: Given a banner, returns the list of crafting materials defined within the plugin and a "whether the player has enough materials" judgment.
 * Used for GUI display, material deduction in {@link club.kid7.bannermaker.service.BannerService#craft},
 * and banner price calculation in {@link club.kid7.bannermaker.service.EconomyService#getPrice}.
 * <p>
 * Material rules corresponding to each PatternType are determined by looking up the {@code MATERIAL_CONTRIBUTORS} table (extracted from {@link BannerUtil}).
 */
public class BannerCost {

    private BannerCost() {
        // Utility class
    }

    /**
     * Accumulates the contribution of each Pattern to materials: counts by Material as the key, and only counts loom banner pattern items once.
     */
    private static final class MaterialAccumulator {
        private final Map<Material, Integer> counts = new LinkedHashMap<>();
        private final Set<Material> loomItemsAdded = new HashSet<>();

        void add(ItemStack stack) {
            if (stack == null || stack.getType().isAir()) {
                return;
            }
            counts.merge(stack.getType(), stack.getAmount(), Integer::sum);
        }

        /** Loom banner pattern item: counted only once throughout the entire crafting process (subsequent contributors calling this will not repeat the addition). */
        void addLoomItemOnce(Material type) {
            if (loomItemsAdded.add(type)) {
                counts.merge(type, 1, Integer::sum);
            }
        }

        /** Flattens into an ItemStack list, automatically split according to the maxStackSize of each Material. */
        List<ItemStack> toItemStacks() {
            List<ItemStack> result = new ArrayList<>();
            counts.forEach((mat, count) -> {
                int remaining = count;
                int stackSize = Math.max(1, mat.getMaxStackSize());
                while (remaining > 0) {
                    int take = Math.min(remaining, stackSize);
                    result.add(new ItemStack(mat, take));
                    remaining -= take;
                }
            });
            return result;
        }
    }

    /**
     * Pattern material contribution strategy: Given an accumulator and the dye color of the pattern,
     * adds all ItemStacks that this pattern should consume to the accumulator.
     */
    @FunctionalInterface
    private interface PatternMaterialContributor {
        void contribute(MaterialAccumulator acc, DyeColor color);
    }

    /** Contributor for N dye items of the corresponding color. */
    private static PatternMaterialContributor dyeOnly(int count) {
        return (acc, color) -> acc.add(DyeColorRegistry.getDyeItemStack(color, count));
    }

    /** One special item + 1 corresponding color dye (Black is base, no extra color added). */
    private static PatternMaterialContributor specialWithOptionalDye(Material special) {
        return (acc, color) -> {
            if (special.toString().endsWith("_BANNER_PATTERN") || special.toString().equals("FIELD_MASONED_BANNER_PATTERN")) {
                acc.addLoomItemOnce(special);
            } else {
                acc.add(new ItemStack(special));
            }
            if (!color.equals(DyeColor.BLACK)) {
                acc.add(DyeColorRegistry.getDyeItemStack(color, 1));
            }
        };
    }

    /** Banner pattern items for Loom: only 1 is needed during the accumulation process (reusable), but each pattern still consumes 1 dye. */
    private static PatternMaterialContributor patternItem(Material item) {
        return (acc, color) -> {
            acc.addLoomItemOnce(item);
            acc.add(DyeColorRegistry.getDyeItemStack(color, 1));
        };
    }

    /** Version-sensitive contributor for BRICKS: uses FIELD_MASONED_BANNER_PATTERN for 1.21.2+, otherwise falls back to BRICK. */
    private static PatternMaterialContributor bricksContributor() {
        return (acc, color) -> {
            Material fieldMasoned = Material.matchMaterial("FIELD_MASONED_BANNER_PATTERN");
            if (fieldMasoned != null) {
                acc.addLoomItemOnce(fieldMasoned);
            } else {
                acc.add(new ItemStack(Material.BRICK));
            }
            if (!color.equals(DyeColor.BLACK)) {
                acc.add(DyeColorRegistry.getDyeItemStack(color, 1));
            }
        };
    }

    private static PatternMaterialContributor loomPattern(String patternName) {
        return (acc, color) -> {
            Material material = Material.matchMaterial(patternName);
            if (material != null) {
                acc.addLoomItemOnce(material);
            }
            // All patterns consume 1 dye except possibly when color is BLACK if it's the base...
            // But wait, in Loom you ALWAYS need 1 dye.
            acc.add(DyeColorRegistry.getDyeItemStack(color, 1));
        };
    }

    /**
     * Material contribution strategy table for each PatternType.
     * key is declared as Object to avoid invokeinterface (see CLAUDE.md for cross-version compatibility pitfalls).
     */
    private static final Map<Object, PatternMaterialContributor> MATERIAL_CONTRIBUTORS = Map.ofEntries(
        // 1 Dye ── Square corners and circles
        Map.entry(PatternType.SQUARE_BOTTOM_LEFT, dyeOnly(1)),
        Map.entry(PatternType.SQUARE_BOTTOM_RIGHT, dyeOnly(1)),
        Map.entry(PatternType.SQUARE_TOP_LEFT, dyeOnly(1)),
        Map.entry(PatternType.SQUARE_TOP_RIGHT, dyeOnly(1)),
        Map.entry(PatternType.CIRCLE, dyeOnly(1)),
        // 3 Dyes ── Stripes, Triangles, Diagonals
        Map.entry(PatternType.STRIPE_BOTTOM, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_TOP, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_LEFT, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_RIGHT, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_CENTER, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_MIDDLE, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_DOWNRIGHT, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_DOWNLEFT, dyeOnly(3)),
        Map.entry(PatternType.TRIANGLE_BOTTOM, dyeOnly(3)),
        Map.entry(PatternType.TRIANGLE_TOP, dyeOnly(3)),
        Map.entry(PatternType.TRIANGLES_BOTTOM, dyeOnly(3)),
        Map.entry(PatternType.TRIANGLES_TOP, dyeOnly(3)),
        Map.entry(PatternType.DIAGONAL_LEFT, dyeOnly(3)),
        Map.entry(PatternType.DIAGONAL_RIGHT, dyeOnly(3)),
        Map.entry(PatternType.DIAGONAL_UP_LEFT, dyeOnly(3)),
        Map.entry(PatternType.DIAGONAL_UP_RIGHT, dyeOnly(3)),
        // 4 Dyes ── Small stripes, Rhombus, Gradients
        Map.entry(PatternType.SMALL_STRIPES, dyeOnly(4)),
        Map.entry(PatternType.RHOMBUS, dyeOnly(4)),
        Map.entry(PatternType.GRADIENT, dyeOnly(4)),
        Map.entry(PatternType.GRADIENT_UP, dyeOnly(4)),
        // 5 Dyes ── Crosses
        Map.entry(PatternType.CROSS, dyeOnly(5)),
        Map.entry(PatternType.STRAIGHT_CROSS, dyeOnly(5)),
        // 6 Dyes ── Half sides
        Map.entry(PatternType.HALF_VERTICAL, dyeOnly(6)),
        Map.entry(PatternType.HALF_HORIZONTAL, dyeOnly(6)),
        Map.entry(PatternType.HALF_VERTICAL_RIGHT, dyeOnly(6)),
        Map.entry(PatternType.HALF_HORIZONTAL_BOTTOM, dyeOnly(6)),
        // 8 Dyes ── Border
        Map.entry(PatternType.BORDER, dyeOnly(8)),
        // Special item + Dye
        Map.entry(PatternType.CURLY_BORDER, loomPattern("BORDURE_BANNER_PATTERN")),
        Map.entry(PatternType.CREEPER, loomPattern("CREEPER_BANNER_PATTERN")),
        Map.entry(PatternType.SKULL, loomPattern("SKULL_BANNER_PATTERN")),
        Map.entry(PatternType.FLOWER, loomPattern("FLOWER_BANNER_PATTERN")),
        Map.entry(PatternType.MOJANG, loomPattern("MOJANG_BANNER_PATTERN")),
        Map.entry(PatternType.BRICKS, bricksContributor()),
        // Banner pattern items (for loom, reusable)
        Map.entry(PatternType.PIGLIN, patternItem(Material.PIGLIN_BANNER_PATTERN)),
        Map.entry(PatternType.GLOBE, patternItem(Material.GLOBE_BANNER_PATTERN)),
        Map.entry(PatternType.FLOW, patternItem(Material.FLOW_BANNER_PATTERN)),
        Map.entry(PatternType.GUSTER, patternItem(Material.GUSTER_BANNER_PATTERN))
    );

    /**
     * Gets the list of banner materials.
     *
     * @param banner The banner to get the material list for.
     * @return The complete material list required for the banner under the plugin's internal pricing; returns an empty list for invalid banners.
     */
    public static List<ItemStack> getMaterials(ItemStack banner) {
        List<ItemStack> materialList = new ArrayList<>();
        // Only check banners
        if (!BannerUtil.isBanner(banner)) {
            return materialList;
        }
        // Base materials
        // Stick
        ItemStack stick = new ItemStack(Material.STICK, 1);
        materialList.add(stick);
        // Wool
        // Color
        DyeColor baseColor = DyeColorRegistry.getDyeColor(banner.getType());
        // Wool
        ItemStack wool = new ItemStack(DyeColorRegistry.getWoolMaterial(baseColor), 6);
        materialList.add(wool);
        // Pattern materials: Accumulate using MaterialAccumulator to avoid creating a 54-slot Bukkit Inventory as a temporary container.
        MaterialAccumulator accumulator = new MaterialAccumulator();
        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());
        // Accumulate materials pattern by pattern: Actual corresponding rules are looked up in MATERIAL_CONTRIBUTORS.
        // patternType is declared as Object to avoid PatternType class ↔ interface binary compatibility issues (see CLAUDE.md).
        for (Pattern pattern : bm.getPatterns()) {
            Object patternType = pattern.getPattern();
            PatternMaterialContributor contributor = MATERIAL_CONTRIBUTORS.get(patternType);
            if (contributor != null) {
                contributor.contribute(accumulator, pattern.getColor());
            }
        }
        List<ItemStack> patternMaterials = accumulator.toItemStacks();
        InventoryUtil.sort(patternMaterials);
        materialList.addAll(patternMaterials);

        return materialList;
    }

    /**
     * Checks if there are enough materials.
     *
     * @param inventory Specified inventory.
     * @param banner    Banner.
     * @return Whether there are enough materials.
     */
    public static boolean hasEnoughMaterials(Inventory inventory, ItemStack banner) {
        // Only check banners
        if (!BannerUtil.isBanner(banner)) {
            return false;
        }
        // Material list
        List<ItemStack> materials = getMaterials(banner);
        for (ItemStack material : materials) {
            // Any item insufficient
            if (!inventory.containsAtLeast(material, material.getAmount())) {
                // Directly return false
                return false;
            }
        }
        return true;
    }
}
