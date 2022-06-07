package pw.yumc.MiaoLib.config.ext;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.config.FileConfig;

public class YumConfig {
    protected static String REMOTEFILECENTER = "http://data.yumc.pw/config/";
    protected static String DataFolder = "plugins" + File.separatorChar + "YumCore";
    protected static String CacheFolder = DataFolder + File.separatorChar + "cache";

    private static String fromYumc = "配置 %s 来自 YUMC 数据中心...";
    private static String createError = "从 YUMC 数据中心下载配置 %s 失败 使用缓存的配置...";

    private YumConfig() {
    }

    /**
     * 获得本地配置文件
     *
     * @param filename
     *            本地文件名称
     * @return {@link FileConfig}
     */
    public static FileConfig getLocal(String filename) {
        File file = new File(DataFolder, filename);
        return new FileConfig(file);
    }

    /**
     * 获得远程配置文件
     *
     * @param configname
     *            配置文件地址
     * @return {@link FileConfig}
     */
    public static FileConfig getRemote(String configname) {
        FileConfig config;
        try {
            config = new FileConfig(new URL(REMOTEFILECENTER + configname).openStream());
            config.save(new File(CacheFolder, configname));
            Log.i(fromYumc, configname);
        } catch (IOException e) {
            Log.d(e);
            config = new FileConfig(new File(CacheFolder, configname));
            Log.i(createError, configname);
        }
        return config;
    }
}
