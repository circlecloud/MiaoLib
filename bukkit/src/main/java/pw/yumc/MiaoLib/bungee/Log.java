package pw.yumc.MiaoLib.bungee;

import java.util.logging.Logger;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * 插件日志输出类
 *
 * @author 喵♂呜
 * @since 2016年7月23日 上午9:11:01
 */
public class Log {
    private static boolean debug = false;
    public static Plugin plugin;
    public static Logger logger;

    public static void init(Plugin plugin) {
        Log.plugin = plugin;
        Log.logger = plugin.getLogger();
    }

    public static void d(String s, Exception e) {
        if (debug) {
            logger.info(s);
            e.printStackTrace();
        }
    }

    public static void i(String s, Object... objects) {
        logger.info(String.format(s, objects));
    }

    public static void d(String s) {
        if (debug) {
            logger.info(s);
        }
    }

    public static void d(Exception e) {
        if (debug) {
            e.printStackTrace();
        }
    }

    public static void w(String s, Object... objects) {
        logger.warning(String.format(s, objects));
    }
}
