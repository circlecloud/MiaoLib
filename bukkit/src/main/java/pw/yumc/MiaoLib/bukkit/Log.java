package pw.yumc.MiaoLib.bukkit;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import pw.yumc.MiaoLib.annotation.NotProguard;

/**
 * 插件日志输出类
 *
 * @author 喵♂呜
 * @since 2016年7月23日 上午9:11:01
 */
@NotProguard
public class Log {
    private static boolean fullDebug = new File(String.format("plugins%1$sYumCore%1$sfulldebug", File.separatorChar)).exists();
    private static boolean globalDebug = new File(String.format("plugins%1$sYumCore%1$sdebug", File.separatorChar)).exists();
    private static boolean debug;
    private static Logger logger;
    private static CommandSender console;
    private static String prefix;

    static {
        try {
            debug = globalDebug || P.getDescription().getVersion().contains("DEV");
            logger = P.instance.getLogger();
            console = Bukkit.getConsoleSender();
            prefix = String.format("§6[§b%s§6]§r ", P.instance.getName());
        } catch (Throwable ex) {
            logger = Logger.getLogger("YumCore");
            debug = true;
            globalDebug = true;
        }
    }

    private Log() {
    }

    /**
     * Add a log Handler to receive logging messages.
     * <p>
     * By default, Loggers also send their output to their parent logger.
     * Typically the root Logger is configured with a set of Handlers
     * that essentially act as default handlers for all loggers.
     *
     * @param handler
     *         a logging Handler
     * @throws SecurityException
     *         if a security manager exists and if
     *         the caller does not have LoggingPermission("control").
     */
    public static void addHandler(Handler handler) throws SecurityException {
        logger.addHandler(handler);
    }

    /**
     * Sends console a message
     *
     * @param message
     *         Message to be displayed
     */
    public static void console(String message) {
        console.sendMessage(prefix + message);
    }

    /**
     * Sends console a message
     *
     * @param message
     *         消息
     * @param object
     *         格式化参数
     */
    public static void console(String message, Object... object) {
        console.sendMessage(prefix + String.format(message, object));
    }

    /**
     * Sends console a message
     *
     * @param msg
     *         Message to be displayed
     */
    public static void console(String[] msg) {
        for (String str : msg) {
            console(str);
        }
    }

    /**
     * 调试消息
     *
     * @param msg
     *         消息
     */
    public static void d(String msg) {
        if (debug) {
            logger.info("[DEBUG] " + msg);
        }
    }

    /**
     * 调试消息
     *
     * @param msg
     *         消息
     * @param object
     *         参数
     */
    public static void d(String msg, Object... object) {
        if (debug) {
            logger.info(String.format("[DEBUG] " + msg, object));
        }
    }

    /**
     * 调试消息
     *
     * @param e
     *         异常
     */
    public static void d(Throwable e) {
        if (debug) {
            e.printStackTrace();
        }
    }

    /**
     * 调试消息
     *
     * @param msg
     *         消息
     * @param e
     *         异常
     */
    public static void d(String msg, Throwable e) {
        if (debug) {
            logger.info("[DEBUG] " + msg);
            e.printStackTrace();
        }
    }

    /**
     * 完全调试消息
     *
     * @param msg
     *         消息
     */
    public static void fd(String msg) {
        if (fullDebug) {
            logger.info("[DEBUG] " + msg);
        }
    }

    /**
     * 完全调试消息
     *
     * @param msg
     *         消息
     * @param object
     *         参数
     */
    public static void fd(String msg, Object... object) {
        if (fullDebug) {
            logger.info("[DEBUG] " + String.format(msg, object));
        }
    }

    /**
     * 完全调试消息
     *
     * @param e
     *         异常
     */
    public static void fd(Throwable e) {
        if (fullDebug) {
            e.printStackTrace();
        }
    }

    /**
     * 完全调试消息
     *
     * @param msg
     *         消息
     * @param e
     *         异常
     */
    public static void fd(String msg, Throwable e) {
        if (fullDebug) {
            logger.info("[DEBUG] " + msg);
            e.printStackTrace();
        }
    }

    /**
     * @return 获得插件前缀
     */
    public static String getPrefix() {
        return prefix;
    }

    public static void i(String msg) {
        logger.info(msg);
    }

    public static void i(String msg, Object... objs) {
        logger.info(String.format(msg, objs));
    }

