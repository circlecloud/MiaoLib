package pw.yumc.MiaoLib.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import pw.yumc.MiaoLib.bukkit.compatible.C;
import pw.yumc.MiaoLib.commands.info.CommandInfo;
import pw.yumc.MiaoLib.commands.info.CommandTabInfo;
import pw.yumc.MiaoLib.commands.interfaces.ErrorHanlder;
import pw.yumc.MiaoLib.commands.interfaces.Executor;
import pw.yumc.MiaoLib.commands.interfaces.HelpGenerator;
import pw.yumc.MiaoLib.commands.interfaces.HelpParse;
import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.bukkit.P;

/**
 * 子命令管理类
 *
 * @author 喵♂呜
 * @since 2016年7月23日 上午9:06:03
 */
public class CommandSub implements TabExecutor {
    private static String argumentTypeError = "注解命令方法 %s 位于 %s 的参数错误 第一个参数应实现 CommandSender 接口!";
    private static String returnTypeError = "注解命令补全 %s 位于 %s 的返回值错误 应实现 List 接口!";

    /**
     * 命令帮助
     */
    private CommandHelp help;
    /**
     * 插件命令
     */
    private PluginCommand cmd;
    /**
     * 插件实例类
     */
    private static JavaPlugin plugin = P.instance;
    /**
     * 默认命令
     */
    private CommandInfo defCmd = null;
    /**
     * 命令列表
     */
    private Set<CommandInfo> cmds = new HashSet<>();
    /**
     * Tab列表
     */
    private Set<CommandTabInfo> tabs = new HashSet<>();
    /**
     * 命令缓存列表
     */
    private Map<String, CommandInfo> cmdCache = new HashMap<>();
    /**
     * 命令名称缓存
     */
    private List<String> cmdNameCache = new ArrayList<>();

    public CommandSub() {
    }

    /**
     * 命令管理器
     *
     * @param name
     *         注册的命令
     */
    public CommandSub(String name) {
        cmd = plugin.getCommand(name);
        if (cmd == null) {
            if ((cmd = CommandKit.create(name)) == null) { throw new IllegalStateException("未找到命令 必须在plugin.yml先注册 " + name + " 命令!"); }
        }
        Log.d("初始化命令: %s", name);
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    /**
     * 命令管理器
     *
     * @param name
     *         注册的命令
     * @param executor
     *         命令执行类
     */
    public CommandSub(String name, Executor... executor) {
        this(name);
        register(executor);
    }

    /**
     * 构建命令列表缓存
     */
    private void buildCmdNameCache() {
        cmdNameCache.clear();
        cmds.forEach(cmd -> {
            cmdNameCache.add(cmd.getName());
            cmdNameCache.addAll(Arrays.asList(cmd.getCommand().aliases()));
        });
        cmdNameCache.add("help");
    }

    /**
     * 检查缓存并获得命令
     *
     * @param subcmd
     *         子命令
     * @return 命令信息
     */
    private CommandInfo getByCache(String subcmd) {
        if (!cmdCache.containsKey(subcmd)) {
            for (CommandInfo cmdinfo : cmds) {
                if (cmdinfo.isValid(subcmd)) {
                    cmdCache.put(subcmd, cmdinfo);
                    break;
                }
            }
            if (!cmdCache.containsKey(subcmd)) {
                cmdCache.put(subcmd, CommandInfo.Unknow);
            }
        }
        return cmdCache.get(subcmd);
    }

    /**
     * 获取玩家命令补全
     *
     * @param sender
     *         命令发送者
     * @param command
     *         命令
     * @param alias
     *         别名
     * @param args
     *         数组
     * @return 在线玩家数组
     */
    private List<String> getPlayerTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String lastWord = args[args.length - 1];
        Player senderPlayer = sender instanceof Player ? (Player) sender : null;
        List<String> matchedPlayers = new ArrayList<>();
        C.Player.getOnlinePlayers()
                .stream()
                .filter(player -> (senderPlayer == null || senderPlayer.canSee(player)) && StringUtil.startsWithIgnoreCase(player.getName(),
                                                                                                                           lastWord))
                .forEach(
                        player -> matchedPlayers.add(player.getName()));
        return matchedPlayers;
    }

