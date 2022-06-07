package pw.yumc.MiaoLib.update;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.bukkit.P;
import pw.yumc.MiaoLib.tellraw.Tellraw;
import pw.yumc.MiaoLib.text.Encrypt;

/**
 * 自动更新程序
 *
 * @author 喵♂呜
 * @since 2016年8月3日 上午11:20:21
 */
public class SubscribeTask implements Runnable, Listener {
    /**
     * 插件实例
     */
    private static JavaPlugin instance = P.instance;
    /**
     * 是否禁止更新
     */
    private static boolean notUpdate = new File(String.format("plugins%1$sYumCore%1$snotupdate", File.separatorChar)).exists();
    /**
     * 检查间隔
     */
    private static int interval = 25;

    private UpdateType updateType;
    /**
     * 更新文件
     */
    private UpdateFile updateFile;
    /**
     * 版本信息
     */
    private VersionInfo versionInfo;

    /**
     * 自动更新
     */
    public SubscribeTask() {
        this(UpdateType.DIRECT);
    }

    /**
     * 自动更新
     *
     * @param type
     *         是否为Maven
     */
    public SubscribeTask(UpdateType type) {
        this(false, type);
    }

    /**
     * 自动更新
     *
     * @param isSecret
     *         是否为私有
     * @param type
     *         更新类型
     */
    public SubscribeTask(boolean isSecret, UpdateType type) {
        this("master", isSecret, type);
    }

