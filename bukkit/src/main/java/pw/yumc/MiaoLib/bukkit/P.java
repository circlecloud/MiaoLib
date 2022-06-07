package pw.yumc.MiaoLib.bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 插件Instance获取类
 *
 * @author 喵♂呜
 * @since 2016年7月23日 上午9:09:57
 */
public class P {
    /**
     * 插件实例
     */
    public static JavaPlugin instance;
    /**
     * 插件配置方法
     */
    public static Method getInjectConfigMethod;

    static {
        Object pluginClassLoader = P.class.getClassLoader();
        try {
            Field field = pluginClassLoader.getClass().getDeclaredField("plugin");
            field.setAccessible(true);
            instance = (JavaPlugin) field.get(pluginClassLoader);
            try {
                getInjectConfigMethod = instance.getClass().getMethod("get" + instance.getName() + "Config");
            } catch (NoSuchMethodException e) {
                Log.d("配置方法 get%sConfig 未找到 将返回getConfig 调用结果!", instance.getName());
            }
        } catch (Exception e) {
            Log.d("P 类初始化失败 %s:%s", e.getClass().getName(), e.getMessage());
            Log.d(e);
        }
    }

    /**
     * @param name
     *         命令名称
     * @return 插件命令
     */
    public static PluginCommand getCommand(String name) {
        return instance.getCommand(name);
    }

    /**
     * @param <FC>
     *         配置源类型
     * @return 获得插件配置文件
     */
    public static <FC> FC getConfig() {
        return (FC) instance.getConfig();
    }

    /**
     * @param <FC>
     *         注入配置源类型
     * @return 获得插件注入配置
     */
    public static <FC> FC getInjectConfig() {
        try {
            return (FC) getInjectConfigMethod.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
        return getConfig();
    }

    /**
     * @return 获得插件文件夹
     */
    public static File getDataFolder() {
        return instance.getDataFolder();
    }

    /**
     * @return 获得插件描述文件
     */
    public static PluginDescriptionFile getDescription() {
        return instance.getDescription();
    }

    /**
     * @return 获得插件日志器
     */
    public static Logger getLogger() {
        return instance.getLogger();
    }

    /**
     * @return 插件名称
     */
    public static String getName() {
        return instance.getName();
    }

    /**
     * @param <PI>
     *         插件源类型
     * @return 获得插件
     */
    public static <PI> PI getPlugin() {
        return (PI) instance;
    }

    /**
     * @return 插件是否已启用
     */
    public static boolean isEnabled() {
        return instance.isEnabled();
    }

    /**
     * 批量注册监听器
     *
     * @param listeners
     *         监听器
     */
    public static void registerEvents(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, instance));
    }

    /**
     * 保存文件
     *
     * @param dirs
     *         目录
     */
    public static void saveFile(final String... dirs) {
        saveFile(false, dirs);
    }

    /**
     * 保存文件
     *
     * @param replace
     *         是否替换
     * @param dirs
     *         目录
     */
    public static void saveFile(boolean replace, final String... dirs) {
        URL url = instance.getClass().getClassLoader().getResource("plugin.yml");
        if (url == null) { return; }
        String upath = url.getFile().substring(url.getFile().indexOf("/") + 1);
        String jarPath = URLDecoder.decode(upath.substring(0, upath.indexOf('!')));
        if (!new File(jarPath).exists()) {
            jarPath = "/" + jarPath;
        }
        try (JarFile jar = new JarFile(jarPath)) {
            jar.stream().forEach(je -> {
                if (!je.isDirectory()) {
                    for (final String dir : dirs) {
                        if (je.getName().startsWith(dir)) {
                            if (!replace) {
                                // 不替换 并且文件不存在
                                if (!new File(getDataFolder(), je.getName()).exists()) {
                                    instance.saveResource(je.getName(), false);
                                }
                            } else {
                                instance.saveResource(je.getName(), true);
                            }
                        }
                    }
                }
            });
        } catch (IOException e) {
            Log.d(e);
        }
    }
}
