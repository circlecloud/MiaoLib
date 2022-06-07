package pw.yumc.MiaoLib.utils;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created with IntelliJ IDEA
 *
 * @author 喵♂呜
 * Created on 2017/9/1 10:49.
 */
public class Assert<T> {
    private T t;

    public static <T> Assert<T> of(T value) {
        return new Assert<>(value);
    }

    public static void runIsTrue(Boolean bool, Runnable runnable) {
        if (bool)
            runnable.run();
    }

    public static void runOrElse(Boolean bool, Runnable t, Runnable f) {
        if (bool)
            t.run();
        else
            f.run();
    }

    public Assert(T t) {
        this.t = t;
    }

    public void runNonNull(Consumer<? super T> consumer) {
        runIsTrue(Objects.nonNull(t), () -> consumer.accept(t));
    }

    public void runIsNull(Consumer<? super T> consumer) {
        runIsTrue(Objects.isNull(t), () -> consumer.accept(t));
    }

    public void runIsAssignable(Class clazz, Consumer<? super T> consumer) {
        runIsTrue(t != null && clazz.isAssignableFrom(t.getClass()), () -> consumer.accept(t));
    }
}