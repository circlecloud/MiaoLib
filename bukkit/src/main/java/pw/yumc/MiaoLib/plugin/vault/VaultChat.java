package pw.yumc.MiaoLib.plugin.vault;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.kit.PKit;

/**
 * Vault 聊天管理
 *
 * @since 2016年5月12日 下午5:02:03
 * @author 喵♂呜
 */
public class VaultChat extends VaultBase {
    private static Chat chat;

    static {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null || (chat = rsp.getProvider()) == null) {
            PKit.disable("已加载 Vault 但是未找到聊天相关插件 停止加载...");
        } else {
            Log.i("发现 Vault 使用聊天管理系统 " + chat.getName());
        }
    }

    /**
     * 获得Chat实例
     *
     * @return {@link Chat}
     */
    public static Chat getChat() {
        return chat;
    }

    /**
     * 获得玩家称号
     *
     * @param player
     *            玩家实体
     * @return 玩家称号
     */
    public static String getPlayerPrefix(Player player) {
        return chat.getPlayerPrefix(player);
    }

    /**
     * 设置玩家所有世界称号
     *
     * @param player
     *            玩家实体
     * @param prefix
     *            玩家称号
     */
    public static void setPlayerPrefix(Player player, String prefix) {
        chat.setPlayerPrefix(player, prefix);
    }
}
