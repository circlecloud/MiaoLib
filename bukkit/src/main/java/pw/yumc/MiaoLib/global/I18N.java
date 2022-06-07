package pw.yumc.MiaoLib.global;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

import pw.yumc.MiaoLib.config.ext.YumConfig;
import pw.yumc.MiaoLib.bukkit.Log;

/**
 * 国际化工具类
 * Created by 喵♂呜 on 2016/10/14 0014.
 */
public class I18N {
    private static String LANG = "zh_CN";
    private static Map<String, String> content;

    static {
        content = new HashMap<>();
        Log.i("Async init I18N tool ...");
        load();
    }

    private I18N() {
    }

    public static String $(String key) {
        return $(key, "");
    }

    public static String $(String key, String def) {
        return content.containsKey(key) ? content.get(key) : def;
    }

    public static void info(String str, Object... objs) {
        Log.i($(str), objs);
    }

    public static void info(String str, String def, Object... objs) {
        Log.i($(str, def), objs);
    }

    public static void send(CommandSender sender, String message, Object... objs) {
        send(sender, message, "", objs);
    }

    public static void send(CommandSender sender, String message, String def, Object... objs) {
        Log.sender(sender, $(message, def), objs);
    }

    /**
     * 载入数据
     */
    private static void load() {
        new Thread(() -> {
            try {
                Map<String, String> local = YumConfig.getLocal(LANG).getContentMap();
                Map<String, String> remote = YumConfig.getRemote(LANG).getContentMap();
                if (local != null) {
                    content.putAll(local);
                }
                if (remote != null) {
                    content.putAll(remote);
                }
                Log.i("本地化工具初始化完毕...");
            } catch (Exception e) {
                Log.w("本地化工具初始化失败: %s %s", e.getClass().getName(), e.getMessage());
                Log.d(LANG, e);
            }
        }).start();
    }
}
