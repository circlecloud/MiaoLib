package pw.yumc.MiaoLib.commands.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 命令帮助注解
 *
 * @since 2016年7月23日 上午9:00:07
 * @author 喵♂呜
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Help {
	/**
	 * @return 当前命令可能需要的参数
	 */
	String possibleArguments() default "";

	/**
	 * @return 命令描述
	 */
	String value();
}
