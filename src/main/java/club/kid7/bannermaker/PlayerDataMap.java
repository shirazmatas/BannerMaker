package club.kid7.bannermaker;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataMap {
    /**
     * 所有玩家資料實例
     * AI Translated: All player data instances
     */
    private final HashMap<UUID, PlayerData> allPlayerData = Maps.newHashMap();

    /**
     * 取得玩家資料實例
     * AI Translated: Get player data instance
     *
     * @param player 玩家
     * AI Translated: Player
     * @return 玩家資料
     * AI Translated: Player data
     */
    public PlayerData get(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData playerData = allPlayerData.get(uuid);
        if (playerData == null) {
            playerData = new PlayerData();
            allPlayerData.put(uuid, playerData);
        }
        return playerData;
    }
}
