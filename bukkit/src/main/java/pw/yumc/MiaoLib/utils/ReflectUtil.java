package pw.yumc.MiaoLib.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Minecraft反射类
 *
 * @since 2015年12月14日 下午1:35:11
 * @author 许凯
 */
@SuppressWarnings("all")
public class ReflectUtil {

    public static Field getDeclaredFieldByName(Class source, String name) {
        try {
            Field field = source.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Field> getDeclaredFieldByType(Class source, Class type) {
        List<Field> list = new ArrayList<>();
        for (Field field : source.getDeclaredFields()) {
            if (field.getType() == type) {
                field.setAccessible(true);
                list.add(field);
            }
        }
        return list;
    }

    public static Method getDeclaredMethod(Class clzz, String methodName, Class... args) {
        try {
            return clzz.getDeclaredMethod(methodName, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getDeclaredMethodByNameAndParams(Class source, String name, Class... args) {
        for (Method method : findMethodByParams(source.getDeclaredMethods(), args)) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    public static List<Method> getDeclaredMethodByNameAndType(Class source, String name, Class returnType) {
        List<Method> methods = new ArrayList<>();
        for (Method method : source.getDeclaredMethods()) {
            if (method.getName().equals(name) && method.getReturnType().equals(returnType)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static List<Method> getDeclaredMethodByParams(Class source, Class... args) {
        return findMethodByParams(source.getDeclaredMethods(), args);
    }

    public static List<Method> getDeclaredMethodByParamsAndType(Class source, Class returnType, Class... args) {
        List<Method> methods = new ArrayList<>();
        for (Method method : findMethodByParams(source.getDeclaredMethods(), args)) {
            if (method.getReturnType().equals(returnType)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static List<Method> getDeclaredMethodByType(Class source, Class returnType) {
        List<Method> methods = new ArrayList<>();
        for (Method method : source.getDeclaredMethods()) {
            if (method.getReturnType().equals(returnType)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static Field getFieldByName(Class source, String name) {
        try {
            Field field = source.getField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Field> getFieldByType(Class source, Class type) {
        List<Field> list = new ArrayList<>();
        for (Field field : source.getFields()) {
            if (field.getType() == type) {
                field.setAccessible(true);
                list.add(field);
            }
        }
        return list;
    }

    public static Object getHandle(Object bukkitObj) {
        try {
            return bukkitObj.getClass().getMethod("getHandle").invoke(bukkitObj);
        } catch (Exception e) {
        }
        return null;
    }

    public static Method getMethodByNameAndParams(Class source, String name, Class... args) {
        for (Method method : findMethodByParams(source.getMethods(), args)) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    public static List<Method> getMethodByNameAndType(Class source, String name, Class returnType) {
        List<Method> methods = new ArrayList<>();
        for (Method method : source.getMethods()) {
            if (method.getName().equals(name) && method.getReturnType().equals(returnType)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static List<Method> getMethodByParams(Class source, Class... args) {
        return findMethodByParams(source.getMethods(), args);
    }

    public static List<Method> getMethodByParamsAndType(Class source, Class returnType, Class... args) {
        List<Method> methods = new ArrayList<>();
        for (Method method : findMethodByParams(source.getMethods(), args)) {
            if (method.getReturnType().equals(returnType)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static List<Method> getMethodByType(Class source, Class returnType) {
        List<Method> methods = new ArrayList<>();
        for (Method method : source.getMethods()) {
            if (method.getReturnType().equals(returnType)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static void invokeMethod(Object object, String methodName, Class arg, Object value) {
        try {
            Method m = object.getClass().getDeclaredMethod(methodName, arg);
            m.invoke(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void invokeMethod(Object object, String methodName, Class[] args, Object[] value) {
        try {
            Method m = object.getClass().getDeclaredMethod(methodName, args);
            m.invoke(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void invokeMethod(Object object, String methodName, Object value) {
        try {
            Method m = object.getClass().getDeclaredMethod(methodName, value.getClass());
            m.invoke(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Method> findMethodByParams(Method[] methods, Class... args) {
        List<Method> list = new ArrayList<>();
        start:
        for (Method method : methods) {
            if (method.getParameterTypes().length == args.length) {
                Class[] array = method.getParameterTypes();
                for (int i = 0; i < args.length; i++) {
                    if (!array[i].equals(args[i])) {
                        continue start;
                    }
                }
                method.setAccessible(true);
                list.add(method);
            }
        }
        return list;
    }
}
