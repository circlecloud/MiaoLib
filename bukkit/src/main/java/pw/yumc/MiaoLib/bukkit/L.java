package pw.yumc.MiaoLib.bukkit;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.NumberConversions;

/**
 * Location区域处理
 *
 * @author 喵♂呜
 * @since 2016年10月2日 下午11:35:56
 */
public class L {
    public static Method deserialize;
    static {
        try {
            deserialize = L.class.getDeclaredMethod("deserialize", Map.class);
        } catch (NoSuchMethodException | SecurityException e) {
            Log.w("%s 的反序列化方法获取失败!", L.class.getName());
        }
    }

    /**
     * 反序列化Location
     *
     * @param args
     *            map to deserialize
     * @return deserialized location
     * @throws IllegalArgumentException
     *             if the world don't exists
     * @see ConfigurationSerializable
     */
    public static Location deserialize(Map<String, Object> args) {
        World world = Bukkit.getWorld((String) args.get("world"));
        if (world == null) { return null; }
        return new Location(world, NumberConversions.toDouble(args.get("x")), NumberConversions.toDouble(args.get("y")), NumberConversions.toDouble(args.get("z")), NumberConversions
                .toFloat(args.get("yaw")), NumberConversions.toFloat(args.get("pitch")));
    }

    /**
     * 序列化Location
     *
     * @param loc
     *            地点
     * @return Map
     */
    public static Map<String, Object> serialize(Location loc) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, Location.class.getName());
        data.put("world", loc.getWorld().getName());
        data.put("x", loc.getX());
        data.put("y", loc.getY());
        data.put("z", loc.getZ());
        data.put("yaw", loc.getYaw());
        data.put("pitch", loc.getPitch());
        return data;
    }
}
