package pw.yumc.MiaoLib.tellraw;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pw.yumc.MiaoLib.bukkit.Log;

/**
 * 物品序列化类
 *
 * @since 2016年9月9日 下午3:47:17
 * @author 喵♂呜
 */
public abstract class ItemSerialize {
    static ItemSerialize itemSerialize;
    static {
        try {
            itemSerialize = new Automatic();
        } catch (IllegalStateException e) {
            itemSerialize = new Manual();
            Log.d("初始化自动物品序列化失败!", e);
        }
    }

    public static String $(ItemStack item) {
        String result = itemSerialize.parse(item);
        Log.d("%s 物品序列化结果: %s", itemSerialize.getName(), result);
        return result;
    }

    public abstract String getName();

    public abstract String parse(ItemStack item);

    static class Automatic extends ItemSerialize {
        private static boolean inited = false;
        private static Method asNMSCopyMethod;
        private static Method nmsSaveNBTMethod;
        private static Class<?> nmsNBTTagCompound;
        private static String ver = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

        static {
            try {
                Class<?> cis = getOBCClass("inventory.CraftItemStack");
                asNMSCopyMethod = cis.getMethod("asNMSCopy", ItemStack.class);
                Class<?> nmsItemStack = asNMSCopyMethod.getReturnType();
                for (Method method : nmsItemStack.getMethods()) {
                    Class<?> rt = method.getReturnType();
                    if (method.getParameterTypes().length == 0 && "NBTTagCompound".equals(rt.getSimpleName())) {
                        nmsNBTTagCompound = rt;
                    }
                }
                for (Method method : nmsItemStack.getMethods()) {
                    Class<?>[] paras = method.getParameterTypes();
                    Class<?> rt = method.getReturnType();
                    if (paras.length == 1 && "NBTTagCompound".equals(paras[0].getSimpleName()) && "NBTTagCompound".equals(rt.getSimpleName())) {
                        nmsSaveNBTMethod = method;
                    }
                }
                inited = true;
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        public Automatic() {
            if (!inited) { throw new IllegalStateException("无法初始化自动处理类!"); }
        }

        @Override
        public String getName() {
            return "Automatic";
        }

        private static Class getOBCClass(String cname) throws ClassNotFoundException {
            return Class.forName("org.bukkit.craftbukkit." + ver + "." + cname);
        }

        @Override
        public String parse(ItemStack item) {
            try {
                return nmsSaveNBTMethod.invoke(asNMSCopyMethod.invoke(null, item), nmsNBTTagCompound.newInstance()).toString();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
                itemSerialize = new Manual();
                return itemSerialize.parse(item);
            }
        }
    }

    static class Manual extends ItemSerialize {

        @Override
        public String getName() {
            return "Manual";
        }

        @Override
        public String parse(ItemStack item) {
            return serialize(item);
        }

        /**
         * 显示序列化
         *
         * @param im
         *            物品属性
         * @return 获取显示序列化
         */
        private String getDisplay(ItemMeta im) {
            StringBuilder display = new StringBuilder();
            display.append("{");
            if (im.hasDisplayName()) {
                display.append(String.format("Name:\"%s\",", im.getDisplayName()));
            }
            if (im.hasLore()) {
                display.append("Lore:[");
                int i = 0;
                for (String line : im.getLore()) {
                    display.append(String.format("%s:\"%s\",", i, new JsonBuilder(line).toString()));
                    i++;
                }
                display.deleteCharAt(display.length() - 1);
                display.append("],");
            }
            display.deleteCharAt(display.length() - 1);
            display.append("}");
            return display.toString();
        }

        /**
         * 附魔序列化
         *
         * @param set
         *            附魔集合
         * @return 获得附魔序列化
         */
        private String getEnch(Set<Entry<Enchantment, Integer>> set) {
            StringBuilder enchs = new StringBuilder();
            for (Map.Entry<Enchantment, Integer> ench : set) {
                enchs.append(String.format("{id:%s,lvl:%s},", ench.getKey().getId(), ench.getValue()));
            }
            enchs.deleteCharAt(enchs.length() - 1);
            return enchs.toString();
        }

        /**
         * 属性序列化
         *
         * @param im
         *            物品属性
         * @return 获得属性序列化
         */
        private String getTag(ItemMeta im) {
            StringBuilder meta = new StringBuilder("{");
            if (im.hasEnchants()) {
                meta.append(String.format("ench:[%s],", getEnch(im.getEnchants().entrySet())));
            }
            if (im.hasDisplayName() || im.hasLore()) {
                meta.append(String.format("display:%s,", getDisplay(im)));
            }
            meta.deleteCharAt(meta.length() - 1);
            meta.append("}");
            return meta.toString();
        }

        /**
         * 序列化物品
         *
         * @param item
         *            {@link ItemStack}
         * @return 物品字符串
         */
        private String serialize(ItemStack item) {
            StringBuilder json = new StringBuilder("{");
            json.append(String.format("id:\"%s\",Damage:\"%s\"", item.getTypeId(), item.getDurability()));
            if (item.getAmount() > 1) {
                json.append(String.format(",Count:%s", item.getAmount()));
            }
            if (item.hasItemMeta()) {
                json.append(String.format(",tag:%s", getTag(item.getItemMeta())));
            }
            json.append("}");
            return json.toString();
        }
    }
}
