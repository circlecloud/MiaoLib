package pw.yumc.MiaoLib.config.yaml;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import pw.yumc.MiaoLib.bukkit.L;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BukkitConstructor extends YamlConstructor {
    public static BukkitConstructor DEFAULT = new BukkitConstructor();
    Map<String, Method> constructor = new HashMap<>();

    public BukkitConstructor() {
        this.yamlConstructors.put(Tag.MAP, new ConstructCustomObject());
        this.loadConstructor();
    }

    public static void register(String classname, Method method) {
        DEFAULT.constructor.put(classname, method);
    }

    private void loadConstructor() {
        constructor.put(Location.class.getName(), L.deserialize);
    }

    private class ConstructCustomObject extends ConstructYamlMap {
        @Override
        public Object construct(Node node) {
            if (node.isTwoStepsConstruction()) { throw new YAMLException("Unexpected referential mapping structure. Node: " + node); }

            Map<?, ?> raw = (Map<?, ?>) super.construct(node);

            if (raw.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                Map<String, Object> typed = new LinkedHashMap<>(raw.size());
                for (Map.Entry<?, ?> entry : raw.entrySet()) {
                    typed.put(entry.getKey().toString(), entry.getValue());
                }

                // 自定义解析部分
                String key = raw.get(ConfigurationSerialization.SERIALIZED_TYPE_KEY).toString();
                if (constructor.containsKey(key)) {
                    try {
                        return constructor.get(key).invoke(null, typed);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        throw new YAMLException("Could not deserialize object", ex);
                    }
                }

                // Bukkit自动解析
                try {
                    return ConfigurationSerialization.deserializeObject(typed);
                } catch (IllegalArgumentException ex) {
                    throw new YAMLException("Could not deserialize object", ex);
                }
            }

            return raw;
        }

        @Override
        public void construct2ndStep(Node node, Object object) {
            throw new YAMLException("Unexpected referential mapping structure. Node: " + node);
        }
    }
}
