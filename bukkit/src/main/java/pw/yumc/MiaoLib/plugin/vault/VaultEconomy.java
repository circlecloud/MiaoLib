package pw.yumc.MiaoLib.plugin.vault;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.kit.PKit;

/**
 * Vault 经济管理
 *
 * @since 2016年5月12日 下午5:02:03
 * @author 喵♂呜
 */
public class VaultEconomy extends VaultBase {
    private static Economy economy;

    static {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null || (economy = rsp.getProvider()) == null) {
            PKit.disable("已加载 Vault 但是未找到经济相关插件 停止加载...");
        } else {
            Log.i("发现 Vault 使用经济管理系统 " + economy.getName());
        }
    }

    /**
     * 添加金额
     *
     * @param oPlayer
     *            玩家
     * @param amont
     *            数量
     * @return {@link EconomyResponse}
     */
    public static EconomyResponse add(OfflinePlayer oPlayer, double amont) {
        return economy.depositPlayer(oPlayer, amont);
    }

    /**
     * 获得Economy实例
     *
     * @return {@link Economy}
     */
    public static Economy getEconomy() {
        return economy;
    }

    /**
     * 判断玩家是否有指定的金额
     *
     * @param oPlayer
     *            玩家
     * @param amont
     *            数量
     * @return 是否
     */
    public static boolean has(OfflinePlayer oPlayer, double amont) {
        return economy.has(oPlayer, amont);
    }

    /**
     * 减少金额
     *
     * @param oPlayer
     *            玩家
     * @param amont
     *            数量
     * @return {@link EconomyResponse}
     */
    public static EconomyResponse remove(OfflinePlayer oPlayer, double amont) {
        return economy.withdrawPlayer(oPlayer, amont);
    }
}
