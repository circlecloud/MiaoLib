package pw.yumc.MiaoLib.plugin.playerpoints;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import pw.yumc.MiaoLib.kit.PKit;

@SuppressWarnings("deprecation")
public class PointAPI {
    private static PlayerPointsAPI api;
    static {
        Plugin pp = Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints");
        if (pp == null) {
            PKit.disable("未找到 PlayerPoint 插件 停止加载...");
        } else {
            api = ((PlayerPoints) pp).getAPI();
        }
    }

    /**
     * @return 获取{@link PlayerPointsAPI}
     */
    public static PlayerPointsAPI getAPI() {
        return api;
    }

    /**
     * 添加点券
     * 
     * @param player
     *            玩家
     * @param amount
     *            数量
     * @return 是否成功
     */
    public static boolean add(String player, int amount) {
        return api.give(player, amount);
    }

    /**
     * 玩家是否指定的点券
     * 
     * @param player
     *            玩家
     * @param amount
     *            数量
     * @return 是否有
     */
    public static boolean has(String player, int amount) {
        return api.look(player) >= amount;
    }

    /**
     * 扣除点券
     * 
     * @param player
     *            玩家
     * @param amount
     *            数量
     * @return 是否成功
     */
    public static boolean remove(String player, int amount) {
        return api.take(player, amount);
    }

    /**
     * 添加点券
     *
     * @param player
     *            玩家
     * @param amount
     *            数量
     * @return 是否成功
     */
    public static boolean add(OfflinePlayer player, int amount) {
        return api.give(player.getUniqueId(), amount);
    }

    /**
     * 玩家是否指定的点券
     *
     * @param player
     *            玩家
     * @param amount
     *            数量
     * @return 是否有
     */
    public static boolean has(OfflinePlayer player, int amount) {
        return api.look(player.getUniqueId()) >= amount;
    }

    /**
     * 扣除点券
     *
     * @param player
     *            玩家
     * @param amount
     *            数量
     * @return 是否成功
     */
    public static boolean remove(OfflinePlayer player, int amount) {
        return api.take(player.getUniqueId(), amount);
    }
}
