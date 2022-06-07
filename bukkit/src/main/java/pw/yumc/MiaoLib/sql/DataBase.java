package pw.yumc.MiaoLib.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.bukkit.P;
import pw.yumc.MiaoLib.config.inject.InjectParse;
import pw.yumc.MiaoLib.sql.core.DataBaseCore;
import pw.yumc.MiaoLib.sql.core.KeyValue;
import pw.yumc.MiaoLib.sql.core.MySQLCore;
import pw.yumc.MiaoLib.sql.core.SQLiteCore;

/**
 * 数据库管理类
 *
 * @since 2015年12月14日 下午1:26:06
 * @author 喵♂呜
 *
 */
public class DataBase {
    private DataBaseCore dataBaseCore;

    /**
     * 初始化数据库管理
     *
     * @param core
     *            数据库核心
     */
    public DataBase(DataBaseCore core) {
        this.dataBaseCore = core;
    }

    public static DataBase create(Plugin plugin, ConfigurationSection dbConfig) {
        ConfigurationSection cfg = dbConfig.getConfigurationSection("MySQL");
        if (dbConfig.getString("FileSystem").equalsIgnoreCase("MySQL")) {
            plugin.getLogger().info("已启用MySQL保存数据,开始连接数据库...");
            return new DataBase(new MySQLCore(cfg));
        }
        return new DataBase(new SQLiteCore(plugin, cfg));
    }

    /**
     * 关闭数据库连接
     *
     * @return 是否关闭成功
     */
    public boolean close() {
        try {
            this.dataBaseCore.getConnection().close();
            return true;
        } catch (SQLException e) {
            Log.d("数据库链接关闭失败!", e);
            return false;
        }
    }

