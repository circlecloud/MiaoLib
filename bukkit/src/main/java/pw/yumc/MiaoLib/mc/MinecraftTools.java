package pw.yumc.MiaoLib.mc;

import pw.yumc.MiaoLib.annotation.NotProguard;

/**
 * Minecraft工具类
 *
 * @author 喵♂呜
 * @since 2017/1/26 0026
 */
@NotProguard
public class MinecraftTools {
    /**
     * 获得服务器信息
     *
     * @param address
     *         服务器地址
     * @return {@link ServerInfo} 服务器信息
     */
    public static ServerInfo getServerInfo(String address) {
        if (address.contains(":")) {
            String[] args = address.split(":");
            return new ServerInfo(args[0], Integer.parseInt(args[1]));
        } else {
            return new ServerInfo(address);
        }
    }

    /**
     * 获得服务器信息
     *
     * @param address
     *         服务器地址
     * @return {@link ServerInfo} 服务器信息
     */
    public static ServerPing getServerPing(String address) {
        if (address.contains(":")) {
            String[] args = address.split(":");
            return new ServerPing(args[0], Integer.parseInt(args[1]));
        } else {
            return new ServerPing(address);
        }
    }
}
