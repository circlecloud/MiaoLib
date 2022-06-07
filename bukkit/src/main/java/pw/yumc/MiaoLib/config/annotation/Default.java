package pw.yumc.MiaoLib.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置默认值
 *
 * @author 喵♂呜
 * @since 2016年8月24日 下午10:41:55
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Default {
    /**
     * @return 默认值
     */
    String value();
}
