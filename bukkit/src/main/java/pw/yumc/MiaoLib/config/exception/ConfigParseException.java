package pw.yumc.MiaoLib.config.exception;

/**
 * 配置注入解析异常
 *
 * @author 喵♂呜
 * @since 2016年10月5日 下午5:15:43
 */
public class ConfigParseException extends RuntimeException {

    public ConfigParseException(Exception e) {
        super(e);
    }

    public ConfigParseException(String string) {
        super(string);
    }

    public ConfigParseException(String string, Exception e) {
        super(string, e);
    }
}