    /**
     * 复制当前数据核心的数据库到指定的数据库
     * 此方法将不会删除当前数据库原有数据
     * 此方法可能花费较长的时间
     *
     * 注意: 当前方法将不会创建表在新的数据库内 需要自行创建数据表
     *
     * @param db
     *            接受数据的数据库核心
     * @return 是否转换成功
     */
    public boolean copyTo(DataBaseCore db) {
        try {
            String src = this.dataBaseCore.getConnection().getMetaData().getURL();
            String des = db.getConnection().getMetaData().getURL();
            Log.i("开始从源 " + src + " 复制数据到 " + des + " ...");
            ResultSet rs = this.dataBaseCore.getConnection().getMetaData().getTables(null, null, "%", null);
            List<String> tables = new LinkedList<>();
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
            info("源数据库中有 " + tables.size() + " 张数据表 ...");
            rs.close();
            int s = 0;
            long start = System.currentTimeMillis();
            for (String table : tables) {
                Log.i("开始复制源数据库中的表 " + table + " ...");
                if (table.toLowerCase().startsWith("sqlite_autoindex_")) {
                    continue;
                }
                Log.i("清空目标数据库中的表 " + table + " ...");
                db.execute("DELETE FROM " + table);
                rs = this.dataBaseCore.query("SELECT * FROM " + table);
                int n = 0;
                String query = "INSERT INTO " + table + " VALUES (";
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    query += "?, ";
                }
                query = query.substring(0, query.length() - 2) + ")";

                Connection con = db.getConnection();
                try {
                    con.setAutoCommit(false);
                    PreparedStatement ps = con.prepareStatement(query);
                    long time = System.currentTimeMillis();
                    while (rs.next()) {
                        n++;
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            ps.setObject(i, rs.getObject(i));
                        }
                        ps.addBatch();
                        if (n % 100 == 0) {
                            try {
                                ps.executeBatch();
                                con.commit();
                            } catch (SQLException e) {
                                info("#====================================================");
                                info("#数据复制区段(不是ID!) " + (n - 100) + "-" + n + " 出现错误...");
                                info("#错误信息如下: ");
                                e.printStackTrace();
                                info("#====================================================");
                            }
                        }
                        if (System.currentTimeMillis() - time > 500) {
                            info("已复制 " + n + " 条记录...");
                            time = System.currentTimeMillis();
                        }
                    }
                    s += n;
                    ps.executeBatch();
                    con.commit();
                    info("数据表 " + table + " 复制完成 共 " + n + " 条记录...");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    con.setAutoCommit(true);
                }
                rs.close();
            }
            info("成功从 " + src + " 复制 " + s + " 条数据到 " + des + " 耗时 " + (System.currentTimeMillis() - start) / 1000 + " 秒...");
            db.getConnection().close();
            this.dataBaseCore.getConnection().close();
            return true;
        } catch (SQLException e) {
            return false;
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
     */
    public boolean createTables(String tableName, KeyValue fields, String Conditions) {
        try {
            this.dataBaseCore.createTables(tableName, fields, Conditions);
            return isTableExists(tableName);
        } catch (Exception e) {
            sqlerr("创建数据表 " + tableName + " 异常(内部方法)...", e);
            return false;
        }
    }

    /**
     * 对数据库表中的记录进行删除操作
     *
     * @param tableName
     *            表名
     * @param fields
     *            删除条件
     * @return 受到影响的数据条目
     */
    public int dbDelete(String tableName, KeyValue fields) {
        String sql = "DELETE FROM `" + tableName + "` WHERE " + fields.toWhereString();
        try {
            return this.dataBaseCore.update(sql);
        } catch (Exception e) {
            sqlerr(sql, e);
            return 0;
        }
    }

    /**
     * 判断数据库某个值是否存在!
     *
     * @param tableName
     *            数据库表名
     * @param fields
     *            选择条件
     * @return 首个符合条件的结果
     */
    public boolean dbExist(String tableName, KeyValue fields) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + fields.toWhereString();
        try {
            return this.dataBaseCore.query(sql).next();
        } catch (Exception e) {
            sqlerr(sql, e);
            return false;
        }
    }

    /**
     * 对数据库表进行插入操作
     *
     * @param tabName
     *            表名
     * @param fields
     *            带键值的
     * @return 受到影响的数据条目
     */
    public int dbInsert(String tabName, KeyValue fields) {
        String sql = "INSERT INTO `" + tabName + "` " + fields.toInsertString();
        try {
            return this.dataBaseCore.update(sql);
        } catch (Exception e) {
            sqlerr(sql, e);
            return 0;
        }

    }

    /**
     * 对数据库表进行选择操作！
     *
     * @param tableName
     *            数据库表名
     * @param fields
     *            读取的字段
     * @param selCondition
     *            选择条件
     * @return 一个含有KeyValue的List（列表）
     */
    public List<KeyValue> dbSelect(String tableName, KeyValue fields, KeyValue selCondition) {
        String sql = "SELECT " + fields.toKeys() + " FROM `" + tableName + "`" + (selCondition == null ? "" : " WHERE " + selCondition.toWhereString());
        List<KeyValue> kvlist = new ArrayList<>();
        try {
            ResultSet dbresult = this.dataBaseCore.query(sql);
            while (dbresult.next()) {
                KeyValue kv = new KeyValue();
                for (String col : fields.getKeys()) {
                    kv.add(col, dbresult.getString(col));
                }
                kvlist.add(kv);
            }
        } catch (Exception e) {
            sqlerr(sql, e);
        }
        return kvlist;
    }

    /**
     * 对数据库表进行选择操作！
     *
     * @param tableName
     *            数据库表名
     * @param selCondition
     *            选择条件
     * @param fields
     *            读取的字段
     * @return 一个含有KeyValue的List（列表）
     */
    public List<KeyValue> dbSelect(String tableName, KeyValue selCondition, String... fields) {
        String sql = "SELECT " + getKeys(fields) + " FROM `" + tableName + "`" + (selCondition == null ? "" : " WHERE " + selCondition.toWhereString());
        List<KeyValue> kvlist = new ArrayList<>();
        try {
            ResultSet dbresult = this.dataBaseCore.query(sql);
            while (dbresult.next()) {
                KeyValue kv = new KeyValue();
                for (String col : fields) {
                    kv.add(col, dbresult.getString(col));
                }
                kvlist.add(kv);
            }
        } catch (Exception e) {
            sqlerr(sql, e);
        }
        return kvlist;
    }

    /**
     * 对数据库表进行选择操作！
     *
     * @param tableName
     *            数据库表名
     * @param fields
     *            字段名
     * @param selConditions
     *            选择条件
     * @return 首个符合条件的结果
     */
    public String dbSelectFirst(String tableName, String fields, KeyValue selConditions) {
        String sql = "SELECT " + fields + " FROM " + tableName + " WHERE " + selConditions.toWhereString() + " limit 1";
        try {
            ResultSet dbresult = this.dataBaseCore.query(sql);
            if (dbresult.next()) { return dbresult.getString(fields); }
        } catch (Exception e) {
            sqlerr(sql, e);
        }
        return null;
    }

    /**
     * 对数据库表中记录进行更新操作
     *
     * @param tabName
     *            表名
     * @param fields
     *            字段参数
     * @param upCondition
     *            更新条件
     * @return 受到影响的数据条目
     */
    public int dbUpdate(String tabName, KeyValue fields, KeyValue upCondition) {
        String sql = "UPDATE `" + tabName + "` SET " + fields.toUpdateString() + " WHERE " + upCondition.toWhereString();
        try {
            return this.dataBaseCore.update(sql);
        } catch (Exception e) {
            sqlerr(sql, e);
            return 0;
        }
    }

    /**
     * 获得当前使用的数据库核心
     *
     * @return 数据库核心
     */
    public DataBaseCore getDataBaseCore() {
        return this.dataBaseCore;
    }

    /**
     * 字段数组转字符串
     *
     * @param fields
     *            字段数组
     * @return 字段字符串
     */
    public String getKeys(String... fields) {
        StringBuilder sb = new StringBuilder();
        for (String string : fields) {
            sb.append("`");
            sb.append(string);
            sb.append("`, ");
        }
        return sb.toString().substring(0, sb.length() - 2);
    }

    public boolean isFieldExists(String tableName, KeyValue fields) {
        DatabaseMetaData dbm;
        ResultSet tables;
        try {
            dbm = this.dataBaseCore.getConnection().getMetaData();
            tables = dbm.getTables(null, null, tableName, null);
            if (tables.next()) {
                ResultSet f = dbm.getColumns(null, null, tableName, fields.getKeys()[0]);
                return f.next();
            }
        } catch (SQLException e) {
            sqlerr("判断 表名:" + tableName + " 字段名:" + fields.getKeys()[0] + " 是否存在时出错!", e);
        }
        return false;
    }

    /**
     * 判断数据表是否存在
     *
     * @param tableName
     *            表名
     * @return 是否存在
     */
    public boolean isTableExists(String tableName) {
        try {
            DatabaseMetaData dbm = this.dataBaseCore.getConnection().getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            return tables.next();
        } catch (SQLException e) {
            sqlerr("判断 表名:" + tableName + " 是否存在时出错!", e);
            return false;
        }
    }

    /**
     * 批量执行SQL语句
     *
     * @param sqls
     *            SQL语句列表
     */
    public void runSqlList(Collection<String> sqls) {
        Connection con = getDataBaseCore().getConnection();
        long start = System.currentTimeMillis();
        try {
            long time = System.currentTimeMillis();
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            int i = 0;
            for (String sql : sqls) {
                st.addBatch(sql);
                i++;
                if (i % 100 == 0) {
                    st.executeBatch();
                    con.commit();
                    if (System.currentTimeMillis() - time > 500) {
                        info("已执行 " + i + " 条语句...");
                        time = System.currentTimeMillis();
                    }
                }
            }
            st.executeBatch();
            con.commit();
            info("执行SQL完毕 总计: " + sqls.size() + " 条 耗时: " + start + "ms!");
        } catch (SQLException e) {
            try {
                con.rollback();
                sqlerr("执行SQL数组发生错误 数据已回滚...", e);
            } catch (SQLException e1) {
                sqlerr("执行SQL数组发生错误 警告! 数据回滚失败...", e1);
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * 输出SQL错误
     *
     * @param sql
     *            SQL语句
     * @param e
     *            错误异常
     */
    public void sqlerr(String sql, Exception e) {
        info("数据库操作出错: " + e.getMessage());
        info("SQL查询语句: " + sql);
        Log.d(this.getClass().getName());
        Log.d(e);
    }

    /**
     * 测试数据库连接
     *
     * @return 是否连接成功
     */
    public boolean testConnect() {
        return this.dataBaseCore.getConnection() != null;
    }

    private void info(String info) {
        Log.i(info);
    }

    public static class DataBaseParse implements InjectParse.Parse<DataBase> {
        public DataBaseParse() {
            InjectParse.register(DataBase.class, this);
        }

        @Override
        public DataBase parse(ConfigurationSection config, String path) {
            return DataBase.create(P.instance, config.getConfigurationSection(path));
        }
    }
}
