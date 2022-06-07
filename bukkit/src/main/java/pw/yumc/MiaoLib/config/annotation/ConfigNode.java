package pw.yumc.MiaoLib.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置节点路径
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigNode {
    /**
     * 定义配置文件路径节点路径.
     * 通常用于配置节点 ('.')
     *
     * @return 节点路径
     */
    String value();
}
