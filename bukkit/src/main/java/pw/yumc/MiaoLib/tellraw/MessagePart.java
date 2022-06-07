package pw.yumc.MiaoLib.tellraw;

/**
 * Tellraw消息块
 *
 * @since 2016年8月10日 下午7:10:36
 * @author 喵♂呜
 */
public class MessagePart {
    private static String TEXT_FORMAT = "\"text\":\"%s\"";
    private static String CLICK_FORMAT = "\"clickEvent\":{\"action\":\"%s\",\"value\":\"%s\"}";
    private static String HOVER_FORMAT = "\"hoverEvent\":{\"action\":\"%s\",\"value\":\"%s\"}";
    private static String INSERT_FORMAT = " \"insertion\":\"%s\"";
    /**
     * 消息文本
     */
    public String text;
    /**
     * 点击操作
     */
    public String clickActionName;
    /**
     * 点击数据
     */
    public String clickActionData;
    /**
     * 悬浮操作
     */
    public String hoverActionName;
    /**
     * 悬浮数据
     */
    public String hoverActionData;
    /**
     * 插入数据
     */
    public String insertionData;

    public MessagePart() {
        this("");
    }

    public MessagePart(String text) {
        this.text = text;
    }

    /**
     * @return 是否有文本
     */
    public boolean hasText() {
        return text != null && !text.isEmpty();
    }

    /**
     * 写入Json
     *
     * @param str
     *            流对象
     */
    public void writeJson(StringBuilder str) {
        str.append("{");
        str.append(String.format(TEXT_FORMAT, new JsonBuilder(text)));
        if (clickActionName != null) {
            str.append(",");
            str.append(String.format(CLICK_FORMAT, clickActionName, new JsonBuilder(clickActionData)));
        }
        if (hoverActionName != null) {
            str.append(",");
            str.append(String.format(HOVER_FORMAT, hoverActionName, new JsonBuilder(hoverActionData)));
        }
        if (insertionData != null) {
            str.append(",");
            str.append(String.format(INSERT_FORMAT, new JsonBuilder(insertionData)));
        }
        str.append("}");
    }
}