    /**
     * Log a message, with no arguments.
     * <p>
     * If the logger is currently enabled for the given message
     * level then the given message is forwarded to all the
     * registered output Handler objects.
     * <p>
     *
     * @param level
     *         One of the message level identifiers, e.g., SEVERE
     * @param msg
     *         The string message (or a key in the message catalog)
     */
    public static void log(Level level, String msg) {
        logger.log(level, msg);
    }

    /**
     * Log a message, with one object parameter.
     * <p>
     * If the logger is currently enabled for the given message
     * level then a corresponding LogRecord is created and forwarded
     * to all the registered output Handler objects.
     * <p>
     *
     * @param level
     *         One of the message level identifiers, e.g., SEVERE
     * @param msg
     *         The string message (or a key in the message catalog)
     * @param param1
     *         parameter to the message
     */
    public static void log(Level level, String msg, Object param1) {
        logger.log(level, msg, param1);
    }

    /**
     * Log a message, with an array of object arguments.
     * <p>
     * If the logger is currently enabled for the given message
     * level then a corresponding LogRecord is created and forwarded
     * to all the registered output Handler objects.
     * <p>
     *
     * @param level
     *         One of the message level identifiers, e.g., SEVERE
     * @param msg
     *         The string message (or a key in the message catalog)
     * @param params
     *         array of parameters to the message
     */
    public static void log(Level level, String msg, Object[] params) {
        logger.log(level, msg, params);
    }

    /**
     * Log a message, with associated Throwable information.
     * <p>
     * If the logger is currently enabled for the given message
     * level then the given arguments are stored in a LogRecord
     * which is forwarded to all registered output handlers.
     * <p>
     * Note that the thrown argument is stored in the LogRecord thrown
     * property, rather than the LogRecord parameters property. Thus is it
     * processed specially by output Formatters and is not treated
     * as a formatting parameter to the LogRecord message property.
     * <p>
     *
     * @param level
     *         One of the message level identifiers, e.g., SEVERE
     * @param msg
     *         The string message (or a key in the message catalog)
     * @param thrown
     *         Throwable associated with log message.
     */
    public static void log(Level level, String msg, Throwable thrown) {
        logger.log(level, msg, thrown);
    }

    /**
     * @param prefix
     *         插件前缀
     */
    public static void setPrefix(String prefix) {
        Log.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
    }

    /**
     * Log a SEVERE message.
     * <p>
     * If the logger is currently enabled for the SEVERE message level then the
     * given message is forwarded to all the registered output Handler objects.
     *
     * @param msg
     *         The string message (or a key in the message catalog)
     */
    public static void severe(String msg) {
        logger.severe(msg);
    }

    /**
     * Sends this sender a message
     *
     * @param sender
     *         命令发送者
     * @param msg
     *         消息
     */
    public static void sender(CommandSender sender, String msg) {
        sender.sendMessage(prefix + msg);
    }

    /**
     * Sends this sender a message
     *
     * @param sender
     *         命令发送者
     * @param msg
     *         消息
     * @param objs
     *         参数
     */
    public static void sender(CommandSender sender, String msg, Object... objs) {
        sender.sendMessage(prefix + String.format(msg, objs));
    }

    /**
     * Sends this sender a message
     *
     * @param sender
     *         命令发送者
     * @param msg
     *         消息
     */
    public static void sender(CommandSender sender, String[] msg) {
        Arrays.stream(msg).forEach(str -> sender(sender, str));
    }

    /**
     * 格式化警告消息
     *
     * @param string
     *         消息
     */
    public static void w(String string) {
        logger.warning(string);
    }

    /**
     * 格式化警告消息
     *
     * @param string
     *         消息
     * @param objects
     *         参数
     */
    public static void w(String string, Object... objects) {
        w(String.format(string, objects));
    }

    /**
     * @return 是否为调试模式
     */
    public static boolean isDebug() {
        return debug;
    }

    /**
     * @return 是否为全局调试模式
     */
    public static boolean isGlobalDebug() {
        return globalDebug;
    }

    /**
     * 打印对象简易名称
     *
     * @param objects
     *         对象
     * @return
     */
    public static String getSimpleNames(Object... objects) {
        StringBuilder str = new StringBuilder("[");
        Arrays.stream(objects)
              .forEach(o -> str.append(Optional.ofNullable(o)
                                               .map(obj -> obj instanceof Class ? (Class) obj : obj.getClass())
                                               .map(Class::getSimpleName)
                                               .orElse(null)).append(", "));
        return objects.length == 0 ? "[]" : str.substring(0, str.length() - 2) + "]";
    }
}
