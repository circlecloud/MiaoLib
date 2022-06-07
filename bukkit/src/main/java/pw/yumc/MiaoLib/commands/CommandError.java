package pw.yumc.MiaoLib.commands;

import org.bukkit.command.CommandSender;

import pw.yumc.MiaoLib.commands.info.CommandInfo;
import pw.yumc.MiaoLib.commands.interfaces.ErrorHanlder;
import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.commands.exception.ArgumentException;
import pw.yumc.MiaoLib.commands.exception.CommandException;
import pw.yumc.MiaoLib.commands.exception.ParseException;
import pw.yumc.MiaoLib.commands.exception.PermissionException;
import pw.yumc.MiaoLib.commands.exception.SenderException;

/**
 * 命令错误处理
 *
 * @author 喵♂呜
 * @since 2016/11/27 0027
 */
public class CommandError implements ErrorHanlder {
    private static String onlyExecutor = "§c当前命令仅允许 §b%s §c执行!";
    private static String losePerm = "§c你需要有 %s 的权限才能执行此命令!";
    private static String cmdErr = "§6错误原因: §4命令参数不正确!";
    private static String cmdUse = "§6使用方法: §e/%s %s%s";
    private static String cmdDes = "§6命令描述: §3%s";
    private static String argErr = "§c参数错误: §4%s";
    private static String execErr = "§c命令执行失败: §4%s";

    @Override
    public void error(CommandException e, CommandSender sender, CommandInfo info, String label, String[] args) {
        if (e == null) { return; }
        if (e instanceof SenderException) {
            Log.sender(sender, onlyExecutor, info.getExecutorStr());
        } else if (e instanceof PermissionException) {
            Log.sender(sender, losePerm, info.getCommand().permission());
        } else if (e instanceof ArgumentException) {
            Log.sender(sender, cmdErr);
            Log.sender(sender, cmdUse, label, info.isMain() ? "" : info.getName() + " ", info.getHelp().possibleArguments());
            Log.sender(sender, cmdDes, info.getHelp().value());
        } else if (e instanceof ParseException) {
            Log.sender(sender, argErr, e.getMessage());
        } else {
            Log.sender(sender, execErr, e.getMessage());
        }
    }
}
