package pw.yumc.MiaoLib.plugin.protocollib;

import org.bukkit.Bukkit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import pw.yumc.MiaoLib.kit.PKit;

/**
 * ProtocolLib基础
 *
 * @since 2016年9月27日 下午2:41:00
 * @author 喵♂呜
 */
public class ProtocolLibBase {
    protected static ProtocolManager manager;

    static {
        if (Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
            PKit.disable("未找到 ProtocolLib 插件 停止加载...");
        } else {
            manager = ProtocolLibrary.getProtocolManager();
        }
    }
}
