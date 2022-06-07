package pw.yumc.MiaoLib.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import pw.yumc.MiaoLib.bukkit.Log;

/**
 * 一个继承于 {@link YamlConfiguration} 的配置文件类
 * 强制UTF-8编码处理所有的文件信息
 *
 * @author 喵♂呜
 * @version 1.0
 * @since 2015年11月7日 下午2:36:07
 */
public class FileConfig extends AbstractConfig {
    protected static String VERSION = "Version";

    private static char ALT_COLOR_CHAR = '&';
    private static String DEFAULT = "config.yml";
    private static String DATA_FORMANT = "yyyyMMddHHmmss";
    private static String CONFIG_BACKUP = "配置: %s 已备份为 %s !";
    private static String CONFIG_UPDATED = "配置: %s 升级成功 版本 %s !";
    private static String CONFIG_OVERRIDE = "配置: %s 将覆盖原有字段数据...";
    private static String CONFIG_NOT_FOUND = "配置: 文件 %s 不存在!";
    private static String CONFIG_READ_ERROR = "配置: %s 读取错误...";
    private static String CONFIG_SAVE_ERROR = "配置: %s 保存错误...";
    private static String CONFIG_UPDATE_WARN = "配置: %s 版本 %s 过低 正在升级到 %s ...";
    private static String CONFIG_CREATE_ERROR = "配置: %s 创建失败...";
    private static String CONFIG_FORMAT_ERROR = "配置: %s 格式错误...";
    private static String CONFIG_BACKUP_ERROR = "配置: %s 备份失败 异常: %s !";
    private static String CONFIG_UPDATE_VALUE = "配置: 更新字段 %s 的值为 %s ...";
    private static String CONFIG_BACKUP_AND_RESET = "配置: %s 格式错误 已备份为 %s 并恢复默认配置!";
    private static String CONFIG_NOT_FOUND_IN_JAR = "配置: 从插件内部未找到预置的 %s 文件!";
    private static String CONFIG_READ_COMMENT_ERROR = "配置: 读取文件注释信息失败!";
    private static String STREAM_NOT_BE_NULL = "数据流不能为 NULL";

    protected File file;

    private CommentConfig commentConfig;

    /**
     * 实例化默认配置文件
     */
    public FileConfig() {
        this(DEFAULT);
    }

    /**
     * 从文件载入配置
     *
     * @param file
     *            配置文件名称
     */
    public FileConfig(File file) {
        Validate.notNull(file, FILE_NOT_BE_NULL);
        init(file);
    }

    /**
     * 从文件载入配置
     *
     * @param parent
     *            文件夹
     * @param filename
     *            配置文件名称
     */
    public FileConfig(File parent, String filename) {
        init(new File(parent, filename), true);
    }

    /**
     * 从数据流载入配置文件
     *
     * @param stream
     *            数据流
     */
    public FileConfig(InputStream stream) {
        init(stream);
    }

    /**
     * 从文件载入配置
     *
     * @param filename
     *            配置文件名称
     */
    public FileConfig(String filename) {
        init(new File(plugin.getDataFolder(), filename), true);
    }

    /**
     * 从文件载入配置
     *
     * @param parent
     *            文件夹
     * @param filename
     *            配置文件名称
     */
    public FileConfig(String parent, String filename) {
        init(new File(parent, filename), true);
    }

    /**
     * 添加到List末尾
     *
     * @param <E>
     *            List内容类型
     * @param path
     *            路径
     * @param obj
     *            对象
     * @return {@link FileConfig}
     */
    public <E> FileConfig addToList(String path, E obj) {
        List<E> l = (List<E>) this.getList(path);
        if (null == l) {
            l = new ArrayList<>();
        }
        l.add(obj);
        return this;
    }

    /**
     * 添加到StringList末尾
     *
     * @param path
     *            路径
     * @param obj
     *            字符串
     * @return {@link FileConfig}
     */
    public FileConfig addToStringList(String path, String obj) {
        addToStringList(path, obj, true);
        return this;
    }

