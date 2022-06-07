package pw.yumc.MiaoLib.commands;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pw.yumc.MiaoLib.commands.annotation.Option;
import pw.yumc.MiaoLib.commands.exception.ParseException;
import pw.yumc.MiaoLib.bukkit.Log;

/**
 * 命令参数解析
 *
 * @author 喵♂呜
 * @since 2016年10月5日 下午4:02:04
 */
public class CommandParse {
    private static Map<Class, Class> allParse = new HashMap<>();
    private static Map<String, Class> primitiveMap = new HashMap<>();
    private boolean isMain;
    private List<BaseParse> pars = new LinkedList<>();

    static {
        register(File.class, FileParse.class);
        register(Player.class, PlayerParse.class);
        register(String.class, StringParse.class);
        primitiveMap.put("boolean", Boolean.class);
        primitiveMap.put("byte", Byte.class);
        primitiveMap.put("char", Character.class);
        primitiveMap.put("short", Short.class);
        primitiveMap.put("int", Integer.class);
        primitiveMap.put("long", Long.class);
        primitiveMap.put("float", Float.class);
        primitiveMap.put("double", Double.class);
    }

    private CommandParse(Class[] classes, Annotation[][] annons, boolean isMain) {
        this.isMain = isMain;
        // 第一个参数实现了CommandSender忽略
        for (int i = 1; i < classes.length; i++) {
            Class clazz = classes[i];
            if (clazz.isPrimitive()) {
                // boolean, byte, char, short, int, long, float, and double.
                clazz = primitiveMap.get(clazz.getName());
            }
            Annotation[] annotations = annons[i];
            BaseParse baseParse = null;
            if (allParse.containsKey(clazz)) {
                try {
                    baseParse = (BaseParse) allParse.get(clazz).newInstance();
                } catch (InstantiationException | IllegalAccessException | NullPointerException ignored) {
                }
            } else {
                try {
                    baseParse = new ValueOfParse(clazz, clazz.getDeclaredMethod("valueOf", String.class));
                } catch (NoSuchMethodException ignored) {
                }
            }
            if (baseParse == null) { throw new ParseException(String.format("存在无法解析的参数类型 %s", clazz.getName())); }
            this.pars.add(baseParse.parseAnnotation(annotations).handleAttrs());
        }
        Log.d("命令解析器 %s", Log.getSimpleNames(pars.toArray()));
    }

    public static CommandParse get(Method method) {
        return new CommandParse(method.getParameterTypes(), method.getParameterAnnotations(), method.getReturnType().equals(boolean.class));
    }

    /**
     * 转化数组为字符串
     *
     * @param arr
     *         数组
     * @return 字符串
     */
    private static String join(Object[] arr) {
        StringBuilder str = new StringBuilder();
        for (Object s : arr) {
            str.append(s.toString());
            str.append(" ");
        }
        return str.length() > " ".length() ? str.toString().substring(0, str.length() - " ".length()) : str.toString();
    }

    public static void register(Class clazz, Class parse) {
        allParse.put(clazz, parse);
    }

    public Object[] parse(CommandSender sender, String label, String[] args) {
        List<Object> pobjs = new LinkedList<>();
        pobjs.add(sender);
        for (int i = 0; i < pars.size(); i++) {
            try {
                BaseParse p = pars.get(i);
                String param = i < args.length ? args[i] : null;
                param = param == null ? p.getDefault(sender) : param;
                // 参数大于解析器 并且为最后一个参数
                if (i + 1 == pars.size() && args.length >= pars.size()) {
                    param = join(Arrays.copyOfRange(args, i, args.length));
                }
                // 尝试让解析器解析Null参数
                try { pobjs.add(p.parse(sender, param)); } catch (NullPointerException npe) { pobjs.add(null); }
            } catch (Exception e) {
                Log.fd(e);
                throw new ParseException(String.format("第 %s 个参数 %s", isMain ? 1 : 2 + i, e.getMessage()));
            }
        }
        Log.d("解析参数: %s => %s", Arrays.toString(args), Log.getSimpleNames(pobjs.toArray()));
        return pobjs.toArray();
    }

    public static abstract class BaseParse<RT> {
        protected Map<String, String> attrs = new HashMap<>();
        protected String def;
        protected int max = Integer.MAX_VALUE;
        protected int min = 0;

