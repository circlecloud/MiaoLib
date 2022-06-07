package pw.yumc.MiaoLib.text;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

/**
 * 字符串滚动工具
 *
 * @since 2016年6月7日 下午2:22:12
 * @author 喵♂呜
 */
public class Scroller {
    private static final char COLOUR_CHAR = '\u00A7';
    private String content;
    private int position;
    private final List<String> list = new ArrayList<>();
    private ChatColor colour;

    /**
     * @param text
     *            需要显示的字符串
     */
    public Scroller(final String text) {
        this(text, 16);
    }

    /**
     * @param text
     *            需要显示的字符串
     * @param size
     *            每次显示长度
     */
    public Scroller(final String text, final int size) {
        this(text, size, 1);
    }

    /**
     * @param text
     *            需要显示的字符串
     * @param size
     *            每次显示长度
     * @param skip
     *            每次滚动长度
     */
    public Scroller(final String text, final int size, final int skip) {
        this(text, size, skip, '&');
    }

    /**
     * @param text
     *            需要显示的字符串
     * @param size
     *            每次显示长度
     * @param skip
     *            每次滚动长度
     * @param colorChar
     *            颜色字符串
     */
    public Scroller(String text, int size, int skip, final char colorChar) {
        this.colour = ChatColor.RESET;
        StringBuilder result;
        if (text.length() < size) {
            result = new StringBuilder(text);
            while (result.length() < size) {
                result.append(" ");
            }
            text = result.toString();
        }
        size -= 2;
        if (size < 1) {
            size = 1;
        }
        if (skip < 0) {
            skip = 0;
        }
        if (colorChar != COLOUR_CHAR) {
            text = ChatColor.translateAlternateColorCodes(colorChar, text);
        }
        for (int i = 0; i < text.length() - size; ++i) {
            this.list.add(text.substring(i, i + size));
        }
        result = new StringBuilder();
        for (int i = 0; i < skip; ++i) {
            System.out.println(i);
            list.add(text.substring(text.length() - size + (i > size ? size : i), text.length()) + result);
            if (result.length() < size) {
                result.append(" ");
            }
        }
        for (int i = 0; i < size - skip; ++i) {
            System.out.println(i);
            list.add(text.substring(text.length() - size + skip + i, text.length()) + result + text.substring(0, i));
        }
        for (int i = 0; i < skip && i <= result.length(); ++i) {
            System.out.println(i);
            list.add(result.substring(0, result.length() - i) + text.substring(0, size - (skip > size ? size : skip) + i));
        }
    }

    /**
     * 获得下一条显示的字符串
     *
     * @return 字符串
     */
    public String next() {
        StringBuilder result = this.getNext();
        if (result.charAt(result.length() - 1) == COLOUR_CHAR) {
            result.setCharAt(result.length() - 1, ' ');
        }
        if (result.charAt(0) == COLOUR_CHAR) {
            final ChatColor var2 = ChatColor.getByChar(result.charAt(1));
            if (var2 != null) {
                colour = var2;
                result = getNext();
                if (result.charAt(0) != 32) {
                    result.setCharAt(0, ' ');
                }
            }
        }
        return colour + result.toString();
    }

    /**
     * 从List获得下一条字符串
     *
     * @return 字符串
     */
    private StringBuilder getNext() {
        return new StringBuilder(this.list.get(this.position++ % this.list.size()));
    }
}
