package pw.yumc.MiaoLib.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.bukkit.P;
import pw.yumc.MiaoLib.reflect.Reflect;

/**
 * 命令工具类
 * 
 * @author 蒋天蓓
 * @since 2016/11/21 0021.
 */
public class CommandKit {
    private static Constructor<PluginCommand> PluginCommandConstructor;
    private static Map<String, Command> knownCommands;
    private static Map<String, Plugin> lookupNames;
    static {
        try {
            PluginManager pluginManager = Bukkit.getPluginManager();

            Field lookupNamesField = pluginManager.getClass().getDeclaredField("lookupNames");
            lookupNamesField.setAccessible(true);
            lookupNames = (Map<String, Plugin>) lookupNamesField.get(pluginManager);

            Field commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);

            knownCommands = Reflect.on(commandMap).field("knownCommands").get();

            PluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            PluginCommandConstructor.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            Log.d("初始化命令管理器失败!");
            Log.d(e);
        }
    }

    /**
     * 创建命令
     * 
     * @param name
     *            命令名称
     * @return {@link PluginCommand}
     */
    public static PluginCommand create(String name) {
        return create(P.instance, name);
    }

    /**
     * 创建命令
     *
     * @param name
     *            命令名称
     * @param aliases
     *            别名
     * @return {@link PluginCommand}
     */
    public static PluginCommand create(String name, String... aliases) {
        return create(P.instance, name, aliases);
    }

    /**
     * 创建命令
     * 
     * @param plugin
     *            插件
     * @param name
     *            命令名称
     * @param aliases
     *            别名
     * @return {@link PluginCommand}
     */
    public static PluginCommand create(JavaPlugin plugin, String name, String... aliases) {
        try {
            Command cmd = PluginCommandConstructor.newInstance(name, plugin);
            registerCommand(plugin, name, cmd);
            for (String alias : aliases) {
                registerCommand(plugin, alias, cmd);
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Log.d(e);
        }
        return plugin.getCommand(name);
    }

    public static void registerCommand(Plugin plugin, String name, Command cmd) {
        if (name.isEmpty()) { return; }
        knownCommands.put(name, cmd);
        knownCommands.put(plugin.getName().toLowerCase() + ":" + name, cmd);
        lookupNames.put(name, plugin);
    }
}
