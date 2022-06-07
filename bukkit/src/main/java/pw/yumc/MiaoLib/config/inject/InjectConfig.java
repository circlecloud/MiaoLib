package pw.yumc.MiaoLib.config.inject;

import java.io.File;

import pw.yumc.MiaoLib.config.FileConfig;

/**
 * 配置自动载入类
 *
 * @since 2016年7月5日 上午8:53:57
 * @author 喵♂呜
 */
public abstract class InjectConfig extends AbstractInjectConfig {
    public InjectConfig() {
        this(new FileConfig());
    }

    public InjectConfig(File file) {
        this(new FileConfig(file));
    }

    public InjectConfig(FileConfig config) {
        inject(config);
    }

    public InjectConfig(String name) {
        this(new FileConfig(name));
    }

    /**
     * 获得配置文件
     *
     * @return 配置文件
     */
    public FileConfig getConfig() {
        return (FileConfig) config;
    }

    /**
     * 重载配置文件
     */
    public void reload() {
        getConfig().reload();
        inject(config);
    }

    /**
     * 自动化保存
     */
    public void save() {
        save(config);
        getConfig().save();
    }
}
