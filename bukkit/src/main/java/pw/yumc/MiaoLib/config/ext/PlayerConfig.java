package pw.yumc.MiaoLib.config.ext;

import java.io.File;

import org.bukkit.entity.Player;

import pw.yumc.MiaoLib.config.FileConfig;

/**
 * 玩家配置管理类
 *
 * @author 喵♂呜
 * @version 1.0
 */
public class PlayerConfig extends FileConfig {
    private static String CONFIG_FOLDER = "userdata";

    /**
     * 获得玩家配置(保存在 指定 文件夹)
     *
     * @param dir
     *            指定目录
     * @param player
     *            玩家
     */
    public PlayerConfig(File dir, Player player) {
        super(dir, player.getName() + ".yml");
    }

    /**
     * 获得玩家配置(保存在 CONFIG_FOLDER 文件夹)
     *
     * @param player
     *            玩家
     */
    public PlayerConfig(Player player) {
        this(player.getName());
    }

    /**
     * 获得玩家配置(保存在 CONFIG_FOLDER 文件夹)
     *
     * @param playername
     *            玩家名称
     */
    public PlayerConfig(String playername) {
        super(new File(plugin.getDataFolder(), CONFIG_FOLDER), playername + ".yml");
    }
}
