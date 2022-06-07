package pw.yumc.MiaoLib.commands.exception;

/**
 * 命令参数解析异常
 * 
 * @author 喵♂呜
 * @since 2016年10月5日 下午5:15:43
 */
public class PermissionException extends CommandException {

    public PermissionException(Exception e) {
        super(e);
    }

    public PermissionException(String string) {
        super(string);
    }

    public PermissionException(String string, Exception e) {
        super(string, e);
    }
}
