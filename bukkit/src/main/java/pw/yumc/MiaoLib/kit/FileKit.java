package pw.yumc.MiaoLib.kit;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * @author 蒋天蓓
 *
 */
public class FileKit {
    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param sender
     *            执行者
     * @param dir
     *            将要删除的文件目录
     * @return 是否删除成功.
     */
    public static boolean deleteDir(final CommandSender sender, final File dir) {
        if (dir.isDirectory()) {
            final String[] children = dir.list();
            if (children == null) { return false; }
            // 递归删除目录中的子目录下
            for (final String element : children) {
                final File file = new File(dir, element);
                if (!deleteDir(file)) {
                    sender.sendMessage("§c删除: §e" + file.getAbsolutePath() + " §c时 发生错误!");
                } else {
                    sender.sendMessage("§c删除: §e" + file.getAbsolutePath() + " §a成功!");
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static boolean deleteDir(final File dir) {
        return deleteDir(Bukkit.getConsoleSender(), dir);
    }
}