    /**
     * 转移数组
     *
     * @param args
     *         原数组
     * @return 转移后的数组字符串
     */
    private String[] moveStrings(String[] args) {
        String[] ret = new String[args.length - 1];
        System.arraycopy(args, 1, ret, 0, ret.length);
        return ret;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (defCmd != null) { return defCmd.execute(sender, label, args); }
            return help.send(sender, label, args);
        }
        String subCmd = args[0].toLowerCase();
        if ("help".equalsIgnoreCase(subCmd)) { return help.send(sender, label, args); }
        CommandInfo cmd = getByCache(subCmd);
        String[] subargs = args;
        if (cmd.equals(CommandInfo.Unknow) && defCmd != null) {
            cmd = defCmd;
        } else {
            subargs = moveStrings(args);
        }
        return cmd.execute(sender, label, subargs);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        String token = args[args.length - 1];
        if (args.length == 1) {
            StringUtil.copyPartialMatches(token, cmdNameCache, completions);
        }
        tabs.forEach(tab -> StringUtil.copyPartialMatches(token, tab.execute(sender, token, args), completions));
        StringUtil.copyPartialMatches(token, getPlayerTabComplete(sender, command, alias, args), completions);
        completions.sort(String.CASE_INSENSITIVE_ORDER);
        return completions;
    }

    /**
     * 通过注解读取命令并注册
     *
     * @param clazzs
     *         子命令处理类
     * @return {@link CommandSub}
     */
    public void register(Executor... clazzs) {
        for (Executor clazz : clazzs) {
            Log.d("解析执行类: %s", clazz.getClass().getName());
            Method[] methods = clazz.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (registerCommand(method, clazz)) {
                    continue;
                }
                registerTab(method, clazz);
            }
        }
        help = new CommandHelp(defCmd, cmds);
        buildCmdNameCache();
    }

    /**
     * 注册命令
     *
     * @param method
     *         方法
     * @param clazz
     *         调用对象
     * @return 是否成功
     */
    private boolean registerCommand(Method method, Executor clazz) {
        CommandInfo ci = CommandInfo.parse(method, clazz);
        if (ci != null) {
            Class[] params = method.getParameterTypes();
            Log.d("注册子命令: %s 参数类型: %s", ci.getName(), Log.getSimpleNames(params));
            try {
                Class<? extends CommandSender> sender = params[0];
                // 用于消除unuse警告
                if (!sender.getName().isEmpty() && method.getReturnType() == boolean.class) {
                    defCmd = ci;
                } else {
                    cmds.add(ci);
                    cmdCache.put(ci.getName(), ci);
                }
                return true;
            } catch (ArrayIndexOutOfBoundsException | ClassCastException ignored) {
            }
            Log.w(argumentTypeError, method.getName(), clazz.getClass().getName());
        }
        return false;
    }

    /**
     * 注册Tab补全
     *
     * @param method
     *         方法
     * @param clazz
     *         调用对象
     * @return 是否成功
     */
    private void registerTab(Method method, Executor clazz) {
        CommandTabInfo ti = CommandTabInfo.parse(method, clazz);
        if (ti != null) {
            if (method.getReturnType().equals(List.class)) {
                Log.d("注册子命令补全: %s ", method.getName());
                tabs.add(ti);
            } else {
                Log.w(returnTypeError, method.getName(), clazz.getClass().getName());
            }
        }
    }

    /**
     * 设置命令错误处理器
     *
     * @param commandErrorHanlder
     *         命令错误处理器
     * @return {@link CommandSub}
     */
    public CommandSub setCommandErrorHanlder(ErrorHanlder commandErrorHanlder) {
        cmds.forEach(commandInfo -> commandInfo.setCommandErrorHandler(commandErrorHanlder));
        return this;
    }

    /**
     * 设置帮助生成器
     *
     * @param helpGenerator
     *         帮助生成器
     * @return {@link CommandSub}
     */
    public CommandSub setHelpGenerator(HelpGenerator helpGenerator) {
        help.setHelpGenerator(helpGenerator);
        return this;
    }

    /**
     * 设置帮助解析器
     *
     * @param helpParse
     *         帮助解析器
     * @return {@link CommandSub}
     */
    public CommandSub setHelpParse(HelpParse helpParse) {
        if (help.getHelpGenerator() instanceof CommandHelp.DefaultHelpGenerator) {
            ((CommandHelp.DefaultHelpGenerator) help.getHelpGenerator()).setHelpParse(helpParse);
        } else {
            Log.w("已设置自定义帮助生成器 解析器设置将不会生效!");
        }
        return this;
    }
}
