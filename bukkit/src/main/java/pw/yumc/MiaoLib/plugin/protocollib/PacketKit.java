package pw.yumc.MiaoLib.plugin.protocollib;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import pw.yumc.MiaoLib.bukkit.Log;

/**
 * ProtocolLib发包工具
 *
 * @since 2016年7月7日 上午10:03:00
 * @author 喵♂呜
 */
public class PacketKit {
    public static boolean ENABLE = false;
    private static ProtocolManager manager;

    static {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            Log.w("未找到 ProtocolLib 插件 部分功能不可用...");
        } else {
            ENABLE = true;
            manager = ProtocolLibrary.getProtocolManager();
        }
    }

    /**
     * 给玩家发送心跳包
     *
     * @param player
     *            玩家
     * @throws InvocationTargetException
     *             调用异常
     */
    public static void keep_live(final Player player) throws InvocationTargetException {
        if (ENABLE) {
            send(player, manager.createPacket(PacketType.Play.Client.KEEP_ALIVE));
        }
    }

    /**
     * 发包
     *
     * @param player
     *            玩家
     * @param packet
     *            数据包
     * @throws InvocationTargetException
     *             调用异常
     */
    public static void send(final Player player, final PacketContainer packet) throws InvocationTargetException {
        if (ENABLE) {
            manager.sendServerPacket(player, packet);
        }
    }
}
