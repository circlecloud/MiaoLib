package pw.yumc.MiaoLib.plugin.vault;

import org.bukkit.Bukkit;

import pw.yumc.MiaoLib.kit.PKit;

/**
 * Vault 基础
 *
 * @since 2016年5月12日 下午4:52:57
 * @author 喵♂呜
 */
public class VaultBase {
    static {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            PKit.disable("未找到 Vault 插件 停止加载...");
        }
    }
}