    /**
     * 添加到StringList末尾
     *
     * @param path
     *            路径
     * @param obj
     *            字符串
     * @param allowrepeat
     *            是否允许重复
     * @return {@link FileConfig}
     */
    public FileConfig addToStringList(String path, String obj, boolean allowrepeat) {
        List<String> l = this.getStringList(path);
        if (null == l) {
            l = new ArrayList<>();
        }
        if (allowrepeat || !l.contains(obj)) {
            l.add(obj);
        }
        this.set(path, l);
        return this;
    }

    /**
     * 获得已颜色转码的文本
     *
     * @param cfgmsg
     *            待转码的List
     * @return 颜色转码后的文本
     */
    public List<String> getColorList(List<String> cfgmsg) {
        List<String> message = new ArrayList<>();
        if (cfgmsg == null) { return Collections.emptyList(); }
        for (String msg : cfgmsg) {
            message.add(ChatColor.translateAlternateColorCodes('&', msg));
        }
        return message;
    }

    /**
     * 获得配置文件名称
     *
     * @return 配置文件名称
     */
    public String getConfigName() {
        return file.getName();
    }

    /**
     * 获得Location
     *
     * @param key
     *            键
     * @return {@link Location}
     */
    public Location getLocation(String key) {
        return getLocation(key, null);
    }

    /**
     * 获得Location
     *
     * @param path
     *            键
     * @param def
     *            默认地点
     * @return {@link Location}
     */
    public Location getLocation(String path, Location def) {
        Object val = get(path, def);
        return val instanceof Location ? (Location) val : def;
    }

    /**
     * 获得已颜色转码的文本
     *
     * @param path
     *            配置路径
     * @return 颜色转码后的文本
     */
    public String getMessage(String path) {
        return getMessage(path, null);
    }

