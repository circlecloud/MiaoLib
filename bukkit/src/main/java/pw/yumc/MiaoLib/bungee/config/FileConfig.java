package pw.yumc.MiaoLib.bungee.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import pw.yumc.MiaoLib.bungee.Log;

/**
 * 
 * Created by 蒋天蓓 on 2016/11/7 0007.
 */
public class FileConfig {
    private File file;
    private Configuration config;

    public FileConfig(Plugin plugin) {
        this(plugin, "config.yml");
    }

    public <T> T get(String path, T def) {
        return config.get(path, def);
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    public Object get(String path) {
        return config.get(path);
    }

    public Object getDefault(String path) {
        return config.getDefault(path);
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }

    public Configuration getSection(String path) {
        return config.getSection(path);
    }

    public Collection<String> getKeys() {
        return config.getKeys();
    }

    public byte getByte(String path) {
        return config.getByte(path);
    }

    public byte getByte(String path, byte def) {
        return config.getByte(path, def);
    }

    public List<Byte> getByteList(String path) {
        return config.getByteList(path);
    }

    public short getShort(String path) {
        return config.getShort(path);
    }

    public short getShort(String path, short def) {
        return config.getShort(path, def);
    }

    public List<Short> getShortList(String path) {
        return config.getShortList(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public List<Integer> getIntList(String path) {
        return config.getIntList(path);
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    public long getLong(String path, long def) {
        return config.getLong(path, def);
    }

    public List<Long> getLongList(String path) {
        return config.getLongList(path);
    }

    public float getFloat(String path) {
        return config.getFloat(path);
    }

    public float getFloat(String path, float def) {
        return config.getFloat(path, def);
    }

    public List<Float> getFloatList(String path) {
        return config.getFloatList(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public List<Double> getDoubleList(String path) {
        return config.getDoubleList(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public List<Boolean> getBooleanList(String path) {
        return config.getBooleanList(path);
    }

    public char getChar(String path) {
        return config.getChar(path);
    }

    public char getChar(String path, char def) {
        return config.getChar(path, def);
    }

    public List<Character> getCharList(String path) {
        return config.getCharList(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public List<?> getList(String path) {
        return config.getList(path);
    }

    public List<?> getList(String path, List<?> def) {
        return config.getList(path, def);
    }

    public FileConfig(Plugin plugin, String name) {
        this.file = new File(plugin.getDataFolder(), name);
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                Files.copy(plugin.getResourceAsStream(name), file.toPath());
            }
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            Log.w("配置文件读取失败!");
            e.printStackTrace();
        }
    }

    public Configuration getConfig() {
        return config;
    }

    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch (IOException e) {
            Log.w("配置文件保存失败!");
            e.printStackTrace();
        }
    }

    public void reload() {
        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            Log.w("配置文件读取失败!");
            e.printStackTrace();
        }
    }

}
