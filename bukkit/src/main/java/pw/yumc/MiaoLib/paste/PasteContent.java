package pw.yumc.MiaoLib.paste;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据组装
 *
 * @since 2016年9月19日 下午3:40:49
 * @author 喵♂呜
 */
public class PasteContent {
    private static String errN = "异常名称: %s";
    private static String errM = "异常说明: %s";
    private static String errInfo = "简易错误信息如下:";
    private static String errStackTrace = "    位于 %s.%s(%s:%s)";
    private List<String> TEXT = new ArrayList<>();

    /**
     * 添加文件
     *
     * @param file
     *            文件
     * @throws IOException
     *             IO异常
     */
    public void addFile(File file) throws IOException {
        if (file == null) { throw new IllegalArgumentException("文件不得为Null!"); }
        addLines(Files.readAllLines(file.toPath(), Charset.forName("UTF-8")));
    }

    /**
     * 添加行
     *
     * @param str
     *            行
     */
    public void addLine(String str) {
        this.TEXT.add(str);
    }

    /**
     * 添加行
     *
     * @param str
     *            行
     */
    public void addLines(List<String> str) {
        this.TEXT.addAll(str);
    }

    /**
     * 添加异常
     *
     * @param e
     *            异常
     */
    public void addThrowable(Throwable e) {
        Throwable temp = e;
        while (temp.getCause() != null) {
            temp = temp.getCause();
        }
        TEXT.add(String.format(errN, e.getClass().getName()));
        TEXT.add(String.format(errM, e.getMessage()));
        TEXT.add(errInfo);
        for (StackTraceElement ste : e.getStackTrace()) {
            TEXT.add(String.format(errStackTrace, ste.getClassName(), ste.getMethodName(), ste.getFileName(), ste.getLineNumber() < 0 ? "未知" : ste.getLineNumber()));
        }
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        for (String str : TEXT) {
            text.append(str).append('\n');
        }
        return text.toString();
    }
}