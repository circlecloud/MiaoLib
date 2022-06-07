package pw.yumc.MiaoLib.callback;

/**
 * 回调类
 * Created by 蒋天蓓 on 2016/6/16 0016.
 */
public abstract class CallBack {
    /**
     * 多个参数回调
     */
    public static abstract class Any {
        public abstract void run(Object... param);
    }

    /**
     * 无参数回调
     */
    public static abstract class None {
        public abstract void run();
    }

    /**
     * 一个参数回调
     *
     * @param <T>
     *            参数
     */
    public static abstract class One<T> {
        public abstract void run(T param);
    }

    /**
     * 三个参数回调
     *
     * @param <B>
     *            参数
     * @param <A>
     *            参数
     * @param <T>
     *            参数
     */
    public static abstract class Three<B, A, T> {
        public abstract void run(B param1, A param2, T param3);
    }

    /**
     * 二个参数回调
     *
     * @param <T>
     *            参数
     * @param <M>
     *            参数
     */
    public static abstract class Two<T, M> {
        public abstract void run(T param1, M param2);
    }
}
