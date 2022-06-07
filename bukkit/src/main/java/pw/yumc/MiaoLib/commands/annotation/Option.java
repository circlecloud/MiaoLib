package pw.yumc.MiaoLib.commands.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 扩展选项注解
 * <code>
 *     例如 "def:默认值 max:最大值 min 最小值"
 *</code>
 * 
 * @since 2016年7月23日 上午9:00:07
 * @author 喵♂呜
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {
    String value();
}