    /**
     * 获得已颜色转码的文本
     *
     * @param path
     *            配置路径
     * @param def
     *            默认文本
     * @return 颜色转码后的文本
     */
    public String getMessage(String path, String def) {
        String message = this.getString(path, def);
        if (message != null) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }
        return message;
    }

    /**
     * 获得已颜色转码的文本
     *
     * @param path
     *            配置路径
     * @return 颜色转码后的文本
     */
    public List<String> getMessageList(String path) {
        List<String> cfgmsg = this.getStringList(path);
        if (cfgmsg == null) { return Collections.emptyList(); }
        for (int i = 0; i < cfgmsg.size(); i++) {
            cfgmsg.set(i, ChatColor.translateAlternateColorCodes(ALT_COLOR_CHAR, cfgmsg.get(i)));
        }
        return cfgmsg;
    }

    /**
     * 获得字符串数组
     *
     * @param path
     *            配置路径
     * @return 字符串数组
     */
    public String[] getStringArray(String path) {
        return this.getStringList(path).toArray(new String[0]);
    }

    @Override
    public void loadFromString(String contents) throws InvalidConfigurationException {
        try {
            commentConfig = new CommentConfig();
            commentConfig.loadFromString(contents);
        } catch (Exception e) {
            Log.d(CONFIG_READ_COMMENT_ERROR);
            commentConfig = null;
        }
        super.loadFromString(contents);
    }

    /**
     * 比较版本号
     *
     * @param newver
     *            新版本
     * @param oldver
     *            旧版本
     * @return 是否需要更新
     */
    public boolean needUpdate(String newver, String oldver) {
        if (newver == null) { return false; }
        if (oldver == null) { return true; }
        String[] va1 = newver.split("\\.");// 注意此处为正则匹配，不能用"."；
        String[] va2 = oldver.split("\\.");
        int idx = 0;
        int minLength = Math.min(va1.length, va2.length);// 取最小长度值
        int diff = 0;
        while (idx < minLength && (diff = va1[idx].length() - va2[idx].length()) == 0// 先比较长度
                && (diff = va1[idx].compareTo(va2[idx])) == 0) {// 再比较字符
            ++idx;
        }
        // 如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : va1.length - va2.length;
        return diff > 0;
    }

    /**
     * 重新载入配置文件
     *
     * @return 是否载入成功
     */
    public boolean reload() {
        return init(file) != null;
    }

    /**
     * 从List移除对象
     *
     * @param <E>
     *            List内容对象类型
     * @param path
     *            路径
     * @param obj
     *            对象
     * @return {@link FileConfig}
     */
    public <E> FileConfig removeFromList(String path, E obj) {
        List<E> l = (List<E>) this.getList(path);
        if (null != l) {
            l.remove(obj);
        }
        return this;
    }

    /**
     * 从StringList移除对象
     *
     * @param path
     *            路径
     * @param obj
     *            对象
     * @return {@link FileConfig}
     */
    public FileConfig removeFromStringList(String path, String obj) {
        List<String> l = this.getStringList(path);
        if (null != l) {
            l.remove(obj);
        }
        this.set(path, obj);
        return this;
    }

    /**
     * 快速保存配置文件
     *
     * @return 是否成功
     */
    public boolean save() {
        try {
            this.save(file);
            return true;
        } catch (IOException e) {
            Log.w(CONFIG_SAVE_ERROR, file.getName());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void save(File file) throws IOException {
        Validate.notNull(file, FILE_NOT_BE_NULL);
        if (commentConfig != null) {
            data = commentConfig.saveToString();
        } else {
            data = saveToString();
        }
        super.save(file);
    }

    @Override
    public void set(String path, Object value) {
        if (commentConfig != null) {
            commentConfig.set(path, value);
        }
        super.set(path, value);
    }

    /**
     * 从Jar保存配置文件
     */
    private void saveFromJar() {
        if (plugin != null && file != null) {
            try {
                String filename = file.getName();
                InputStream filestream = plugin.getResource(file.getName());
                String errFileName = this.getErrName(filename);
                file.renameTo(new File(file.getParent(), errFileName));
                if (filestream == null) {
                    file.createNewFile();
                } else {
                    plugin.saveResource(filename, true);
                }
                Log.w(CONFIG_BACKUP_AND_RESET, filename, errFileName);
            } catch (IOException | IllegalArgumentException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            Log.w(CONFIG_NOT_FOUND_IN_JAR, file != null ? file.getName() : "");
        }
    }

    /**
     * 备份配置文件
     *
     * @param oldcfg
     *            配置文件
     */
    protected void backupConfig(FileConfig oldcfg) {
        String filename = oldcfg.getConfigName();
        try {
            String newCfgName = this.getBakName(filename);
            File newcfg = new File(file.getParent(), newCfgName);
            oldcfg.save(newcfg);
            Log.w(CONFIG_BACKUP, filename, newCfgName);
        } catch (IOException e) {
            Log.w(CONFIG_BACKUP_ERROR, filename, e.getMessage());
            Log.d(oldcfg.getConfigName(), e);
        }
    }

    /**
     * 检查配置文件
     *
     * @param file
     *            配置文件
     */
    protected void check(File file) {
        String filename = file.getName();
        InputStream stream = plugin.getResource(filename);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                if (stream == null) {
                    file.createNewFile();
                } else {
                    plugin.saveResource(filename, true);
                }
            } else {
                if (stream == null) { return; }
                FileConfig newcfg = new FileConfig(stream);
                FileConfig oldcfg = new FileConfig(file);
                if (needUpdate(newcfg, oldcfg)) {
                    backupConfig(oldcfg);
                    updateConfig(newcfg, oldcfg).save(file);
                }
            }
        } catch (IOException e) {
            Log.w(CONFIG_CREATE_ERROR, filename);
        }
    }

    protected String getBakName(String cfgname) {
        return cfgname + "." + getStringDate(DATA_FORMANT) + ".bak";
    }

    protected String getErrName(String cfgname) {
        return cfgname + "." + getStringDate(DATA_FORMANT) + ".err";
    }

    /**
     * 获取现在时间
     * 
     * @param format
     *            字符串格式
     * @return yyyy-MM-dd HH:mm:ss
     */
    protected String getStringDate(String format) {
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(format).format(new Date());
    }

    /**
     * 初始化FileConfig
     *
     * @param file
     *            配置文件
     * @return FileConfig
     */
    protected FileConfig init(File file) {
        init(file, false);
        return this;
    }

    /**
     * 初始化FileConfig
     *
     * @param file
     *            配置文件
     * @param check
     *            是否检查文件
     * @return FileConfig
     */
    protected FileConfig init(File file, boolean check) {
        Validate.notNull(file, FILE_NOT_BE_NULL);
        this.file = file;
        if (check) {
            check(file);
        }
        try {
            init(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Log.d(CONFIG_NOT_FOUND, file.toPath());
        }
        return this;
    }

    /**
     * 初始化FileConfig
     *
     * @param stream
     *            输入流
     * @return FileConfig
     */
    protected FileConfig init(InputStream stream) {
        Validate.notNull(stream, STREAM_NOT_BE_NULL);
        try {
            this.load(new InputStreamReader(stream, UTF_8));
        } catch (InvalidConfigurationException | IllegalArgumentException ex) {
            if (file == null) { throw new IllegalArgumentException(ex); }
            Log.w(CONFIG_FORMAT_ERROR, file.getName());
            Log.w(ex.getMessage());
            saveFromJar();
        } catch (IOException ex) {
            if (file == null) { throw new IllegalStateException(ex); }
            Log.w(CONFIG_READ_ERROR, file.getName());
        }
        return this;
    }

    /**
     * 检查配置文件版本
     *
     * @param newcfg
     *            新配置文件
     * @param oldcfg
     *            旧配置文件
     * @return 是否需要升级
     */
    protected boolean needUpdate(FileConfig newcfg, FileConfig oldcfg) {
        return needUpdate(newcfg.getString(VERSION), oldcfg.getString(VERSION));
    }

    /**
     * 更新配置文件
     *
     * @param newCfg
     *            新配置文件
     * @param oldCfg
     *            旧配置文件
     * @return 更新以后的配置文件
     */
    protected FileConfig updateConfig(FileConfig newCfg, FileConfig oldCfg) {
        return updateConfig(newCfg, oldCfg, false);
    }

    /**
     * 更新配置文件
     *
     * @param newCfg
     *            新的配置文件
     * @param oldCfg
     *            老的配置文件
     * @param force
     *            是否强制更新
     * @return 更新以后的配置文件
     */
    protected FileConfig updateConfig(FileConfig newCfg, FileConfig oldCfg, boolean force) {
        String filename = oldCfg.getConfigName();
        String newver = newCfg.getString(VERSION);
        String oldver = oldCfg.getString(VERSION);
        Set<String> oldConfigKeys = oldCfg.getKeys(true);
        Log.w(CONFIG_UPDATE_WARN, filename, oldver, newver);
        // 保留版本字段 不更新
        oldConfigKeys.remove(VERSION);
        // 强制更新 去除新版本存在的字段
        if (force) {
            Log.w(CONFIG_OVERRIDE, filename);
            oldConfigKeys.removeAll(newCfg.getKeys(true));
        }
        // 复制旧的数据
        for (String string : oldConfigKeys) {
            Object var = oldCfg.get(string);
            // 需要进行节点检查 还有类型检查 不同类型情况下 使用新配置
            if (var != null && !(var instanceof MemorySection)) {
                Object newVer = newCfg.get(string);
                if (newVer != null && !newVer.getClass().equals(var.getClass())) {
                    Log.w("警告! 旧数据类型与新配置类型不匹配!");
                }
                Log.d(CONFIG_UPDATE_VALUE, string, var);
                newCfg.set(string, var);
            }
        }
        Log.i(CONFIG_UPDATED, filename, newver);
        return newCfg;
    }
}
