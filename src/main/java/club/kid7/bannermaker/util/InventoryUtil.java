package club.kid7.bannermaker.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class InventoryUtil {
    /**
     * Give player a single item stack
     *
     * @param player    The player to give items to
     * @param itemStack The item to give
     */
    public static void give(Player player, ItemStack itemStack) {
        //Copy ItemStack to avoid modifying the original item
        itemStack = itemStack.clone();
        //Remove all PersistentData
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        PersistentDataUtil.removeAll(itemMeta);
        itemStack.setItemMeta(itemMeta);
        //Put into player's inventory
        HashMap<Integer, ItemStack> itemsCanNotAddToInv = player.getInventory().addItem(itemStack);
        //If there's a part that cannot be put in, drop it on the ground directly
        if (!itemsCanNotAddToInv.isEmpty()) {
            player.getWorld().dropItem(player.getLocation(), itemsCanNotAddToInv.get(0));
        }
    }

    public static void sort(List<ItemStack> itemStacks) {
        //Remove null values
        itemStacks.removeAll(Collections.singletonList(null));
        //Re-sort
        itemStacks.sort((itemStack1, itemStack2) -> {
            int c = Integer.compare(itemStack1.getType().ordinal(), itemStack2.getType().ordinal());
            if (c == 0) {
                c = -Integer.compare(itemStack1.getAmount(), itemStack2.getAmount());
            }
            return c;
        });
        //Merge
        ItemStack previous = null;
        final Iterator<ItemStack> iterator = itemStacks.iterator();
        while (iterator.hasNext()) {
            final ItemStack item = iterator.next();
            if (previous != null && previous.isSimilar(item) && previous.getAmount() < previous.getMaxStackSize()) {
                int count = Math.min(item.getAmount(), previous.getMaxStackSize() - previous.getAmount());
                if (count > 0) {
                    previous.setAmount(previous.getAmount() + count);
                    item.setAmount(item.getAmount() - count);
                    if (item.getAmount() <= 0) {
                        iterator.remove();
                        continue;
                    }
                }
            }
            previous = item;
        }
    }
}