    /**
     * 自动更新
     *
     * @param branch
     *         更新分支
     * @param isSecret
     *         是否为私有
     * @param type
     *         更新类型
     */
    public SubscribeTask(String branch, boolean isSecret, UpdateType type) {
        Log.d("订阅更新 分支 %s 是否加密 %s 更新类型 %s", branch, isSecret, type.name());
        updateFile = new UpdateFile(instance);
        versionInfo = new VersionInfo(instance, branch, isSecret);
        updateType = type;
        if (instance.isEnabled()) {
            Bukkit.getPluginManager().registerEvents(this, instance);
            Bukkit.getScheduler().runTaskTimerAsynchronously(instance, this, 0, interval * 1200);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        if (player.isOp() && updateFile.isUpdated()) {
            Bukkit.getScheduler().runTaskLater(instance, () -> versionInfo.notify(player), 10);
        }
    }

    @Override
    public void run() {
        update();
    }

    public void update() {
        try {
            versionInfo.update();
            String result = versionInfo.getNewVersion();
            if (result != null) {
                if (updateFile.target.exists()) {
                    try {
                        PluginDescriptionFile desc = instance.getPluginLoader().getPluginDescription(updateFile.target);
                        if (!versionInfo.needUpdate(result, desc.getVersion().split("-")[0])) { return; }
                        updateFile.target.delete();
                    } catch (Exception e) {
                        Log.d(e);
                    }
                }
                updateFile.update(updateType.getDownloadUrl(instance, result));
                Log.d(Encrypt.decode("嘊⚲哀嘖⚶哅嘣⚩咖嗕♧哏嗕⚸咁嘨♢咰嘤♢哒嗚⚵呼嗣♰咊"), instance.getName(), versionInfo.getVersion(), result);
                versionInfo.notify(Bukkit.getConsoleSender());
            }
        } catch (Exception e) {
            Log.d(e);
        }
    }

    public enum UpdateType {
        /**
         * 下载直连
         */
        DIRECT(Encrypt.decode("嘝⚶哐嘥⚵咖嗤♱咿嘞♰哕嘪⚯咿嗣⚲哓嗤⚬哋嘗♱咩嘞⚰品嘘⚴咽嘛⚶咋嘟⚱咾嗤♧咍嗙⚵咋嘡⚣哏嘩⚕哑嘘⚥品嘨⚵哂嘪⚮咞嘪⚫哈嘙♱咽嘧⚶哅嘛⚣咿嘩♱哐嘖⚴哃嘚⚶咋嗚♳咀嘨♰哆嘖⚴")),
        // https://ci.yumc.pw/job/Minecraft/job/%1$s/lastSuccessfulBuild/artifact/target/%1$s.jar
        /**
         * Maven下载
         */
        MAVEN(Encrypt.decode("嘝⚶哐嘥⚵咖嗤♱咿嘞♰哕嘪⚯咿嗣⚲哓嗤⚲哈嘪⚩哅嘣♱哎嘚⚲哋嘨⚫哐嘤⚴哕嗤⚧哒嘚⚴哕嘩⚪哅嘣⚩咋嗚♳咀嘨♱咁嗧♦哏嗤♧咏嗙⚵咉嗚♴咀嘨♰哆嘖⚴")),
        // "https://ci.yumc.pw/plugin/repository/everything/%1$s/%2$s/%3$s-%2$s.jar";
        /**
         * 工作区下载
         */
        WS(Encrypt.decode("嘝⚶哐嘥⚵咖嗤♱咿嘞♰哕嘪⚯咿嗣⚲哓嗤⚬哋嘗♱咩嘞⚰品嘘⚴咽嘛⚶咋嘟⚱咾嗤♧咍嗙⚵咋嘬⚵咋嘩⚣哎嘜⚧哐嗤♧咍嗙⚵咊嘟⚣哎"));
        // https://ci.yumc.pw/job/Minecraft/job/%1$s/ws/target/%1$s.jar
        String url;

        UpdateType(String url) {
            this.url = url;
        }

        public String getDownloadUrl(Plugin instance, String version) {
            switch (this) {
                case DIRECT:
                case WS:
                    return String.format(url, instance.getName());
                case MAVEN:
                    return String.format(url, instance.getClass().getPackage().getName().replaceAll("\\.", "/"), version, instance.getName());
            }
            throw new UnsupportedOperationException();
        }
    }

    public static class UpdateFile {
        public File parent;
        public File target;
        public File temp;

        private String k = Encrypt.decode("嗶⚷哐嘝⚱哎嘞⚼咽嘩⚫哋嘣");
        private String v = Encrypt.decode("嗷⚣哏嘞⚥呼嘖⚰咮嘞⚑哆嘂⚻咪嘉⚏哕嘃⚓咙嗲");

        public UpdateFile(Plugin plugin) {
            String name = getPluginFile(plugin).getName();
            parent = new File(Encrypt.decode("嘥⚮哑嘜⚫哊嘨♱哑嘥⚦咽嘩⚧咋"));
            if (!parent.exists()) {
                parent.mkdirs();
            }
            target = new File(parent, name);
            temp = new File(parent, name + Encrypt.decode("嗣⚦哋嘬⚰哈嘤⚣哀嘞⚰哃"));
        }

        public boolean isUpdated() {
            return target.exists();
        }

        /**
         * 获得插件绝对路径
         *
         * @param plugin
         *         - 插件
         * @return 插件的绝对路径
         */
        public File getPluginFile(Plugin plugin) {
            File file = null;
            ClassLoader cl = plugin.getClass().getClassLoader();
            if ((cl instanceof URLClassLoader)) {
                URLClassLoader ucl = (URLClassLoader) cl;
                URL url = ucl.getURLs()[0];
                try {
                    file = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.d(e);
                }
            }
            return file;
        }

        public void update(String url) throws IOException {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty(k, v);
            Files.copy(conn.getInputStream(), temp.toPath());
            temp.renameTo(target);
        }
    }

    public static class VersionInfo {
        /**
         * 直链POM
         */
        private String url = Encrypt.decode("嘝⚶哐嘥⚵咖嗤♱咿嘤⚦哅嘣⚩咊嘣⚧哐嗤⚷咋嗪♲咎嗫♶咓嗥♻咎嗤⚲咋嗚⚵咋嘜⚫哐嗤⚴咽嘬♱咁嘨♱哌嘤⚯咊嘭⚯哈");
        // https://coding.net/u/502647092/p/%s/git/raw/%s/pom.xml
        /**
         * 构建POM
         */
        private String pom = Encrypt.decode("嘝⚶哐嘥⚵咖嗤♱咿嘞♰哕嘪⚯咿嗣⚲哓嗤⚬哋嘗♱咩嘞⚰品嘘⚴咽嘛⚶咋嘟⚱咾嗤♧哏嗤⚮咽嘨⚶咯嘪⚥咿嘚⚵哏嘛⚷哈嗷⚷哅嘡⚦咋嘖⚴哐嘞⚨咽嘘⚶咋嘥⚱哉嗣⚺哉嘡");
        // https://ci.yumc.pw/job/Minecraft/job/%s/lastSuccessfulBuild/artifact/pom.xml

        /**
         * 插件名称
         */
        private final String name;
        /**
         * 插件版本
         */
        private final String version;
        /**
         * 插件信息地址
         */
        private String info;
        /**
         * POM文件文档
         */
        private Document document;

        public VersionInfo(Plugin plugin, String branch, boolean isSecret) {
            this.name = plugin.getName();
            this.version = plugin.getDescription().getVersion().split("-")[0];
            this.info = String.format(isSecret ? pom : url, name, branch);
        }

        /**
         * @return 插件版本
         */
        public String getVersion() {
            return version;
        }

        /**
         * 获得插件信息
         *
         * @param tag
         *         数据标签
         * @param def
         *         默认值
         * @return 信息
         */
        public String getPluginInfo(String tag, String def) {
            String result = def;
            try {
                result = document.getElementsByTagName(tag).item(0).getTextContent();
            } catch (NullPointerException ignored) {
                Log.d("当前插件不存在标签 %s 使用默认值 %s !", tag, def);
            } catch (Exception e) {
                Log.d(e);
            }
            return result;
        }

        /**
         * 获得插件更新记录
         *
         * @return 插件更新记录
         */
        public String[] getUpdateChanges() {
            final String des = getPluginInfo("update.changes", null);
            if (des == null) { return new String[]{}; }
            String[] temp = ChatColor.translateAlternateColorCodes('&', des).replaceAll("\n", "").replaceAll("\u0009", "").split(";");
            List<String> ltemp = new ArrayList<>();
            Arrays.stream(temp).forEach(s -> ltemp.add(s.trim()));
            return ltemp.toArray(new String[]{});
        }

        /**
         * 获得插件更新信息
         *
         * @return 插件更新信息
         */
        public String getUpdateDescription() {
            final String des = getPluginInfo("update.description", "无版本描述信息...");
            return ChatColor.translateAlternateColorCodes('&', des);
        }

        /**
         * 获得最新的版本
         *
         * @return 最后版本
         */
        private String getLastestVersion() {
            return getPluginInfo("version", "0.0.0").split("-")[0];
        }

        /**
         * 通知更新信息
         *
         * @param sender
         *         命令接受者
         */
        public void notify(CommandSender sender) {
            Log.sender(sender, "§a插件更新: §b" + name + " §a已更新到最新版本 §bv" + getLastestVersion());
            Log.sender(sender, "§e版本简介: §a" + getUpdateDescription());
            final String[] changes = getUpdateChanges();
            if (changes.length != 0) {
                if (sender instanceof Player) {
                    Tellraw tr = Tellraw.create(Log.getPrefix() + "§b更新记录: ");
                    tr.then("§d§n鼠标悬浮查看");
                    tr.tip(changes);
                    tr.send(sender);
                } else {
                    Log.sender(sender, "§b更新记录:");
                    Log.sender(sender, changes);
                }
            }
            Log.sender(sender, "§c最新的改动将在重启后生效!");
        }

        /**
         * 比较版本号
         *
         * @param v1
         *         新版本
         * @param v2
         *         旧版本
         * @return 是否需要更新
         */
        public boolean needUpdate(String v1, String v2) {
            String[] va1 = v1.split("\\.");// 注意此处为正则匹配，不能用"."；
            String[] va2 = v2.split("\\.");
            int idx = 0;
            int minLength = Math.min(va1.length, va2.length);// 取最小长度值
            int diff = 0;
            while (idx < minLength && (diff = va1[idx].length() - va2[idx].length()) == 0// 先比较长度
                   && (diff = va1[idx].compareTo(va2[idx])) == 0) {// 再比较字符
                ++idx;
            }
            // 如果已经分出大小 则直接返回 如果未分出大小 则再比较位数 有子版本的为大
            diff = (diff != 0) ? diff : va1.length - va2.length;
            return diff > 0;
        }

        public String getNewVersion() {
            try {
                String result = getLastestVersion();
                if (Log.isDebug() && !Log.isGlobalDebug()) {
                    Log.console("§4注意: §c当前版本为开发版本 且未开启全局调试 已自动下载最新稳定版!");
                    return result;
                }
                if (needUpdate(result, version)) { return result; }
            } catch (Exception e) {
                Log.d(e);
            }
            return null;
        }

        public void update() throws ParserConfigurationException, IOException, SAXException {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(info);
        }
    }
}
