package pw.yumc.MiaoLib.commands.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

/**
 * 命令注解
 *
 * <pre>
 * 参数名称            描述       默认值
 * name              命令名称     方法名称
 * aliases           命令别名
 * minimumArguments  最小参数     默认0
 * permission        权限
 * executor          执行者       所有
 * </pre>
 *
 * @since 2016年7月23日 上午8:59:05
 * @author 喵♂呜
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cmd {
    /**
     * @return 命令别名
     */
    String[] aliases() default "";

    /**
     * @return 命令执行者
     */
    Executor[] executor() default Executor.ALL;

    /**
     * @return 命令最小参数
     */
    int minimumArguments() default 0;

    /**
     * @return 当前命令权限
     */
    String permission() default "";

    /**
     * @return 命令名称
     */
    String value() default "";

    /**
     * 命令执行者
     *
     * @author 喵♂呜
     * @since 2016年8月26日 下午8:55:15
     */
    enum Executor {
        /**
         * 玩家
         */
        PLAYER("玩家"),
        /**
         * 控制台
         */
        CONSOLE("控制台"),
        /**
         * 命令方块
         */
        BLOCK("命令方块"),
        /**
         * 命令矿车
         */
        COMMANDMINECART("命令矿车"),
        /**
         * 远程控制台
         */
        REMOTECONSOLE("远程控制台"),
        /**
         * 所有
         */
        ALL("所有执行者"),
        /**
         * 未知
         */
        UNKNOW("未知");
        private String name;

        Executor(String name) {
            this.name = name;
        }

        /**
         * 解析Executor
         *
         * @param sender
         *            命令执行者
         * @return {@link Executor}
         */
        public static Executor valueOf(CommandSender sender) {
            if (sender instanceof Player) {
                return Executor.PLAYER;
            } else if (sender instanceof ConsoleCommandSender) {
                return Executor.CONSOLE;
            } else if (sender instanceof BlockCommandSender) {
                return Executor.BLOCK;
            } else if (sender instanceof CommandMinecart) {
                return Executor.COMMANDMINECART;
            } else if (sender instanceof RemoteConsoleCommandSender) {
                return Executor.REMOTECONSOLE;
            } else {
                return Executor.UNKNOW;
            }
        }

        /**
         * @return 执行者名称
         */
        public String getName() {
            return name;
        }
    }
}
