package pw.yumc.MiaoLib.commands.exception;

/**
 * 命令参数解析异常
 * 
 * @author 喵♂呜
 * @since 2016年10月5日 下午5:15:43
 */
public class SenderException extends CommandException {

    public SenderException(Exception e) {
        super(e);
    }

    public SenderException(String string) {
        super(string);
    }

    public SenderException(String string, Exception e) {
        super(string, e);
    }
}
