package pw.yumc.MiaoLib.misc;

import java.util.Map;
import java.util.WeakHashMap;

import pw.yumc.MiaoLib.bukkit.Log;

/**
 * 性能检测类
 * Created by 蒋天蓓 on 2017/2/9 0009.
 */
public class Timings {
    private static Map<String, Timings> timingsMap = new WeakHashMap<>();
    private String name;
    private long start;

    public Timings(String name) {
        this.name = name;
        this.start = System.nanoTime();
    }

    public static Timings get(String string) {
        if (!timingsMap.containsKey(string)) {
            timingsMap.put(string, new Timings(string));
        }
        return timingsMap.get(string);
    }

    public static Timings clear(String string) {
        return timingsMap.remove(string);
    }

    public void start() {
        this.start = System.nanoTime();
    }

    public double stop() {
        return (System.nanoTime() - start) / 1024.00 / 1024.00;
    }

    public void print() {
        Log.d("操作 %s 耗时 %sms", name, stop());
    }
}
