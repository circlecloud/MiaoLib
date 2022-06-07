package pw.yumc.MiaoLib.config.inject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import pw.yumc.MiaoLib.config.annotation.ConfigNode;
import pw.yumc.MiaoLib.config.annotation.Default;
import pw.yumc.MiaoLib.config.annotation.ReadOnly;
import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.config.annotation.Nullable;
import pw.yumc.MiaoLib.config.exception.ConfigParseException;

/**
 * 抽象注入配置
 *
 * @author 喵♂呜
 * @since 2016年7月5日 上午10:11:22
 */
public abstract class AbstractInjectConfig {
    private static String INJECT_TYPE_ERROR = "配置节点 %s 数据类型不匹配 应该为: %s 但实际为: %s!";
    private static String INJECT_ERROR = "配置节点 %s 自动注入失败 可能造成插件运行错误 %s: %s!";
    private static String PATH_NOT_FOUND = "配置节点 %s 丢失 将使用默认值!";
    protected ConfigurationSection config;

    /**
     * 添加默认值
     *
     * @param field
     *            字段
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private void applyDefault(Field field) throws IllegalArgumentException, IllegalAccessException {
        Object value = null;
        switch (field.getType().getName()) {
        case "java.util.List":
            value = new ArrayList<>();
            break;
        case "java.util.Map":
            value = new HashMap<>();
            break;
        }
        field.set(this, value);
    }

    /**
     * 处理值
     *
     * @param path
     *            路径
     * @param field
     *            字段
     * @param value
     *            值
     * @throws java.text.ParseException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void hanldeValue(Field field, String path, Object value) throws IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException, ConfigParseException {
        Class type = field.getType();
        if (!type.equals(value.getClass())) {
            value = InjectParse.parse(type, value, config, path);
        }
        if (type.equals(String.class)) {
            value = ChatColor.translateAlternateColorCodes('&', String.valueOf(value));
        }
        if (value != null && !type.isAssignableFrom(value.getClass())) {
            Log.w("字段 %s 默认类型为 %s 但解析后为 %s 可能存在转换错误!", field.getName(), type.getName(), value.getClass().getName());
        }
        field.set(this, value);
        Log.fd("设置字段 %s 为 %s ", field.getName(), value);
    }

    /**
     * 配置注入后的初始化操作(非注入对象初始化也要在此处)
     */
    protected void init() {
    }

    /**
     * 注入配置数据
     *
     * @param config
     *            配置区
     */
    public void inject(ConfigurationSection config) {
        inject(config, false);
        init();
    }

    /**
     * 注入配置数据
     *
     * @param config
     *            配置区
     * @param save
     *            是否为保存
     */
    public void inject(ConfigurationSection config, boolean save) {
        if (config == null) {
            Log.w("尝试%s ConfigurationSection 为 Null 的数据!", save ? "保存" : "读取");
            return;
        }
        this.config = config;
        for (Field field : getClass().getDeclaredFields()) {
            // 忽略瞬态字段 忽略基础字段 忽略内联字段
            if (Modifier.isTransient(field.getModifiers()) || field.getType().isPrimitive() || field.getType().isSynthetic()) {
                continue;
            }
            ConfigNode node = field.getAnnotation(ConfigNode.class);
            String path = field.getName();
            if (node != null && !node.value().isEmpty()) {
                path = node.value();
            }
            field.setAccessible(true);
            if (save) {
                setConfig(path, field);
            } else {
                setField(path, field);
            }
        }
    }

    /**
     * 自动化保存
     *
     * @param config
     *            配置文件区
     * @return
     *         配置文件
     */
    public ConfigurationSection save(ConfigurationSection config) {
        inject(config, true);
        return config;
    }

    /**
     * 通用保存流程
     *
     * @param path
     *            配置路径
     * @param field
     *            字段
     */
    protected void setConfig(String path, Field field) {
        try {
            if (field.getAnnotation(ReadOnly.class) == null) {
                config.set(path, field.get(this));
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Log.w(INJECT_ERROR, e.getClass().getName(), e.getMessage());
            Log.d(e);
        }
    }

    /**
     * 通用读取流程
     *
     * @param path
     *            配置路径
     * @param field
     *            字段
     */
    protected void setField(String path, Field field) {
        Object value = null;
        try {
            if (!config.contains(path)) {
                Default def = field.getAnnotation(Default.class);
                if (def != null) {
                    value = def.value();
                } else if (field.getAnnotation(Nullable.class) == null) {
                    Log.w(PATH_NOT_FOUND, path);
                    applyDefault(field);
                }
            } else {
                value = config.get(path);
            }
            if (value == null) { return; }
            hanldeValue(field, path, value);
        } catch (IllegalArgumentException ex) {
            Log.w(INJECT_TYPE_ERROR, path, field.getType().getName(), value != null ? value.getClass().getName() : "空指针");
            Log.d(ex);
        } catch (ConfigParseException e) {
            Log.w(e.getMessage());
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException | IllegalAccessException ex) {
            Log.w(INJECT_ERROR, path, ex.getClass().getName(), ex.getMessage());
            Log.d(ex);
        }
    }

    public ConfigurationSection getConfig() {
        return config;
    }
}
