package club.kid7.bannermaker.util;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class BannerUtil {
    /**
     * Check if ItemStack is a banner
     *
     * @param itemStack The item to check
     * @return Whether it is a banner
     */
    static public boolean isBanner(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return isBanner(itemStack.getType());
    }

    /**
     * Check if Material is a banner
     *
     * @param material The material to check
     * @return Whether it is a banner
     */
    static public boolean isBanner(Material material) {
        if (material == null) {
            return false;
        }
        return XTag.BANNERS.isTagged(XMaterial.matchXMaterial(material));
    }

    /**
     * Determine if it is a loom recipe
     *
     * @param patternRecipe The recipe to check, produced by getPatternRecipe()
     * @return Whether it is a loom recipe
     */
    static public boolean isLoomRecipe(HashMap<Integer, ItemStack> patternRecipe) {
        for (Map.Entry<Integer, ItemStack> entry : patternRecipe.entrySet()) {
            ItemStack itemStack = entry.getValue();
            // If it contains a banner pattern item, it is directly regarded as a loom recipe
            if (isBannerPatternItemStack(itemStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if it is a banner pattern item
     *
     * @param itemStack The item to check
     * @return Whether it is a banner pattern item
     */
    static public boolean isBannerPatternItemStack(ItemStack itemStack) {
        return itemStack.getType().toString().endsWith("_BANNER_PATTERN");
    }

    /**
     * Get the banner material list
     *
     * @param banner The banner to get the material list for
     * @return List<ItemStack>
     */
    static public List<ItemStack> getMaterials(ItemStack banner) {
        List<ItemStack> materialList = new ArrayList<>();
        //Only check banners
        if (!isBanner(banner)) {
            return materialList;
        }
        //Basic materials
        //Stick
        ItemStack stick = new ItemStack(Material.STICK, 1);
        materialList.add(stick);
        //Color
        DyeColor baseColor = DyeColorRegistry.getDyeColor(banner.getType());

        //Wool
        ItemStack wool = new ItemStack(DyeColorRegistry.getWoolMaterial(baseColor), 6);
        materialList.add(wool);

        //Pattern materials
        Inventory materialInventory = Bukkit.createInventory(null, 54);
        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

        //Calculate Pattern by Pattern
        for (Pattern pattern : bm.getPatterns()) {
            //Required dye
            DyeColor dyeColor = pattern.getColor();
            PatternType patternType = pattern.getPattern();
            if (patternType.equals(PatternType.SQUARE_BOTTOM_LEFT)
                || patternType.equals(PatternType.SQUARE_BOTTOM_RIGHT)
                || patternType.equals(PatternType.SQUARE_TOP_LEFT)
                || patternType.equals(PatternType.SQUARE_TOP_RIGHT)
                || patternType.equals(PatternType.CIRCLE)) {
                materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 1));
            } else if (patternType.equals(PatternType.STRIPE_BOTTOM)
                || patternType.equals(PatternType.STRIPE_TOP)
                || patternType.equals(PatternType.STRIPE_LEFT)
                || patternType.equals(PatternType.STRIPE_RIGHT)
                || patternType.equals(PatternType.STRIPE_CENTER)
                || patternType.equals(PatternType.STRIPE_MIDDLE)
                || patternType.equals(PatternType.STRIPE_DOWNRIGHT)
                || patternType.equals(PatternType.STRIPE_DOWNLEFT)
                || patternType.equals(PatternType.TRIANGLE_BOTTOM)
                || patternType.equals(PatternType.TRIANGLE_TOP)
                || patternType.equals(PatternType.TRIANGLES_BOTTOM)
                || patternType.equals(PatternType.TRIANGLES_TOP)
                || patternType.equals(PatternType.DIAGONAL_LEFT)
                || patternType.equals(PatternType.DIAGONAL_RIGHT)
                || patternType.equals(PatternType.DIAGONAL_UP_LEFT)
                || patternType.equals(PatternType.DIAGONAL_UP_RIGHT)) {
                materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 3));
            } else if (patternType.equals(PatternType.SMALL_STRIPES)
                || patternType.equals(PatternType.RHOMBUS)
                || patternType.equals(PatternType.GRADIENT)
                || patternType.equals(PatternType.GRADIENT_UP)) {
                materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 4));
            } else if (patternType.equals(PatternType.CROSS)
                || patternType.equals(PatternType.STRAIGHT_CROSS)) {
                materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 5));
            } else if (patternType.equals(PatternType.HALF_VERTICAL)
                || patternType.equals(PatternType.HALF_HORIZONTAL)
                || patternType.equals(PatternType.HALF_VERTICAL_RIGHT)
                || patternType.equals(PatternType.HALF_HORIZONTAL_BOTTOM)) {
                materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 6));
            } else if (patternType.equals(PatternType.BORDER)) {
                materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 8));
            } else if (patternType.equals(PatternType.CURLY_BORDER)) {
                materialInventory.addItem(new ItemStack(Material.VINE));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 1));
                }
            } else if (patternType.equals(PatternType.CREEPER)) {
                materialInventory.addItem(new ItemStack(Material.CREEPER_HEAD));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 1));
                }
            } else if (patternType.equals(PatternType.BRICKS)) {
                materialInventory.addItem(new ItemStack(Material.BRICK));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 1));
                }
            } else if (patternType.equals(PatternType.SKULL)) {
                materialInventory.addItem(new ItemStack(Material.WITHER_SKELETON_SKULL));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 1));
                }
            } else if (patternType.equals(PatternType.FLOWER)) {
                materialInventory.addItem(new ItemStack(Material.OXEYE_DAISY));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 1));
                }
            } else if (patternType.equals(PatternType.MOJANG)) {
                materialInventory.addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 1));
                }
            } else if (patternType.equals(PatternType.PIGLIN)) { // Pattern materials are not consumed, at most one will be needed
                // TODO: Should be moved to the back to be processed together
                if (!materialInventory.contains(Material.PIGLIN_BANNER_PATTERN)) {
                    materialInventory.addItem(new ItemStack(Material.PIGLIN_BANNER_PATTERN));
                }
                materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 1));
            } else if (patternType.equals(PatternType.GLOBE)) { // Pattern materials are not consumed, at most one will be needed
                // TODO: Should be moved to the back to be processed together
                if (!materialInventory.contains(Material.GLOBE_BANNER_PATTERN)) {
                    materialInventory.addItem(new ItemStack(Material.GLOBE_BANNER_PATTERN));
                }
                materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 1));
            } else if (patternType.equals(PatternType.FLOW)) { // Pattern materials are not consumed, at most one will be needed
                // TODO: Should be moved to the back to be processed together
                if (!materialInventory.contains(Material.FLOW_BANNER_PATTERN)) {
                    materialInventory.addItem(new ItemStack(Material.FLOW_BANNER_PATTERN));
                }
                materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 1));
            } else if (patternType.equals(PatternType.GUSTER)) { // Pattern materials are not consumed, at most one will be needed
                // TODO: Should be moved to the back to be processed together
                if (!materialInventory.contains(Material.GUSTER_BANNER_PATTERN)) {
                    materialInventory.addItem(new ItemStack(Material.GUSTER_BANNER_PATTERN));
                }
                materialInventory.addItem(DyeColorRegistry.getDyeItemStack(dyeColor, 1));
            }
        }
        // Add to temporary list
        List<ItemStack> patternMaterials = new ArrayList<>();
        for (ItemStack item : materialInventory.getContents()) {
            if (item != null && !item.getType().isAir()) {
                patternMaterials.add(item);
            }
        }
        //重新排序
        // AI Translated: Re-sort
        InventoryUtil.sort(patternMaterials);
        //將材料加到清單中
        // AI Translated: Add materials to the list
        materialList.addAll(patternMaterials);

        return materialList;
    }

    /**
     * 檢查是否擁有足夠材料
     * AI Translated: Check if there are enough materials
     *
     * @param inventory 指定物品欄
     * AI Translated: Specified inventory
     * @param banner    旗幟
     * AI Translated: Banner
     * @return 是否擁有足夠材料
     * AI Translated: Whether there are enough materials
     */
    static public boolean hasEnoughMaterials(Inventory inventory, ItemStack banner) {
        //只檢查旗幟
        // AI Translated: Only check banners
        if (!isBanner(banner)) {
            return false;
        }
        //材料清單
        // AI Translated: Material list
        List<ItemStack> materials = getMaterials(banner);
        for (ItemStack material : materials) {
            //任何一項不足
            // AI Translated: Any item insufficient
            if (!inventory.containsAtLeast(material, material.getAmount())) {
                //直接回傳false
                // AI Translated: Directly return false
                return false;
            }
        }
        return true;
    }

    static public boolean isCraftableInServer(ItemStack banner, int maxPatterns){
        // Only check banners
        if (!isBanner(banner)) {
            return false;
        }
        int patternCount = ((BannerMeta) Objects.requireNonNull(banner.getItemMeta())).numberOfPatterns();
        return patternCount <= maxPatterns;
    }

    /**
     * 是否可以在生存模式合成（不超過6個pattern）
     * AI Translated: Whether it can be crafted in survival mode (no more than 6 patterns)
     *
     * @param banner 旗幟
     * AI Translated: Banner
     * @return 是否可以合成
     * AI Translated: Whether it can be crafted
     */
    static public boolean isCraftableInSurvival(ItemStack banner) {
        //Only check banners
        if (!isBanner(banner)) {
            return false;
        }
        int patternCount = ((BannerMeta) Objects.requireNonNull(banner.getItemMeta())).numberOfPatterns();
        return patternCount <= 6;
    }

    /**
     * 是否可以合成
     * AI Translated: Whether it can be crafted
     *
     * @param player 玩家
     * AI Translated: Player
     * @param banner 旗幟
     * AI Translated: Banner
     * @return 是否可以合成
     * AI Translated: Whether it can be crafted
     */
    static public boolean isCraftable(Player player, ItemStack banner) {
        //只檢查旗幟
        // AI Translated: Only check banners
        if (!isBanner(banner)) {
            return false;
        }
        // 若啟用複雜合成功能，則額外檢查玩家是否擁有對應權限
        // AI Translated: If complex crafting is enabled, additionally check if the player has the corresponding permission
        if (BannerMaker.getInstance().isEnableComplexBannerCraft()) {
            if (player.hasPermission("bannermaker.getbanner.complex-craft")) {
                return true;
            }
        }
        return isCraftableInSurvival(banner);
    }

    /**
     * 取得旗幟在玩家存檔中的Key
     * AI Translated: Get the banner's key in the player's archive
     *
     * @param banner 欲檢查之旗幟
     * AI Translated: The banner to check
     * @return String
     */
    static public String getKey(ItemStack banner) {
        //只處理旗幟
        // AI Translated: Only handle banners
        if (!isBanner(banner)) {
            return null;
        }
        ItemMeta itemMeta = Objects.requireNonNull(banner.getItemMeta());
        return PersistentDataUtil.get(itemMeta, "banner-key");
    }

    /**
     * 取得旗幟名稱，若無名稱則嘗試取得KEY
     * AI Translated: Get the banner name, or try to get the KEY if there is no name
     *
     * @param banner 欲檢查之旗幟
     * AI Translated: The banner to check
     * @return String
     */
    static public String getName(ItemStack banner) {
        //只處理旗幟
        // AI Translated: Only handle banners
        if (!isBanner(banner)) {
            return null;
        }
        //先試著取得自訂名稱
        // AI Translated: First try to get the custom name
        if (banner.hasItemMeta() && Objects.requireNonNull(banner.getItemMeta()).hasDisplayName()) {
            return banner.getItemMeta().getDisplayName();
        }
        //嘗試取得key
        // AI Translated: Try to get the key
        String key = BannerUtil.getKey(banner);
        if (key != null) {
            return key;
        }
        //若都沒有，回傳空字串
        // AI Translated: If neither, return an empty string
        return "";
    }

    public static List<PatternType> getPatternTypeList() {
        return Registry.BANNER_PATTERN.stream()
            .sorted(Comparator.comparing(p -> p.getKey().toString()))
            .filter(pattern -> !pattern.getKey().getKey().equals("base"))
            .collect(Collectors.toList());
    }

    static public HashMap<Integer, ItemStack> getPatternRecipe(final ItemStack banner, int step) {
        HashMap<Integer, ItemStack> recipe = Maps.newHashMap();
        //填滿空氣
        // AI Translated: Fill with air
        for (int i = 0; i < 10; i++) {
            recipe.put(i, new ItemStack(Material.AIR));
        }
        //只處理旗幟
        // AI Translated: Only handle banners
        if (!isBanner(banner)) {
            return recipe;
        }
        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());
        int totalStep = bm.numberOfPatterns() + 1;
        //顏色
        // AI Translated: Color
        DyeColor baseColor = DyeColorRegistry.getDyeColor(banner.getType());
        if (step == 1) {
            //第一步，旗幟合成
            // AI Translated: First step, banner crafting
            //羊毛
            // AI Translated: Wool
            ItemStack wool = new ItemStack(DyeColorRegistry.getWoolMaterial(baseColor));
            for (int i = 0; i < 6; i++) {
                recipe.put(i, wool.clone());
            }
            //木棒
            // AI Translated: Stick
            ItemStack stick = new ItemStack(Material.STICK);
            recipe.put(7, stick);
        } else if (step <= totalStep) {
            //新增Pattern
            // AI Translated: Add Pattern
            //當前banner
            // AI Translated: Current banner
            ItemStack prevBanner = new ItemStack(DyeColorRegistry.getBannerMaterial(baseColor));
            BannerMeta pbm = (BannerMeta) Objects.requireNonNull(prevBanner.getItemMeta());
            //新增至目前的Pattern
            // AI Translated: Add to current Patterns
            for (int i = 0; i < step - 2; i++) {
                pbm.addPattern(bm.getPattern(i));
            }
            prevBanner.setItemMeta(pbm);
            //當前Pattern
            // AI Translated: Current Pattern
            Pattern pattern = bm.getPattern(step - 2);
            //所需染料
            // AI Translated: Required dye
            DyeColor dyeColor = pattern.getColor();
            ItemStack dyeItem = DyeColorRegistry.getDyeItemStack(dyeColor, 1);
            //旗幟位置
            // AI Translated: Banner position
            int bannerPosition = 4;
            //染料位置
            // AI Translated: Dye position
            List<Integer> dyePosition = Collections.emptyList();
            //根據Pattern決定位置
            // AI Translated: Determine position based on Pattern
            PatternType patternType = pattern.getPattern();
            if (patternType.equals(PatternType.SQUARE_BOTTOM_LEFT)) {
                dyePosition = Collections.singletonList(6);
            } else if (patternType.equals(PatternType.SQUARE_BOTTOM_RIGHT)) {
                dyePosition = Collections.singletonList(8);
            } else if (patternType.equals(PatternType.SQUARE_TOP_LEFT)) {
                dyePosition = Collections.singletonList(0);
            } else if (patternType.equals(PatternType.SQUARE_TOP_RIGHT)) {
                dyePosition = Collections.singletonList(2);
            } else if (patternType.equals(PatternType.STRIPE_BOTTOM)) {
                dyePosition = Arrays.asList(6, 7, 8);
            } else if (patternType.equals(PatternType.STRIPE_TOP)) {
                dyePosition = Arrays.asList(0, 1, 2);
            } else if (patternType.equals(PatternType.STRIPE_LEFT)) {
                dyePosition = Arrays.asList(0, 3, 6);
            } else if (patternType.equals(PatternType.STRIPE_RIGHT)) {
                dyePosition = Arrays.asList(2, 5, 8);
            } else if (patternType.equals(PatternType.STRIPE_CENTER)) {
                bannerPosition = 3;
                dyePosition = Arrays.asList(1, 4, 7);
            } else if (patternType.equals(PatternType.STRIPE_MIDDLE)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(3, 4, 5);
            } else if (patternType.equals(PatternType.STRIPE_DOWNRIGHT)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(0, 4, 8);
            } else if (patternType.equals(PatternType.STRIPE_DOWNLEFT)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(2, 4, 6);
            } else if (patternType.equals(PatternType.SMALL_STRIPES)) {
                dyePosition = Arrays.asList(0, 2, 3, 5);
            } else if (patternType.equals(PatternType.CROSS)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(0, 2, 4, 6, 8);
            } else if (patternType.equals(PatternType.STRAIGHT_CROSS)) {
                bannerPosition = 0;
                dyePosition = Arrays.asList(1, 3, 4, 5, 7);
            } else if (patternType.equals(PatternType.TRIANGLE_BOTTOM)) {
                bannerPosition = 7;
                dyePosition = Arrays.asList(4, 6, 8);
            } else if (patternType.equals(PatternType.TRIANGLE_TOP)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(0, 2, 4);
            } else if (patternType.equals(PatternType.TRIANGLES_BOTTOM)) {
                dyePosition = Arrays.asList(3, 5, 7);
            } else if (patternType.equals(PatternType.TRIANGLES_TOP)) {
                dyePosition = Arrays.asList(1, 3, 5);
            } else if (patternType.equals(PatternType.DIAGONAL_LEFT)) {
                dyePosition = Arrays.asList(0, 1, 3);
            } else if (patternType.equals(PatternType.DIAGONAL_RIGHT)) {
                dyePosition = Arrays.asList(1, 2, 5);
            } else if (patternType.equals(PatternType.DIAGONAL_UP_LEFT)) {
                dyePosition = Arrays.asList(3, 6, 7);
            } else if (patternType.equals(PatternType.DIAGONAL_UP_RIGHT)) {
                dyePosition = Arrays.asList(5, 7, 8);
            } else if (patternType.equals(PatternType.CIRCLE)) {
                bannerPosition = 1;
                dyePosition = Collections.singletonList(4);
            } else if (patternType.equals(PatternType.RHOMBUS)) {
                dyePosition = Arrays.asList(1, 3, 5, 7);
            } else if (patternType.equals(PatternType.HALF_VERTICAL)) {
                bannerPosition = 5;
                dyePosition = Arrays.asList(0, 1, 3, 4, 6, 7);
            } else if (patternType.equals(PatternType.HALF_HORIZONTAL)) {
                bannerPosition = 7;
                dyePosition = Arrays.asList(0, 1, 2, 3, 4, 5);
            } else if (patternType.equals(PatternType.HALF_VERTICAL_RIGHT)) {
                bannerPosition = 3;
                dyePosition = Arrays.asList(1, 2, 4, 5, 7, 8);
            } else if (patternType.equals(PatternType.HALF_HORIZONTAL_BOTTOM)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(3, 4, 5, 6, 7, 8);
            } else if (patternType.equals(PatternType.BORDER)) {
                dyePosition = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8);
            } else if (patternType.equals(PatternType.CURLY_BORDER)) {
                recipe.put(1, new ItemStack(Material.VINE));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    dyePosition = Collections.singletonList(7);
                }
            } else if (patternType.equals(PatternType.CREEPER)) {
                recipe.put(1, new ItemStack(Material.CREEPER_HEAD));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    dyePosition = Collections.singletonList(7);
                }
            } else if (patternType.equals(PatternType.GRADIENT)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(0, 2, 4, 7);
            } else if (patternType.equals(PatternType.GRADIENT_UP)) {
                bannerPosition = 7;
                dyePosition = Arrays.asList(1, 4, 6, 8);
            } else if (patternType.equals(PatternType.BRICKS)) {
                recipe.put(1, new ItemStack(Material.BRICK));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    dyePosition = Collections.singletonList(7);
                }
            } else if (patternType.equals(PatternType.SKULL)) {
                recipe.put(1, new ItemStack(Material.WITHER_SKELETON_SKULL));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    dyePosition = Collections.singletonList(7);
                }
            } else if (patternType.equals(PatternType.FLOWER)) {
                recipe.put(1, new ItemStack(Material.OXEYE_DAISY));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    dyePosition = Collections.singletonList(7);
                }
            } else if (patternType.equals(PatternType.MOJANG)) {
                recipe.put(1, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    dyePosition = Collections.singletonList(7);
                }
            } else if (patternType.equals(PatternType.PIGLIN)) {
                recipe.put(7, new ItemStack(Material.PIGLIN_BANNER_PATTERN));
                dyePosition = Collections.singletonList(5);
            } else if (patternType.equals(PatternType.GLOBE)) {
                recipe.put(7, new ItemStack(Material.GLOBE_BANNER_PATTERN));
                dyePosition = Collections.singletonList(5);
            } else if (patternType.equals(PatternType.FLOW)) {
                recipe.put(7, new ItemStack(Material.FLOW_BANNER_PATTERN));
                dyePosition = Collections.singletonList(5);
            } else if (patternType.equals(PatternType.GUSTER)) {
                recipe.put(7, new ItemStack(Material.GUSTER_BANNER_PATTERN));
                dyePosition = Collections.singletonList(5);
            }
            //放置旗幟與染料
            // AI Translated: Place banner and dye
            recipe.put(bannerPosition, prevBanner);
            for (int i : dyePosition) {
                recipe.put(i, dyeItem.clone());
            }
        }
        //合成結果
        // AI Translated: Crafting result
        //當前banner
        // AI Translated: Current banner
        ItemStack currentBanner = new ItemStack(DyeColorRegistry.getBannerMaterial(baseColor));
        BannerMeta cbm = (BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta());
        //新增至目前的Pattern
        // AI Translated: Add to current Patterns
        for (int i = 0; i < step - 1; i++) {
            cbm.addPattern(bm.getPattern(i));
        }
        currentBanner.setItemMeta(cbm);
        recipe.put(9, currentBanner);

        return recipe;
    }

    static public String serialize(ItemStack banner) {
        //只檢查旗幟
        // AI Translated: Only check banners
        if (!isBanner(banner)) {
            return null;
        }
        DyeColor color = Objects.requireNonNull(DyeColorRegistry.getDyeColor(banner.getType()));
        int colorCode = DyeColorRegistry.getValue(color);
        StringBuilder dataStringBuilder = new StringBuilder(String.valueOf(colorCode));

        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

        for (Pattern pattern : bm.getPatterns()) {
            dataStringBuilder
                .append(";")
                .append(pattern.getPattern().getIdentifier())
                .append(":")
                .append(DyeColorRegistry.getValue(pattern.getColor()));
        }
        String dataString = dataStringBuilder.toString();

        return SerializationUtil.objectToBase64(dataString);
    }

    static public ItemStack deserialize(String bannerString) {
        try {
            String dataString = SerializationUtil.objectFromBase64(bannerString);
            String[] dataArray = dataString.split(";");

            ItemStack banner = new ItemStack(DyeColorRegistry.getBannerMaterial(Integer.parseInt(dataArray[0])));

            BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

            for (int i = 1; i < dataArray.length; i++) {
                String[] patternData = dataArray[i].split(":");
                PatternType patternType = PatternType.getByIdentifier(patternData[0]);
                DyeColor patternColor = DyeColorRegistry.getDyeColor(Integer.parseInt(patternData[1]));
                Pattern pattern = new Pattern(patternColor, Objects.requireNonNull(patternType));
                bm.addPattern(pattern);
            }
            banner.setItemMeta(bm);
            return banner;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
