package pw.yumc.MiaoLib.config.inject;

import org.bukkit.configuration.ConfigurationSection;

/**
 * 配置自动载入类
 *
 * @since 2016年7月5日 上午8:53:57
 * @author 喵♂呜
 */
public abstract class InjectConfigurationSection extends AbstractInjectConfig {

    public InjectConfigurationSection(ConfigurationSection config) {
        inject(config);
    }

    /**
     * 重载配置文件
     *
     * @param config
     *            配置区
     */
    public void reload(ConfigurationSection config) {
        inject(config);
    }
}
