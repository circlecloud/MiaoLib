package pw.yumc.MiaoLib.sql.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 数据库键值管理类
 *
 * @since 2015年12月14日 下午1:26:24
 * @author 喵♂呜
 */
public class KeyValue {

    private Map<Object, Object> keyvalues = new HashMap<>();

    /**
     * 数据库键值管理类
     */
    public KeyValue() {
    }

    /**
     * 数据库键值管理类
     *
     * @param key
     *            键
     * @param value
     *            值
     */
    public KeyValue(String key, Object value) {
        add(key, value);
    }

    /**
     * 添加数据
     *
     * @param key
     *            键
     * @param value
     *            值
     * @return {@link KeyValue}
     */
    public KeyValue add(String key, Object value) {
        this.keyvalues.put(key, value);
        return this;
    }

    /**
     * 获得所有的键
     *
     * @return 所有的键
     */
    public String[] getKeys() {
        return this.keyvalues.keySet().toArray(new String[] {});
    }

    /**
     * 获得值
     *
     * @param key
     *            查询的键
     * @return 值
     */
    public String getString(String key) {
        Object obj = this.keyvalues.get(key);
        return obj == null ? "" : obj.toString();
    }

    /**
     * 获得所有的值
     *
     * @return 所有的值
     */
    public Object[] getValues() {
        List<Object> keys = new ArrayList<>();
        for (Entry<Object, Object> next : this.keyvalues.entrySet()) {
            keys.add(next.getValue());
        }
        return keys.toArray(new Object[keys.size()]);
    }

    /**
     * 判断数据是否为空
     *
     * @return 数据是否为空
     */
    public boolean isEmpty() {
        return this.keyvalues.isEmpty();
    }

    /**
     * 转换为数据表创建SQL语句
     *
     * @return 数据表创建SQL语句
     */
    public String toCreateString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<Object, Object> next : this.keyvalues.entrySet()) {
            sb.append("`");
            sb.append(next.getKey());
            sb.append("` ");
            sb.append(next.getValue());
            sb.append(", ");
        }
        return sb.toString().substring(0, sb.length() - 2);
    }

    /**
     * 转换字段为数据添加SQL语句
     *
     * @return 添加SQL语句
     */
    public String toInsertString() {
        String ks = "";
        String vs = "";
        for (Entry<Object, Object> next : this.keyvalues.entrySet()) {
            ks += "`" + next.getKey() + "`, ";
            vs += "'" + next.getValue() + "', ";
        }
        return "(" + ks.substring(0, ks.length() - 2) + ") VALUES (" + vs.substring(0, vs.length() - 2) + ")";
    }

    /**
     * @return 转换为键列
     */
    public String toKeys() {
        StringBuilder sb = new StringBuilder();
        for (Object next : this.keyvalues.keySet()) {
            sb.append("`");
            sb.append(next);
            sb.append("`, ");
        }
        return sb.toString().substring(0, sb.length() - 2);
    }

    @Override
    public String toString() {
        return this.keyvalues.toString();
    }

    /**
     * 转换字段为更新SQL语句
     *
     * @return 更新SQL语句
     */
    public String toUpdateString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<Object, Object> next : this.keyvalues.entrySet()) {
            sb.append("`");
            sb.append(next.getKey());
            sb.append("`='");
            sb.append(next.getValue());
            sb.append("' ,");
        }
        return sb.substring(0, sb.length() - 2);
    }

    /**
     * 转换字段为查询SQL语句
     *
     * @return 查询SQL语句
     */
    public String toWhereString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<Object, Object> next : this.keyvalues.entrySet()) {
            sb.append("`");
            sb.append(next.getKey());
            sb.append("`='");
            sb.append(next.getValue());
            sb.append("' and ");
        }
        return sb.substring(0, sb.length() - 5);
    }
}
