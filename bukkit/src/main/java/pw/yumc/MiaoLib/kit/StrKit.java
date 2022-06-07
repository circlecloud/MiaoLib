package pw.yumc.MiaoLib.kit;

import java.util.Collection;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

/**
 * 字符串工具类
 *
 * @author 喵♂呜
 * @since 2016年9月14日 上午1:02:23
 */
public class StrKit {
    private static String EMPTY = "";

    private StrKit() {
    }

    /**
     * @param string
     *            源字串
     * @return 颜色转化后的字串
     */
    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * 转移数组后获取字符串
     *
     * @param args
     *            原数组
     * @param start
     *            数组开始位置
     * @return 转移后的数组字符串
     */
    public static String consolidateStrings(String[] args, int start) {
        String ret = args[start];
        if (args.length > start + 1) {
            for (int i = start + 1; i < args.length; i++) {
                ret = ret + " " + args[i];
            }
        }
        return ret;
    }

    /**
     * Copies all elements from the iterable collection of originals to the
     * collection provided.
     *
     * @param <T>
     *            the collection of strings
     * @param token
     *            String to search for
     * @param originals
     *            An iterable collection of strings to filter.
     * @param collection
     *            The collection to add matches to
     * @return the collection provided that would have the elements copied
     *         into
     * @throws UnsupportedOperationException
     *             if the collection is immutable
     *             and originals contains a string which starts with the specified
     *             search string.
     * @throws IllegalArgumentException
     *             if any parameter is is null
     * @throws IllegalArgumentException
     *             if originals contains a null element.
     *             <b>Note: the collection may be modified before this is thrown</b>
     */
    public static <T extends Collection<? super String>> T copyPartialMatches(String token, Iterable<String> originals, T collection) throws UnsupportedOperationException, IllegalArgumentException {
        Validate.notNull(token, "Search token cannot be null");
        Validate.notNull(collection, "Collection cannot be null");
        Validate.notNull(originals, "Originals cannot be null");

        for (String string : originals) {
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string);
            }
        }

        return collection;
    }

    /**
     * @param str
     *            字串
     * @return 是否为空字串
     */
    public static boolean isBlank(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 转化数组为字符串
     *
     * @param arr
     *            数组
     * @return 字符串
     */
    public static String join(Object[] arr) {
        return join(arr, EMPTY);
    }

    /**
     * 转化数组为字符串
     *
     * @param arr
     *            数组
     * @param split
     *            分割符
     * @return 字符串
     */
    public static String join(Object[] arr, String split) {
        StringBuilder str = new StringBuilder();
        for (Object s : arr) {
            str.append(s.toString());
            str.append(split);
        }
        return str.length() > split.length() ? str.toString().substring(0, str.length() - split.length()) : str.toString();
    }

    /**
     * @param str
     *            字串
     * @return 是否不为空字串
     */
    public static boolean notBlank(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * This method uses a region to check case-insensitive equality. This
     * means the internal array does not need to be copied like a
     * toLowerCase() call would.
     *
     * @param string
     *            String to check
     * @param prefix
     *            Prefix of string to compare
     * @return true if provided string starts with, ignoring case, the prefix
     *         provided
     * @throws NullPointerException
     *             if prefix is null
     * @throws IllegalArgumentException
     *             if string is null
     */
    public static boolean startsWithIgnoreCase(String string, String prefix) throws IllegalArgumentException, NullPointerException {
        Validate.notNull(string, "Cannot check a null string for a match");
        return string.length() >= prefix.length() && string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /**
     * <p>
     * Gets a substring from the specified String avoiding exceptions.
     * </p>
     *
     * <p>
     * A negative start position can be used to start/end <code>n</code>
     * characters from the end of the String.
     * </p>
     *
     * <p>
     * The returned substring starts with the character in the <code>start</code>
     * position and ends before the <code>end</code> position. All position counting is
     * zero-based -- i.e., to start at the beginning of the string use
     * <code>start = 0</code>. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.
     * </p>
     *
     * <p>
     * If <code>start</code> is not strictly to the left of <code>end</code>, ""
     * is returned.
     * </p>
     *
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", * ,  *)    = "";
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, 4)   = "c"
     * StringUtils.substring("abc", 4, 6)   = ""
     * StringUtils.substring("abc", 2, 2)   = ""
     * StringUtils.substring("abc", -2, -1) = "b"
     * StringUtils.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str
     *            the String to get the substring from, may be null
     * @param start
     *            the position to start from, negative means
     *            count back from the end of the String by this many characters
     * @param end
     *            the position to end at (exclusive), negative means
     *            count back from the end of the String by this many characters
     * @return substring from start position to end positon,
     *         <code>null</code> if null String input
     */
    public static String substring(String str, int start, int end) {
        if (str == null) { return null; }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) { return EMPTY; }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }
}
