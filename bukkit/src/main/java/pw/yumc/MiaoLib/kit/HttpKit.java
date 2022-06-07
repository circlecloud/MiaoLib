package pw.yumc.MiaoLib.kit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lombok.Builder;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;

/**
 * HttpKit
 */
public class HttpKit {

    public enum HttpMethod {
        GET,
        POST,
        PUT,
        HEADER,
        DELETE,
        PATCH
    }

    private static String CHARSET = "UTF-8";

    private static SSLSocketFactory sslSocketFactory = initSSLSocketFactory();
    private static TrustAnyHostnameVerifier trustAnyHostnameVerifier = new HttpKit.TrustAnyHostnameVerifier();

    private HttpKit() {
    }

    /**
     * Get 方法获取HTML
     *
     * @param url
     *            网址
     * @return 网页HTML
     */
    public static String get(String url) {
        return get(url, null);
    }

    /**
     * Get 方法获取HTML
     *
     * @param url
     *            网址
     * @param queryParas
     *            查询参数
     * @return 网页HTML
     */
    public static String get(String url, Map<String, String> queryParas) {
        return get(url, queryParas, null);
    }

    /**
     * Get 方法获取HTML
     *
     * @param url
     *            网址
     * @param queryParas
     *            查询参数
     * @param headers
     *            头信息
     * @return 网页HTML
     */
    public static String get(String url, Map<String, String> queryParas, Map<String, String> headers) {
        return send(HttpInfo.builder().url(url).method(HttpMethod.GET).query(queryParas).header(headers).build());
    }

    /**
     * Post 方法获取HTML
     *
     * @param url
     *            网址
     * @param data
     *            查询参数
     * @return 网页HTML
     */
    public static String post(String url, String data) {
        return post(url, data, null);
    }

    /**
     * Post 方法获取HTML
     *
     * @param url
     *            网址
     * @param data
     *            查询参数
     * @param header
     *            头信息
     * @return 网页HTML
     */
    public static String post(String url, String data, Map<String, String> header) {
        return send(HttpInfo.builder().url(url).data(data).method(HttpMethod.POST).header(header).build());
    }

    /**
     * DELETE 方法
     * 
     * @param url
     *            地址
     * @param header
     *            头信息
     * @return
     */
    public static String delete(String url, Map<String, String> header) {
        return send(HttpInfo.builder().url(url).method(HttpMethod.DELETE).header(header).build());
    }

    /**
     * Post 获取网页
     *
     * @param info
     *            链接信息
     * @return 网页HTML
     */
    @SneakyThrows
    public static String send(HttpInfo info) {
        HttpURLConnection conn = getHttpConnection(buildUrlWithQueryString(info), info.getMethod().name(), info.getHeader());
        try {
            conn.connect();
            if (StrKit.notBlank(info.getData())) {
                OutputStream out = conn.getOutputStream();
                out.write(info.getData().getBytes(CHARSET));
                out.flush();
                out.close();
            }
            return readResponseString(conn);
        } finally {
            conn.disconnect();
        }
    }

    @Data
    @Builder
    public static class HttpInfo {
        private String url;
        private HttpMethod method;
        private Map<String, String> query;
        private String data;
        private Map<String, String> header;
    }

    /**
     * 构建查询串为字符串
     *
     * @param info
     *            网址
     * @return 构建后的地址
     */
    private static String buildUrlWithQueryString(HttpInfo info) {
        val url = info.getUrl();
        val query = info.getQuery();
        if (query == null || query.isEmpty()) { return url; }
        StringBuilder sb = new StringBuilder(url);
        if (!url.contains("?")) {
            sb.append("?");
        }
        query.entrySet().stream().filter(entry -> StrKit.isBlank(entry.getValue())).forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                value = URLEncoder.encode(value, CHARSET);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            sb.append(key).append("=").append(value);
        });
        return sb.toString().substring(0, sb.length() - 1);
    }

    /**
     * 获得HTTP链接
     *
     * @param url
     *            地址
     * @param method
     *            方法
     * @param headers
     *            头信息
     * @return HTTP链接
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws KeyManagementException
     */
    private static HttpURLConnection getHttpConnection(String url, String method, Map<String, String> headers) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        URL _url = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
            ((HttpsURLConnection) conn).setHostnameVerifier(trustAnyHostnameVerifier);
        }

        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        conn.setDoInput(true);

        conn.setConnectTimeout(19000);
        conn.setReadTimeout(19000);

        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");

        if (headers != null && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        return conn;
    }

    /**
     * 获得SSL安全套接字
     *
     * @return 安全套接字工厂
     */
    @SneakyThrows
    private static SSLSocketFactory initSSLSocketFactory() {
        TrustManager[] tm = { new HttpKit.TrustAnyTrustManager() };
        SSLContext sslContext = SSLContext.getInstance("TLS", "SunJSSE");
        sslContext.init(null, tm, new java.security.SecureRandom());
        return sslContext.getSocketFactory();
    }

    /**
     * 从连接读取HTML
     *
     * @param conn
     *            HTTP连接
     * @return 字符串
     */
    @SneakyThrows
    private static String readResponseString(HttpURLConnection conn) {
        StringBuilder sb = new StringBuilder();
        @Cleanup
        InputStream inputStream = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * https 域名校验
     *
     * @author 喵♂呜
     * @since 2016年4月1日 下午10:36:01
     */
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * https 证书管理
     *
     * @author 喵♂呜
     * @since 2016年4月1日 下午10:36:05
     */
    private static class TrustAnyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
