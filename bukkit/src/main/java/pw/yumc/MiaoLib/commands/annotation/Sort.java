package pw.yumc.MiaoLib.commands.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 命令排序注解
 *
 * @since 2016年7月23日 上午9:04:56
 * @author 喵♂呜
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sort {
    /**
     * @return 命令排序
     */
    int value();
}