        public String getDefault(CommandSender sender) {
            return def;
        }

        /**
         * 解析参数
         *
         * @param sender
         *         发送者
         * @param arg
         *         命令参数
         * @return 解析后的数据
         * @throws ParseException
         *         解析异常
         */
        public abstract RT parse(CommandSender sender, String arg) throws ParseException;

        public BaseParse<RT> parseAnnotation(Annotation[] annotations) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Option.class) {
                    String value = ((Option) annotation).value();
                    for (String str : value.split(" ")) {
                        if (str.isEmpty()) {
                            continue;
                        }
                        if (str.contains(":")) {
                            String[] args = str.split(":");
                            attrs.put(args[0], args[1]);
                        } else {
                            attrs.put(str, null);
                        }
                    }
                }
            }
            return this;
        }

        public BaseParse<RT> handleAttrs() {
            if (attrs.containsKey("def")) {
                def = String.valueOf(attrs.get("def"));
            }
            if (attrs.containsKey("min")) {
                min = Integer.parseInt(String.valueOf(attrs.get("min")));
            }
            if (attrs.containsKey("max")) {
                max = Integer.parseInt(String.valueOf(attrs.get("max")));
            }
            return this;
        }

        public <T> T throwException(String str, Object... objects) {
            throw new ParseException(String.format(str, objects));
        }

        public void throwRange() {
            throwRange("");
        }

        public <T> T throwRange(String str) {
            return throwException(str.isEmpty() ? "范围必须在 %s 到 %s 之间!" : str, min, max);
        }
    }

    public static class ValueOfParse extends BaseParse<Object> {
        private Class eType;
        private Enum[] eList;
        private Method method;
        private Method checker;

        public ValueOfParse(Class eType, Method method) {
            this.eType = eType;
            try {
                checker = eType.getDeclaredMethod("doubleValue");
            } catch (NoSuchMethodException ignored) {
            }
            this.method = method;
            if (eType.isEnum()) {
                this.eList = ((Class<Enum>) eType).getEnumConstants();
            }
        }

        @Override
        public Object parse(CommandSender sender, String arg) {
            try {
                Object result = method.invoke(null, arg);
                if (checker != null) {
                    double num = (double) checker.invoke(result);
                    if (min > num || num > max) {
                        throwRange();
                    }
                }
                return result;
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException ex) {
                if (eType.isEnum() && eList.length < 21) {
                    return throwException("%s 不是 %s 有效值为 %s", arg, eType.getSimpleName(), Arrays.toString(eList));
                } else {
                    return throwException("%s 不是一个有效的 %s", arg, eType.getSimpleName());
                }
            }
        }
    }

    public static class FileParse extends BaseParse<File> {
        @Override
        public File parse(CommandSender sender, String arg) throws ParseException {
            File file = new File(arg);
            if (attrs.containsKey("check") && !file.exists()) { throw new ParseException("文件 " + arg + " 不存在!"); }
            return file;
        }
    }

    public static class PlayerParse extends BaseParse<Player> {
        boolean sender = false;
        boolean check = false;

        @Override
        public Player parse(CommandSender sender, String arg) {
            Player p = Bukkit.getPlayerExact(arg);
            if (this.check && p == null) { return throwException("玩家 %s 不存在或不在线!", arg); }
            return p;
        }

        @Override
        public BaseParse<Player> handleAttrs() {
            super.handleAttrs();
            sender = attrs.containsKey("sender");
            check = attrs.containsKey("check");
            return this;
        }

        @Override
        public String getDefault(CommandSender sender) {
            return this.sender && sender instanceof Player ? sender.getName() : super.getDefault(sender);
        }
    }

    public static class StringParse extends BaseParse<String> {
        List<String> options;

        @Override
        public String parse(CommandSender sender, String arg) {
            if (min > arg.length() || arg.length() > max) { return throwRange("长度必须在 %s 和 %s 之间!"); }
            if (options != null && !options.contains(arg)) { return throwException("参数 %s 不是一个有效的选项 有效值为 %s", arg, options); }
            return arg;
        }

        @Override
        public BaseParse<String> handleAttrs() {
            super.handleAttrs();
            if (attrs.containsKey("option")) {
                options = Arrays.asList(attrs.get("option").split(","));
            }
            return this;
        }
    }
}
