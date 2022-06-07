package pw.yumc.MiaoLib.kit;

/**
 * 异常处理工具
 *
 * @since 2016年3月30日 上午10:59:09
 * @author 喵♂呜
 */
public class ExKit {
    /**
     * 任意抛出异常
     *
     * @param exception
     *            异常
     */
    public static void throwException(final Throwable exception) {
        ExKit.throwExceptionT(exception);
    }

    /**
     * 抛出异常
     * 
     * @param <T>
     *            异常
     * @param exception
     *            异常
     * @throws T
     *             异常
     */
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwExceptionT(final Throwable exception) throws T {
        if (exception != null) { throw (T) exception; }
    }
}
