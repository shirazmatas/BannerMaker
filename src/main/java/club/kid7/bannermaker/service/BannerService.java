package club.kid7.bannermaker.service;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.InventoryUtil;
import club.kid7.bannermaker.util.MessageComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

import static club.kid7.bannermaker.configuration.Language.tl;
import static club.kid7.bannermaker.util.TagUtil.tag;

public class BannerService {

    /**
     * 使用材料合成旗幟
     * AI Translated: Use materials to craft banner
     *
     * @param player 要給予物品的玩家
     * AI Translated: The player to give items to
     * @param banner 要給予的旗幟
     * AI Translated: The banner to give
     * @return 是否成功給予
     * AI Translated: Whether it was successfully given
     */
    public boolean craft(Player player, ItemStack banner) {
        //Check materials
        if (!BannerUtil.hasEnoughMaterials(player.getInventory(), banner)) {
            return false;
        }
        //Remove materials
        if (!removeMaterials(player, banner)) {
            return false;
        }

        InventoryUtil.give(player, banner);
        return true;
    }

    /**
     * Buy banner
     *
     * @param player The player to give items to
     * @param banner The banner to give
     * @return Whether it was successfully given
     */
    public boolean buy(Player player, ItemStack banner) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        EconomyService economyService = BannerMaker.getInstance().getEconomyService();
        //檢查是否啟用經濟
        // AI Translated: Check if economy is enabled
        if (!economyService.isAvailable()) {
            //未啟用經濟，強制失敗
            // AI Translated: Economy not enabled, force failure
            messageService.send(player, tl(NamedTextColor.RED, "general.economy-not-supported"));
            return false;
        }
        //價格
        // AI Translated: Price
        double price = economyService.getPrice(banner);
        //檢查財產是否足夠
        // AI Translated: Check if funds are sufficient
        if (!economyService.has(player, price)) {
            //財產不足
            // AI Translated: Insufficient funds
            messageService.send(player, tl(NamedTextColor.RED, "general.no-money"));
            return false;
        }
        //扣款
        // AI Translated: Deduct money
        EconomyResponse response = economyService.withdraw(player, price);
        //檢查交易是否成功
        // AI Translated: Check if transaction was successful
        if (!response.transactionSuccess()) {
            //交易失敗
            // AI Translated: Transaction failed
            messageService.send(player, tl(NamedTextColor.RED, "general.economy-transaction-error", tag("message", response.errorMessage)));
            return false;
        }
        InventoryUtil.give(player, banner);
        messageService.send(player, tl(NamedTextColor.GREEN, "general.money-transaction",
            tag("amount", economyService.format(response.amount)),
            tag("balance", economyService.format(response.balance))));
        return true;
    }

    /**
     * 展示旗幟給附近玩家
     * AI Translated: Show banner to nearby players
     *
     * @param sender 發送者
     * AI Translated: Sender
     * @param banner 要展示的旗幟
     * AI Translated: The banner to show
     * @param maxDistance 最大距離
     * AI Translated: Maximum distance
     */
    public void showToNearby(Player sender, ItemStack banner, double maxDistance) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        Component msgBannerName = buildBannerMessageComponent(banner);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("BannerMaker.show.receive") && !p.equals(sender)) {
                continue;
            }
            if (!p.getWorld().equals(sender.getWorld())) {
                continue;
            }
            if (p.getLocation().distanceSquared(sender.getLocation()) > maxDistance * maxDistance) {
                continue;
            }
            messageService.send(p, buildShowMessage(sender, msgBannerName));
        }
    }

    /**
     * 展示旗幟給所有玩家
     * AI Translated: Show banner to all players
     *
     * @param sender 發送者
     * AI Translated: Sender
     * @param banner 要展示的旗幟
     * AI Translated: The banner to show
     */
    public void showToAll(Player sender, ItemStack banner) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        Component msgBannerName = buildBannerMessageComponent(banner);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("BannerMaker.show.receive") && !p.equals(sender)) {
                continue;
            }
            messageService.send(p, buildShowMessage(sender, msgBannerName));
        }
    }

    /**
     * 建立旗幟展示訊息組件（含懸停與點擊事件）
     * AI Translated: Create banner display message component (including hover and click events)
     */
    private Component buildBannerMessageComponent(ItemStack banner) {
        String bannerString = BannerUtil.serialize(banner);
        return MessageComponentUtil.getTranslatableComponent(banner)
            .hoverEvent(MessageComponentUtil.getHoverEventItem(banner))
            .clickEvent(ClickEvent.runCommand("/bm view " + bannerString));
    }

    /**
     * 建立展示訊息的完整文字
     * AI Translated: Create the complete text for the display message
     */
    private Component buildShowMessage(Player sender, Component bannerName) {
        return tl("general.show-banner-message",
            tag("sender", sender.getDisplayName()),
            tag("banner", bannerName));
    }

    /**
     * 發送旗幟分享指令給玩家
     * AI Translated: Send banner sharing command to player
     *
     * @param player 玩家
     * AI Translated: Player
     * @param banner 要分享的旗幟
     * AI Translated: The banner to share
     */
    public void sendShareCommand(Player player, ItemStack banner) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        String bannerString = BannerUtil.serialize(banner);
        Component msg = tl("general.share-click-text")
            .hoverEvent(HoverEvent.showText(tl("general.share-hover-text")))
            .clickEvent(ClickEvent.copyToClipboard("/bm view " + bannerString));
        messageService.send(player, msg);
    }

    /**
     * 從物品欄移除材料
     * AI Translated: Remove materials from inventory
     */
    private boolean removeMaterials(Player player, ItemStack banner) {
        if (!BannerUtil.isBanner(banner)) {
            return false;
        }
        if (!BannerUtil.hasEnoughMaterials(player.getInventory(), banner)) {
            return false;
        }
        List<ItemStack> materials = BannerUtil.getMaterials(banner);
        //過濾材料，不須消耗旗幟圖形
        // AI Translated: Filter materials, banner patterns do not need to be consumed
        materials.removeIf(BannerUtil::isBannerPatternItemStack);
        HashMap<Integer, ItemStack> itemCannotRemoved = player.getInventory().removeItem(materials.toArray(new ItemStack[0]));
        return itemCannotRemoved.isEmpty();
    }
}
