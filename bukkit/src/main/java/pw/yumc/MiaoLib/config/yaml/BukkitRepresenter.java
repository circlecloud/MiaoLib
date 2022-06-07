package pw.yumc.MiaoLib.config.yaml;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import pw.yumc.MiaoLib.bukkit.L;

public class BukkitRepresenter extends YamlRepresenter {
    public static BukkitRepresenter DEFAULT = new BukkitRepresenter();

    public BukkitRepresenter() {
        this.multiRepresenters.put(Location.class, new RepresentLocation());
    }

    public static void register(Class<?> clazz, Represent represent) {
        DEFAULT.multiRepresenters.put(clazz, represent);
    }

    public class RepresentLocation extends RepresentMap {
        @Override
        public Node representData(Object data) {
            return super.representData(L.serialize((Location) data));
        }
    }
}
