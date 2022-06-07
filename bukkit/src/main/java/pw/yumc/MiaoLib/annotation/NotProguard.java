package pw.yumc.MiaoLib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 防混淆注解
 * Created by 蒋天蓓 on 2017/2/4 0004.
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.CONSTRUCTOR })
public @interface NotProguard {
}
