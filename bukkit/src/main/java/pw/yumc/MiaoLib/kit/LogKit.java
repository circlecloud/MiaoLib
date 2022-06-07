package pw.yumc.MiaoLib.kit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.bukkit.P;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogKit implements Runnable {
    private final static Plugin plugin = P.instance;
    private final static File dataFolder = plugin.getDataFolder();

    private PrintStream ps;
    private final List<String> logs = new ArrayList<>(5);

    public LogKit(final File log) {
        try {
            if (!log.exists()) {
                log.createNewFile();
            }
            final FileOutputStream fos = new FileOutputStream(log, true);
            this.ps = new PrintStream(fos, true, "UTF-8");
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0, 100);
        } catch (final FileNotFoundException e) {
            Log.d(e);
            Log.w("日志文件未找到 %s !", e.getMessage());
        } catch (final IOException e) {
            Log.d(e);
            Log.w("无法创建日志文件 %s !", e.getMessage());
        }

    }

    public LogKit(final String name) {
        this(new File(dataFolder, name));
    }

    /**
     * 关闭日志并保存
     */
    public void close() {
        this.ps.close();
    }

    /**
     * 添加日志
     *
     * @param s
     *            日志
     */
    public void log(final String s) {
        synchronized (logs) {
            logs.add(new Date().toLocaleString() + s);
        }
    }

    /**
     * 添加日志
     *
     * @param s
     *            日志
     */
    public void console(final String s) {
        send(Bukkit.getConsoleSender(), s);
    }

    /**
     * 添加日志
     * 
     * @param sender
     *            接受者
     * @param s
     *            日志
     */
    public void send(final CommandSender sender, final String s) {
        Log.sender(sender, s);
        log(ChatColor.stripColor(s));
    }

    @Override
    public void run() {
        synchronized (logs) {
            for (final String s : logs) {
                ps.println(s);
            }
            ps.flush();
            logs.clear();
        }
    }
}