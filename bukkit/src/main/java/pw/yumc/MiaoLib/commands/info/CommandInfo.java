package pw.yumc.MiaoLib.commands.info;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import pw.yumc.MiaoLib.commands.exception.ArgumentException;
import pw.yumc.MiaoLib.commands.exception.CommandException;
import pw.yumc.MiaoLib.commands.exception.SenderException;
import pw.yumc.MiaoLib.bukkit.P;
import pw.yumc.MiaoLib.commands.CommandError;
import pw.yumc.MiaoLib.commands.CommandParse;
import pw.yumc.MiaoLib.commands.annotation.Async;
import pw.yumc.MiaoLib.commands.annotation.Cmd;
import pw.yumc.MiaoLib.commands.annotation.Cmd.Executor;
import pw.yumc.MiaoLib.commands.annotation.Help;
import pw.yumc.MiaoLib.commands.annotation.Sort;
import pw.yumc.MiaoLib.commands.exception.PermissionException;
import pw.yumc.MiaoLib.commands.interfaces.ErrorHanlder;

/**
 * 命令信息存储类
 *
 * @author 喵♂呜
 * @since 2016年7月23日 上午9:56:42
 */
public class CommandInfo {
    public static CommandInfo Unknow = new CommandInfo();
    private static Help defHelp = new Help() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return Help.class;
        }

        @Override
        public String possibleArguments() {
            return "这家伙很懒";
        }

        @Override
        public String value() {
            return "没写帮助信息";
        }
    };
    private Object origin;
    private Method method;
    private String name;
    private List<String> aliases;
    private List<Executor> executors;
    private String executorStr;
    private boolean async;
    private boolean main;
    private Cmd command;
    private Help help;
    private Integer sort;
    private CommandParse parse;
    /**
     * 命令错误处理
     */
    private ErrorHanlder commandErrorHandler = new CommandError();

    public CommandInfo(Method method, Object origin, Cmd command, Help help, boolean async, int sort, CommandParse parse) {
        this.method = method;
        this.origin = origin;
        this.name = "".equals(command.value()) ? method.getName().toLowerCase() : command.value();
        this.aliases = Arrays.asList(command.aliases());
        this.executors = Arrays.asList(command.executor());
        this.executorStr = eS(executors);
        this.command = command;
        this.help = help != null ? help : defHelp;
        this.async = async;
        this.main = method.getReturnType().equals(boolean.class);
        this.sort = sort;
        this.parse = parse;
    }

    private CommandInfo() {
        this.method = null;
        this.origin = null;
        this.name = "unknow";
        this.aliases = null;
        this.executors = null;
        this.executorStr = null;
        this.command = null;
        this.help = null;
        this.async = false;
        this.main = false;
        this.sort = 0;
        this.parse = null;
    }

    /**
     * 解析CommandInfo
     *
     * @param method
     *         方法
     * @param origin
     *         源对象
     * @return {@link CommandInfo}
     */
    public static CommandInfo parse(Method method, Object origin) {
        Cmd command = method.getAnnotation(Cmd.class);
        if (command != null) {
            method.setAccessible(true);
            Help help = method.getAnnotation(Help.class);
            Async async = method.getAnnotation(Async.class);
            Sort sort = method.getAnnotation(Sort.class);
            CommandParse cp = CommandParse.get(method);
            return new CommandInfo(method, origin, command, help, async != null, sort != null ? sort.value() : 50, cp);
        }
        return null;
    }

    /**
     * 执行命令
     *
     * @param sender
     *         命令发送者
     * @param label
     *         命令标签
     * @param args
     *         参数
     * @return 是否执行成功
     */
    public boolean execute(final CommandSender sender, final String label, final String[] args) {
        if (method == null) { return false; }
        Runnable runnable = () -> {
            try {
                check(sender, label, args);
                method.invoke(origin, parse.parse(sender, label, args));
            } catch (Exception e) {
                if (e.getCause() instanceof CommandException) { e = (Exception) e.getCause(); }
                if (e instanceof CommandException) {
                    commandErrorHandler.error((CommandException) e, sender, this, label, args);
                    return;
                }
                throw new RuntimeException(e);
            }
        };
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(P.instance, runnable);
        } else {
            runnable.run();
        }
        return true;
    }

    /**
     * @return 命令注解
     */
    public Cmd getCommand() {
        return command;
    }

    /**
     * @return 帮助注解
     */
    public Help getHelp() {
        return help;
    }

    /**
     * @return 命令名称
     */
    public String getName() {
        return name;
    }

    /**
     * @return 命令别名
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * @return 命令排序
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * @return 是否为异步命令
     */
    public boolean isAsync() {
        return async;
    }

    /**
     * @return 是否为主命令
     */
    public boolean isMain() {
        return main;
    }

    /**
     * @return 允许的命令发送者
     */
    public String getExecutorStr() {
        return executorStr;
    }

    /**
     * 验证命令是否匹配
     *
     * @param cmd
     *         需验证命令
     * @return 是否匹配
     */
    public boolean isValid(String cmd) {
        return name.equalsIgnoreCase(cmd) || aliases.contains(cmd);
    }

    /**
     * 设置为主命令
     */
    public void setMain() {
        this.main = true;
    }

    /**
     * 设置命令错误处理器
     *
     * @param commandErrorHandler
     *         命令错误处理器
     */
    public void setCommandErrorHandler(ErrorHanlder commandErrorHandler) {
        this.commandErrorHandler = commandErrorHandler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandInfo that = (CommandInfo) o;
        return Objects.equals(origin, that.origin) && Objects.equals(method, that.method) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, method, name);
    }

    private void check(CommandSender sender, String label, String[] args) {
        if (!executors.contains(Executor.ALL) && !executors.contains(Executor.valueOf(sender))) { throw new SenderException(executorStr); }
        if (!"".equals(command.permission()) && !sender.hasPermission(command.permission())) { throw new PermissionException(command.permission()); }
        if (args.length < command.minimumArguments()) { throw new ArgumentException(String.valueOf(command.minimumArguments())); }
    }

    private String eS(List<Executor> executors) {
        StringBuilder str = new StringBuilder();
        for (Executor executor : executors) {
            str.append(executor.getName());
            str.append(", ");
        }
        return str.toString().substring(0, str.length() - 2);
    }
}
