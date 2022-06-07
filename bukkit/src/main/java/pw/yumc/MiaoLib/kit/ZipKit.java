package pw.yumc.MiaoLib.kit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * ZIP操作类
 *
 * @since 2016年7月19日 上午10:24:06
 * @author 喵♂呜
 */
public class ZipKit {
    /**
     * 获取文件真实名称
     *
     * @param name
     *            名称
     * @return 文件名称
     */
    public static String getRealName(String name) {
        return new File(name).getName();
    }

    /**
     * 解压ZIP文件
     *
     * @param zipFile
     *            zip文件
     * @param destPath
     *            解压目录
     * @throws ZipException
     *             ZIP操作异常
     * @throws IOException
     *             IO异常
     */
    public static void unzip(File zipFile, File destPath) throws IOException {
        unzip(zipFile, destPath, null);
    }

    /**
     * 解压ZIP文件
     *
     * @param zipFile
     *            zip文件
     * @param destPath
     *            解压目录
     * @param ext
     *            解压后缀
     * @throws ZipException
     *             ZIP操作异常
     * @throws IOException
     *             IO异常
     */
    public static void unzip(File zipFile, File destPath, String ext) throws IOException {
        ZipFile zipObj = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> e = zipObj.entries();
        while (e.hasMoreElements()) {
            ZipEntry entry = e.nextElement();
            File destinationFilePath = new File(destPath, getRealName(entry.getName()));
            if (entry.isDirectory() || (ext != null && !destinationFilePath.getName().endsWith(ext))) {
                continue;
            }
            destinationFilePath.getParentFile().mkdirs();
            Files.copy(zipObj.getInputStream(entry), destinationFilePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        zipObj.close();
    }

    /**
     * ZIP压缩
     *
     * @param inputFile
     *            输入文件
     * @param zipFileName
     *            输出文件名称
     * @throws IOException
     *             IO异常
     */
    public static void zip(File inputFile, String zipFileName) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName));
        BufferedOutputStream bos = new BufferedOutputStream(zos);
        zip(zos, inputFile, inputFile.getName(), bos);
        bos.close();
        zos.close(); // 输出流关闭  
    }

    /**
     * ZIP压缩
     *
     * @param zos
     *            Zip输出流
     * @param file
     *            添加的文件
     * @param base
     *            基础目录
     * @param bos
     *            输出流
     * @throws IOException
     *             IO异常
     */
    private static void zip(ZipOutputStream zos, File file, String base, BufferedOutputStream bos) throws IOException { // 方法重载  
        if (file.isDirectory()) {
            File[] fl = file.listFiles();
            if (fl == null || fl.length == 0) {
                zos.putNextEntry(new ZipEntry(base + "/")); // 创建zip压缩进入点base  
            } else {
                for (File fl1 : fl) {
                    zip(zos, fl1, base + "/" + fl1.getName(), bos); // 递归遍历子文件夹  
                }
            }
        } else {
            zos.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base  
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            int b;
            while ((b = bis.read()) != -1) {
                bos.write(b); // 将字节流写入当前zip目录  
            }
            bos.flush();
            bis.close();
            fis.close(); // 输入流关闭  
        }
    }

    /**
     * GZIP压缩文件
     *
     * @param file
     *            压缩文件
     * @throws IOException
     *             IO异常
     */
    public static void gzip(File file) throws IOException {
        gzip(file, file.getName() + ".gz", true);
    }

    /**
     * GZIP压缩文件
     *
     * @param file
     *            压缩文件
     * @param out
     *            输出名称
     * @throws IOException
     *             IO异常
     */
    public static void gzip(File file, String out) throws IOException {
        gzip(file, out, true);
    }

    /**
     * GZIP压缩文件
     *
     * @param file
     *            压缩文件
     * @param out
     *            输出名称
     * @param delete
     *            是否删除
     * @throws IOException
     *             IO异常
     */
    public static void gzip(File file, String out, boolean delete) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(out);
        gzip(fis, fos);
        fis.close();
        fos.flush();
        fos.close();
        if (delete) {
            file.delete();
        }
    }

    /**
     * GZIP压缩数据流
     *
     * @param is
     *            输入流
     * @param os
     *            输出流
     * @throws IOException
     *             IO异常
     */
    public static void gzip(InputStream is, OutputStream os) throws IOException {
        GZIPOutputStream gos = new GZIPOutputStream(os);
        int count;
        byte data[] = new byte[1024];
        while ((count = is.read(data, 0, 1024)) != -1) {
            gos.write(data, 0, count);
        }
        gos.finish();
        gos.flush();
        gos.close();
    }
}
