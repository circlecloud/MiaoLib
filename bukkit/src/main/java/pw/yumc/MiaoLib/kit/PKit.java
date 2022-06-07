package pw.yumc.MiaoLib.kit;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.bukkit.P;

/**
 * 插件工具集
 *
 * @since 2016年3月31日 下午7:13:25
 * @author 喵♂呜
 */
public class PKit {
    private static Map<ClassLoader, Plugin> pluginMap = new HashMap<>();

    /**
     * 关闭插件
     */
    public static void disable() {
        Bukkit.getPluginManager().disablePlugin(P.instance);
    }

    /**
     * 关闭插件
     *
     * @param msg
     *            关闭提示
     */
    public static void disable(String msg) {
        Log.w(msg);
        disable();
    }

    /**
     * 通过堆栈获取操作插件
     *
     * @return 操作插件
     */
    public static Plugin getOperatePlugin() {
        return getOperatePlugin(new Exception().getStackTrace());
    }

    /**
     * 通过堆栈获取操作插件
     *
     * @param stacktrace
     *            堆栈
     * @return 操作插件
     */
    public static Plugin getOperatePlugin(StackTraceElement[] stacktrace) {
        collectPlugin();
        for (StackTraceElement element : stacktrace) {
            try {
                ClassLoader loader = Class.forName(element.getClassName(), false, PKit.class.getClassLoader()).getClassLoader();
                if (pluginMap.containsKey(loader)) { return pluginMap.get(loader); }
            } catch (ClassNotFoundException ignored) {
            }
        }
        return null;
    }

    /**
     * 运行主线程任务
     *
     * @param run
     *            任务
     * @return Bukkit 任务 {@link BukkitTask}
     */
    public static BukkitTask runTask(Runnable run) {
        return Bukkit.getScheduler().runTask(P.instance, run);
    }

    /**
     * 运行异步任务
     *
     * @param run
     *            任务
     * @return Bukkit 任务 {@link BukkitTask}
     */
    public static BukkitTask runTaskAsync(Runnable run) {
        return Bukkit.getScheduler().runTaskAsynchronously(P.instance, run);
    }

    /**
     * 运行主线程延时任务
     *
     * @param run
     *            任务
     * @param delay
     *            延时多久
     * @return Bukkit 任务 {@link BukkitTask}
     */
    public static BukkitTask runTaskLater(Runnable run, long delay) {
        return Bukkit.getScheduler().runTaskLater(P.instance, run, delay);
    }

    /**
     * 运行异步延时任务
     *
     * @param run
     *            任务
     * @param delay
     *            延时多久
     * @return Bukkit 任务 {@link BukkitTask}
     */
    public static BukkitTask runTaskLaterAsync(Runnable run, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(P.instance, run, delay);
    }

    /**
     * 运行主线程循环任务
     *
     * @param run
     *            任务
     * @param delay
     *            开启延时
     * @param timer
     *            任务间隔
     * @return Bukkit 任务 {@link BukkitTask}
     */
    public static BukkitTask runTaskTimer(Runnable run, long delay, long timer) {
        return Bukkit.getScheduler().runTaskTimer(P.instance, run, delay, timer);
    }

    /**
     * 运行异步循环任务
     *
     * @param run
     *            任务
     * @param delay
     *            开启延时
     * @param timer
     *            任务间隔
     * @return Bukkit 任务 {@link BukkitTask}
     */
    public static BukkitTask runTaskTimerAsync(Runnable run, long delay, long timer) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(P.instance, run, delay, timer);
    }

    /**
     * 安排一个主线程任务
     *
     * @param run
     *            任务
     * @return 任务ID
     */
    public static int scheduleTask(Runnable run) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(P.instance, run);
    }

    /**
     * 安排一个主线程循环任务
     *
     * @param run
     *            任务
     * @param delay
     *            开启延时
     * @param timer
     *            执行间隔
     * @return 任务ID
     */
    public static int scheduleTask(Runnable run, long delay, long timer) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(P.instance, run, delay, timer);
    }

    /**
     * 收集插件信息
     */
    private static void collectPlugin() {
        if (Bukkit.getPluginManager().getPlugins().length != pluginMap.keySet().size() - 1) {
            pluginMap.clear();
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                pluginMap.put(plugin.getClass().getClassLoader(), plugin);
            }
            pluginMap.remove(PKit.class.getClassLoader());
        }
    }
}
