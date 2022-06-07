package pw.yumc.MiaoLib.paste;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Pastebin {
    private static String POST_URL = "http://pastebin.com/api/api_post.php";
    private String API_KEY;

    public Pastebin() {
        this.API_KEY = "0e7d92011945cbcc1e884ab6e3e75e69";
    }

    public Pastebin(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public static void main(String[] args) {
        Pastebin p = new Pastebin();
        PasteContent paste = new PasteContent();
        paste.addLine("异常提交测试!");
        paste.addThrowable(new Throwable());
        System.out.println(p.post(paste));
    }

    public String post(PasteContent content) {
        return post("", PasteFormat.JAVA, Pastebin.Private.UNLISTED, content);
    }

    public String post(String name, PasteFormat format, Pastebin.Private level, PasteContent content) {
        String result = "Failed to post!";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(POST_URL).openConnection();
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
            connection.setInstanceFollowRedirects(false);
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            byte[] outByte = ("api_option=paste&api_dev_key=" + URLEncoder.encode(this.API_KEY, "utf-8") + "&api_paste_code=" + URLEncoder.encode(content.toString(), "utf-8") + "&api_paste_private="
                    + URLEncoder.encode(level.getLevel(), "utf-8") + "&api_paste_name=" + URLEncoder.encode(name, "utf-8") + "&api_paste_expire_date=" + URLEncoder.encode("N", "utf-8")
                    + "&api_paste_format=" + URLEncoder.encode(format.toString(), "utf-8") + "&api_user_key=" + URLEncoder.encode("", "utf-8")).getBytes();
            outputStream.write(outByte);
            outputStream.flush();
            outputStream.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder request = new StringBuilder();
            String temp;
            while ((temp = br.readLine()) != null) {
                request.append(temp);
                request.append("\r\n");
            }
            br.close();
            result = request.toString().trim();
            if (!result.contains("http://")) {
                result = "Failed to post! (returned result: " + result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public enum Private {
        PUBLIC(0),
        UNLISTED(1),
        PRIVATE(2);

        int level;

        Private(int level) {
            this.level = level;
        }

        public String getLevel() {
            return String.valueOf(level);
        }
    }
}
