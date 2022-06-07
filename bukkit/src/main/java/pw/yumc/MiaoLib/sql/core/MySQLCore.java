package pw.yumc.MiaoLib.sql.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.bukkit.configuration.ConfigurationSection;

/**
 * 数据库操作类
 *
 * @since 2015年12月14日 下午1:26:39
 * @author 喵♂呜
 */
public class MySQLCore extends DataBaseCore {
    private static String driverName = "com.mysql.jdbc.Driver";
    private Connection connection;
    private Properties info;
    private String url;

    /**
     * 初始化连接信息
     *
     * @param cfg
     *            配置节点
     */
    public MySQLCore(ConfigurationSection cfg) {
        this(cfg.getString("ip", "127.0.0.1"), cfg.getInt("port", 3306), cfg.getString("database", "minecraft"), cfg.getString("username", "root"), cfg.getString("password", ""));
    }

    /**
     * 初始化连接信息
     *
     * @param host
     *            域名
     * @param port
     *            端口
     * @param dbname
     *            数据库
     * @param username
     *            用户名
     * @param password
     *            密码
     */
    public MySQLCore(String host, int port, String dbname, String username, String password) {
        this.info = new Properties();
        this.info.put("autoReconnect", "true");
        this.info.put("user", username);
        this.info.put("password", password);
        this.info.put("useUnicode", "true");
        this.info.put("characterEncoding", "utf8");
        this.info.put("useSSL", "false");
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + dbname + "";
        try {
            Class.forName(driverName).newInstance();
        } catch (Exception e) {
            warn("数据库初始化失败 请检查驱动 " + driverName + " 是否存在!");
        }
    }

    /**
     * 创建数据表
     *
     * @param tableName
     *            表名
     * @param fields
     *            字段参数
     * @param Conditions
     *            -附加值
     * @return 运行结果
     * @throws SQLException
     *             SQL异常
     */
    @Override
    public boolean createTables(String tableName, KeyValue fields, String Conditions) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS `%s` ( %s %s ) ENGINE = InnoDB DEFAULT CHARSET=UTF8";
        return execute(String.format(sql, tableName, fields.toCreateString(), Conditions == null ? "" : ", " + Conditions));
    }

    @Override
    public Connection getConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                return this.connection;
            }
            this.connection = DriverManager.getConnection(this.url, this.info);
            return this.connection;
        } catch (SQLException e) {
            warn("数据库操作出错: " + e.getMessage());// 得到出错信息
            warn("登录URL: " + this.url); // 发生错误时，将连接数据库信息打印出来
            warn("登录账户: " + this.info.getProperty("user"));
            warn("登录密码: " + this.info.getProperty("password"));
            return null;
        }
    }
}
