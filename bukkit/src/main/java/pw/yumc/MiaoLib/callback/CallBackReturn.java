package pw.yumc.MiaoLib.callback;

/**
 * 带参数回调类
 * Created by 蒋天蓓 on 2016/6/16 0016.
 */
public abstract class CallBackReturn {
    /**
     * 无参数回调
     *
     * @param <OUT>
     *            返回值
     */
    public static abstract class None<OUT> {
        public abstract OUT run();
    }

    /**
     * 一个参数回调
     *
     * @param <IN>
     *            参数
     * @param <OUT>
     *            返回值
     */
    public static abstract class One<IN, OUT> {
        public abstract OUT run(IN param);
    }
}
