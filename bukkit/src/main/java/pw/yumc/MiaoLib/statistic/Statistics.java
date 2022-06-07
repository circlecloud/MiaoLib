/*
 * Copyright 2011-2015 喵♂呜. All rights reserved.
 */
package pw.yumc.MiaoLib.statistic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Yum数据中心 数据统计类
 *
 * @since 2015年12月14日 下午1:36:42
 * @author 喵♂呜
 */
public class Statistics {
    /**
     * 统计系统版本
     */
    private static int REVISION = 10;

    /**
     * 统计插件基础配置文件
     */
    private static File configfile = new File(String.format("plugins%1$sPluginHelper%1$sconfig.yml", File.separatorChar));

    /**
     * UTF-8编码
     */
    private static Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * getOnlinePlayers方法
     */
    private static Method getOnlinePlayers;

    /**
     * 插件实体
     */
    private static Plugin plugin;

    static {
        try {
            getOnlinePlayers = Bukkit.class.getDeclaredMethod("getOnlinePlayers");
            if (getOnlinePlayers.getReturnType() != Player[].class) {
                for (Method method : Bukkit.class.getDeclaredMethods()) {
                    if (method.getReturnType() == Player[].class && method.getName().endsWith("getOnlinePlayers")) {
                        getOnlinePlayers = method;
                    }
                }
            }
            Object pluginClassLoader = Statistics.class.getClassLoader();
            Field field = pluginClassLoader.getClass().getDeclaredField("plugin");
            field.setAccessible(true);
            plugin = (JavaPlugin) field.get(pluginClassLoader);
        } catch (NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {
        }
    }

    /**
     * 统计插件基础配置文件
     */
    private YamlConfiguration config;

    /**
     * 调试模式
     */
    private boolean debug;

    /**
     * 唯一服务器编码
     */
    private String guid;

    /**
     * 线程任务
     */
    private volatile BukkitTask task = null;

    /**
     * 统计线程
     */
    private volatile StatisticsTimer timer = null;

    /**
     * 插件使用数据统计
     */
    public Statistics() {
        try {
            if (!configfile.exists()) {
                configfile.getParentFile().mkdirs();
                configfile.createNewFile();
            }
            config = YamlConfiguration.loadConfiguration(configfile);
            initFile(config);
        } catch (IOException ignored) {
        }
        this.guid = config.getString("guid");
        this.debug = config.getBoolean("d", false);
        start();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数
     * @return 所代表远程资源的响应结果
     * @throws IOException
     *             IO异常
     */
    public static String postData(String url, String param) throws IOException {
        PrintWriter out;
        String result = "";
        URL realUrl = new URL(url);
        // 打开和URL之间的连接
        URLConnection conn = realUrl.openConnection();
        // 设置通用的请求属性
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
        // 设置超时时间 10秒
        conn.setReadTimeout(10000);
        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true);
        conn.setDoInput(true);
        // 获取URLConnection对象对应的输出流
        out = new PrintWriter(conn.getOutputStream());
        // 发送请求参数
        out.write(param);
        // flush输出流的缓冲
        out.flush();
        String response;
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), UTF_8));
        while ((response = reader.readLine()) != null) {
            result += response;
        }
        reader.close();
        out.close();
        return result;
    }

    /**
     * 初始化配置文件
     *
     * @param config
     *            配置文件
     * @throws IOException
     *             IO异常
     */
    private static void initFile(YamlConfiguration config) throws IOException {
        if (config.getString("guid") == null) {
            config.options().header("YUMC数据中心 http://www.yumc.pw 收集的数据仅用于统计插件使用情况").copyDefaults(true);
            config.set("guid", UUID.randomUUID().toString());
            config.set("d", false);
            config.save(configfile);
        }
        if (!config.contains("YumAccount")) {
            config.set("YumAccount.username", "Username Not Set");
            config.set("YumAccount.password", "Password NotSet");
            config.save(configfile);
        }
        if (!config.contains("TellrawManualHandle")) {
            config.set("TellrawManualHandle", false);
            config.save(configfile);
        }
    }

    /**
     * 简化输出
     *
     * @param msg
     *            输出对象
     */
    public void print(String msg) {
        if (debug) {
            System.out.println("[Statistics] " + msg);
        }
    }

    /**
     * 开启数据统计 这将会在异步执行
     *
     * @return 是否运行成功.
     */
    public boolean start() {
        if (task != null || !plugin.isEnabled()) { return true; }
        timer = new StatisticsTimer();
        // 开启TPS统计线程
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, timer, 0, 20);
        // 开启发送数据线程
        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                postPlugin();
            } catch (Throwable e) {
                if (debug) {
                    e.printStackTrace();
                }
            }
        }, 50, 25 * 1200);
        return true;
    }

    /**
     * 获得在线玩家人数
     *
     * @return 在线玩家人数
     */
    private int getOnlinePlayerNumber() {
        try {
            return ((Player[]) getOnlinePlayers.invoke(Bukkit.getServer())).length;
        } catch (Exception ex) {
            return Bukkit.getOnlinePlayers().size();
        }
    }

    /**
     * 发送服务器数据到统计网页
     */
    private void postPlugin() throws IOException {
        // 服务器数据获取
        PluginDescriptionFile description = plugin.getDescription();
        String pluginname = description.getName();
        String tmposarch = System.getProperty("os.arch");

        Map<String, Object> data = new HashMap<>();
        data.put("guid", guid);
        data.put("server_version", Bukkit.getVersion());
        data.put("server_port", Bukkit.getServer().getPort());
        data.put("server_tps", timer.getAverageTPS());
        data.put("plugin_version", description.getVersion());
        data.put("players_online", getOnlinePlayerNumber());
        data.put("os_name", System.getProperty("os.name"));
        data.put("os_arch", "amd64".equalsIgnoreCase(tmposarch) ? "x86_64" : tmposarch);
        data.put("os_version", System.getProperty("os.version"));
        data.put("os_usemem", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024);
        data.put("os_cores", Runtime.getRuntime().availableProcessors());
        data.put("auth_mode", Bukkit.getServer().getOnlineMode() ? 1 : 0);
        data.put("java_version", System.getProperty("java.version"));

        String jsondata = "Info=" + JSONValue.toJSONString(data);

        String url = String.format("http://api.yumc.pw/I/P/S/V/%s/P/%s", REVISION, URLEncoder.encode(pluginname, "UTF-8"));
        print("Plugin: " + pluginname + " Send Data To CityCraft Data Center");
        print("Address: " + url);
        print("Data: " + jsondata);
        // 发送数据
        JSONObject result = (JSONObject) JSONValue.parse(postData(url, jsondata));
        print("Plugin: " + pluginname + " Recover Data From CityCraft Data Center: " + result.get("info"));
    }

    public class StatisticsTimer implements Runnable {
        private LinkedList<Double> history = new LinkedList<>();
        private transient long lastPoll = System.nanoTime();

        /**
         * @return 获得TPS
         */
        public double getAverageTPS() {
            double avg = 0.0D;
            for (Double f : history) {
                avg += f;
            }
            return avg / history.size();
        }

        @Override
        public void run() {
            long startTime = System.nanoTime();
            long timeSpent = (startTime - lastPoll) / 1000;
            if (history.size() > 10) {
                history.removeFirst();
            }
            double ttps = 2.0E7D / (timeSpent == 0 ? 1 : timeSpent);
            if (ttps <= 21.0D) {
                history.add(ttps);
            }
            lastPoll = startTime;
        }
    }
}