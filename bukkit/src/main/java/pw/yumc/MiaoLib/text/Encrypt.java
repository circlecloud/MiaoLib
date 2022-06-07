package pw.yumc.MiaoLib.text;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;

/**
 * 加密工具类
 * 
 * @author 喵♂呜
 * @since 2017/4/22
 */
public class Encrypt {
    private static String key;
    static {
        try {
            key = URLDecoder.decode("%E5%96%B5%E2%99%82%E5%91%9C", "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    /**
     * 解密地址
     *
     * @param s
     *            密串
     * @return 解密后的地址
     */
    public static String decode(String s) {
        return decode(key, s);
    }

    /**
     * 加密地址
     *
     * @param s
     *            密串
     * @return 加密后的地址
     */
    public static String encode(String s) {
        return encode(key, s);
    }

    /**
     * 解密地址
     * 
     * @param key
     *            密钥
     * @param s
     *            密串
     * @return 解密后的地址
     */
    public static String decode(String key, String s) {
        return process(key, s, false);
    }

    /**
     * 加密地址
     *
     * @param key
     *            密钥
     * @param s
     *            密串
     * @return 加密后的地址
     */
    public static String encode(String key, String s) {
        return process(key, s, true);
    }

    private static String process(String key, String s, boolean isEncode) {
        StringBuilder str = new StringBuilder();
        int ch;
        for (int i = 0, j = 0; i < s.length(); i++, j++) {
            if (j > key.length() - 1) {
                j = j % key.length();
            }
            if (isEncode) {
                ch = s.codePointAt(i) + key.codePointAt(j);
            } else {
                ch = s.codePointAt(i) + 65535 - key.codePointAt(j);
            }
            if (ch > 65535) {
                ch = ch % 65535;// ch - 33 = (ch - 33) % 95 ;
            }
            str.append((char) ch);
        }
        return str.toString();
    }

    @Test
    public void test() {
        //String src = "http://ci.yumc.pw/job/%1$s/lastSuccessfulBuild/artifact/target/%1$s.jar";
        //String dest = "­­¥l`c¢c«¦¡g¥©`¨dWbX¬h¡¤¨®§¬ªs©¢¥a¦­¢¨h­¤­hZcU§g£¤";
        //private static String direct = d("­­¥l`c¢c«¦¡g¥©`¨dWbX¬h¡¤¨®§¬ªs©¢¥a¦­¢¨h­¤­hZcU§g£¤");
        // private static String direct = "http://ci.yumc.pw/job/%1$s/lastSuccessfulBuild/artifact/target/%1$s.jar";
        //private static String maven = d("­­¥l`c¢c«¦¡g¥©`¤¥®c«¥¡¤­¨§«`¯§«¥¢§aVe]¬dWcX¬hZeU§f^gV¤b£§");
        // private static String maven = "http://ci.yumc.pw/plugin/repository/everything/%1$s/%2$s/%3$s-%2$s.jar";
        String src = "Authorization";
        //String dest = "嘝⚶哐嘥♼咋嗤⚥哅嗣⚻哑嘢⚥咊嘥⚹咋嘟⚱咾嗤⚏哅嘖⚱咮嘚⚲哈嘖⚥品嗤⚹哏嗤⚶咽嘧⚩品嘩♱咩嘞⚣哋嘇⚧哌嘡⚣咿嘚♰哆嘖⚴";
        System.out.println("\"" + encode(src) + "\"");
        //System.out.println("\"" + decode(dest) + "\"");
    }

    @Test
    public void t() {
        System.out.println(decode("499521", "b¨¬ £ "));
    }
}
