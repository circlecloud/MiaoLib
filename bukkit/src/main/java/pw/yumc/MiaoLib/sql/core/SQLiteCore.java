package pw.yumc.MiaoLib.sql.core;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

/**
 * 数据库操作类
 *
 * @since 2015年7月14日 下午3:25:06
 * @author 喵♂呜
 */
public class SQLiteCore extends DataBaseCore {
    private static String driverName = "org.sqlite.JDBC";
    private Connection connection;
    private File dbFile;

    /**
     * 初始化连接信息
     *
     * @param dbFile
     *            数据库文件
     */
    public SQLiteCore(File dbFile) {
        this.dbFile = dbFile;
        if (this.dbFile.exists()) {
            // So we need a new connection
            try {
                this.dbFile.createNewFile();
            } catch (IOException e) {
                warn("数据库文件 " + dbFile.getAbsolutePath() + " 创建失败!");
                e.printStackTrace();
            }
        }
        try {
            Class.forName(driverName).newInstance();
        } catch (Exception e) {
            warn("数据库初始化失败 请检查驱动 " + driverName + " 是否存在!");
            e.printStackTrace();
        }
    }

    /**
     * 初始化连接信息
     *
     * @param plugin
     *            插件实体
     * @param cfg
     *            配置信息
     */
    public SQLiteCore(Plugin plugin, ConfigurationSection cfg) {
        this(plugin, cfg.getString("database"));
    }

    /**
     * 初始化连接信息
     *
     * @param plugin
     *            插件实体
     * @param filename
     *            文件名称
     */
    public SQLiteCore(Plugin plugin, String filename) {
        this.dbFile = new File(plugin.getDataFolder(), filename + ".db");
        if (this.dbFile.exists()) {
            // So we need a new connection
            try {
                this.dbFile.createNewFile();
            } catch (IOException e) {
                warn("数据库文件 " + this.dbFile.getAbsolutePath() + " 创建失败!");
                e.printStackTrace();
            }
        }
        try {
            Class.forName(driverName).newInstance();
        } catch (Exception e) {
            warn("数据库初始化失败 请检查驱动 " + driverName + " 是否存在!");
            e.printStackTrace();
        }
    }

    /**
     * 初始化连接信息
     *
     * @param filepath
     *            文件路径
     */
    public SQLiteCore(String filepath) {
        this(new File(filepath));
    }

    @Override
    public boolean createTables(String tableName, KeyValue fields, String Conditions) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS `%s` ( %s %s )";
        return execute(String.format(sql, tableName, fields.toCreateString().replace("AUTO_INCREMENT", "AUTOINCREMENT"), Conditions == null ? "" : " , " + Conditions));
    }

    /**
     * @return 获得自增关键词
     */
    @Override
    public String getAUTO_INCREMENT() {
        return "AUTOINCREMENT";
    }

    @Override
    public Connection getConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                return this.connection;
            }
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
            return this.connection;
        } catch (SQLException e) {
            warn("数据库操作出错: " + e.getMessage());// 得到出错信息
            warn("数据库文件: " + this.dbFile.getAbsolutePath()); // 发生错误时，将连接数据库信息打印出来
            return null;
        }
    }

}
