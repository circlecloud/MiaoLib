package pw.yumc.MiaoLib.paste;

/**
 * 代码格式
 *
 * @since 2016年9月18日 下午7:00:15
 * @author 喵♂呜
 */
public enum PasteFormat {
    JAVA("java"),
    JAVASCRIPT("javascript"),
    HTML("html"),
    YAML("yaml");

    String format;

    PasteFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return format;
    }
}