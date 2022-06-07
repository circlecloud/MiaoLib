package pw.yumc.MiaoLib.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A wrapper for an {@link Object} or {@link Class} upon which reflective calls can be made.
 * <p>
 * An example of using <code>Reflect</code> is
 * 
 * <pre>
 * // Static import all reflection methods to decrease verbosity
 * import static org.joor.Reflect.*;
 *
 * // Wrap an Object / Class / class name with the on() method:
 * on("java.lang.String")
 * // Invoke constructors using the create() method:
 * .create("Hello World")
 * // Invoke methods using the call() method:
 * .call("toString")
 * // Retrieve the wrapped object
 * </pre>
 * 
 * @author Lukas Eder
 */
public class Reflect {

    // ---------------------------------------------------------------------
    // Static API used as entrance points to the fluent API
    // ---------------------------------------------------------------------

    /**
     * The wrapped object
     */
    private final Object object;

    /**
     * A flag indicating whether the wrapped object is a {@link Class} (for accessing static fields and methods), or any
     * other type of {@link Object} (for accessing instance fields and methods).
     */
    private final boolean isClass;

    private Reflect(final Class<?> type) {
        this.object = type;
        this.isClass = true;
    }

    private Reflect(final Object object) {
        this.object = object;
        this.isClass = false;
    }

    // ---------------------------------------------------------------------
    // Members
    // ---------------------------------------------------------------------

    /**
     * Conveniently render an {@link AccessibleObject} accessible
     *
     * @param <T>
     *            对象类型
     * @param accessible
     *            The object to render accessible
     * @return The argument object rendered accessible
     */
    public static <T extends AccessibleObject> T accessible(final T accessible) {
        if (accessible == null) { return null; }

        if (!accessible.isAccessible()) {
            accessible.setAccessible(true);
        }

        return accessible;
    }

    /**
     *
     * 获得公共字段
     *
     * @param clazz
     *            类名
     * @param name
     *            字段名
     * @return 字段{@link Field}
     * @throws NoSuchFieldException
     *             没有这样的字段
     */
    public static Field getDeclaredField(Class<?> clazz, final String name) throws NoSuchFieldException {
        Field field = null;
        while (clazz != Object.class) {
            try {
                field = clazz.getDeclaredField(name);
                if (field != null) {
                    break;
                }
            } catch (final Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (field == null) { throw new NoSuchFieldException("name is not found"); }
        return field;
    }

    // ---------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------

    /**
     * Wrap a class.
     * <p>
     * Use this when you want to access static fields and methods on a {@link Class} object, or as a basis for
     * constructing objects of that class using {@link #create(Object...)}
     *
     * @param clazz
     *            The class to be wrapped
     * @return A wrapped class object, to be used for further reflection.
     */
    public static Reflect on(final Class<?> clazz) {
        return new Reflect(clazz);
    }

    /**
     * Wrap an object.
     * <p>
     * Use this when you want to access instance fields and methods on any {@link Object}
     *
     * @param object
     *            The object to be wrapped
     * @return A wrapped object, to be used for further reflection.
     */
    public static Reflect on(final Object object) {
        return new Reflect(object);
    }

    // ---------------------------------------------------------------------
    // Fluent Reflection API
    // ---------------------------------------------------------------------

    /**
     * Wrap a class name.
     * <p>
     * This is the same as calling <code>on(Class.forName(name))</code>
     *
     * @param name
     *            A fully qualified class name
     * @return A wrapped class object, to be used for further reflection.
     * @throws ReflectException
     *             If any reflection exception occurred.
     * @see #on(Class)
     */
    public static Reflect on(final String name) throws ReflectException {
        return on(forName(name));
    }

    /**
     * Get a wrapper type for a primitive type, or the argument type itself, if it is not a primitive type.
     * 
     * @param type
     *            类
     * @return 返回封装类
     */
    public static Class<?> wrapper(final Class<?> type) {
        if (type == null) {
            return null;
        } else if (type.isPrimitive()) {
            if (boolean.class == type) {
                return Boolean.class;
            } else if (int.class == type) {
                return Integer.class;
            } else if (long.class == type) {
                return Long.class;
            } else if (short.class == type) {
                return Short.class;
            } else if (byte.class == type) {
                return Byte.class;
            } else if (double.class == type) {
                return Double.class;
            } else if (float.class == type) {
                return Float.class;
            } else if (char.class == type) {
                return Character.class;
            } else if (void.class == type) { return Void.class; }
        }
        return type;
    }

    /**
     * @param name
     *            类名
     * @return 类
     * @see Class#forName(String)
     * @throws ReflectException
     *             反射异常
     */
    private static Class<?> forName(final String name) throws ReflectException {
        try {
            return Class.forName(name);
        } catch (final Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * Wrap an object created from a constructor
     */
    private static Reflect on(final Constructor<?> constructor, final Object... args) throws ReflectException {
        try {
            return on(accessible(constructor).newInstance(args));
        } catch (final Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * Wrap an object returned from a method
     */
    private static Reflect on(final Method method, final Object object, final Object... args) throws ReflectException {
        try {
            accessible(method);

            if (method.getReturnType() == void.class) {
                method.invoke(object, args);
                return on(object);
            }
            return on(method.invoke(object, args));
        } catch (final Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * Get the POJO property name of an getter/setter
     */
    private static String property(final String string) {
        final int length = string.length();

        if (length == 0) {
            return "";
        } else if (length == 1) {
            return string.toLowerCase();
        } else {
            return string.substring(0, 1).toLowerCase() + string.substring(1);
        }
    }

    /**
     * Get an array of types for an array of objects
     *
     * @see Object#getClass()
     */
    private static Class<?>[] types(final Object... values) {
        if (values == null) { return new Class[0]; }

        final Class<?>[] result = new Class[values.length];

        for (int i = 0; i < values.length; i++) {
            final Object value = values[i];
            result[i] = value == null ? Object.class : value.getClass();
        }

        return result;
    }

    /**
     * Unwrap an object
     */
    private static Object unwrap(final Object object) {
        if (object instanceof Reflect) { return ((Reflect) object).get(); }

        return object;
    }

    /**
     * Create a proxy for the wrapped object allowing to typesafely invoke methods on it using a custom interface
     *
     * @param <P>
     *            代理类型
     * @param proxyType
     *            The interface type that is implemented by the proxy
     * @return A proxy for the wrapped object
     */
    public <P> P as(final Class<P> proxyType) {
        final boolean isMap = (object instanceof Map);
        final InvocationHandler handler = (proxy, method, args) -> {
            final String name = method.getName();

            // Actual method name matches always come first
            try {
                return on(object).call(name, args).get();
            }

            // [#14] Simulate POJO behaviour on wrapped map objects
            catch (final ReflectException e) {
                if (isMap) {
                    final Map<String, Object> map = (Map<String, Object>) object;
                    final int length = (args == null ? 0 : args.length);

                    if (length == 0 && name.startsWith("get")) {
                        return map.get(property(name.substring(3)));
                    } else if (length == 0 && name.startsWith("is")) {
                        return map.get(property(name.substring(2)));
                    } else if (length == 1 && name.startsWith("set")) {
                        map.put(property(name.substring(3)), args[0]);
                        return null;
                    }
                }

                throw e;
            }
        };

        return (P) Proxy.newProxyInstance(proxyType.getClassLoader(), new Class[] { proxyType }, handler);
    }

    /**
     * Call a method by its name.
     * <p>
     * This is a convenience method for calling <code>call(name, new Object[0])</code>
     *
     * @param name
     *            The method name
     * @return The wrapped method result or the same wrapped object if the method returns <code>void</code>, to be used
     *         for further reflection.
     * @throws ReflectException
     *             If any reflection exception occurred.
     * @see #call(String, Object...)
     */
    public Reflect call(final String name) throws ReflectException {
        return call(name, new Object[0]);
    }

    /**
     * Call a method by its name.
     * <p>
     * This is roughly equivalent to {@link Method#invoke(Object, Object...)}. If the wrapped object is a {@link Class},
     * then this will invoke a static method. If the wrapped object is any other {@link Object}, then this will invoke
     * an instance method.
     * <p>
     * Just like {@link Method#invoke(Object, Object...)}, this will try to wrap primitive types or unwrap primitive
     * type wrappers if applicable. If several methods are applicable, by that rule, the first one encountered is
     * called. i.e. when calling
     *
     * <pre>
     *  <code>on(...).call("method", 1, 1);</code>
     * </pre>
     *
     * The first of the following methods will be called:
     *
     * <pre>
     * <code>
     *  public void method(int param1, Integer param2);
     *  public void method(Integer param1, int param2);
     *  public void method(Number param1, Number param2);
     *  public void method(Number param1, Object param2);
     *  public void method(int param1, Object param2);
     * </code>
     * </pre>
     *
     * <p>
     * The best matching method is searched for with the following strategy:
     * <ol>
     * <li>public method with exact signature match in class hierarchy</li>
     * <li>non-public method with exact signature match on declaring class</li>
     * <li>public method with similar signature in class hierarchy</li>
     * <li>non-public method with similar signature on declaring class</li>
     * </ol>
     *
     * @param name
     *            The method name
     * @param args
     *            The method arguments
     * @return The wrapped method result or the same wrapped object if the method returns <code>void</code>, to be used
     *         for further reflection.
     * @throws ReflectException
     *             If any reflection exception occurred.
     */
    public Reflect call(final String name, final Object... args) throws ReflectException {
        final Class<?>[] types = types(args);

        // Try invoking the "canonical" method, i.e. the one with exact
        // matching argument types
        try {
            final Method method = exactMethod(name, types);
            return on(method, object, args);
        }

        // If there is no exact match, try to find a method that has a "similar"
        // signature if primitive argument types are converted to their wrappers
        catch (final NoSuchMethodException e) {
            try {
                final Method method = similarMethod(name, types);
                return on(method, object, args);
            } catch (final NoSuchMethodException e1) {
                throw new ReflectException(e1);
            }
        }
    }

    /**
     * Call a constructor.
     * <p>
     * This is a convenience method for calling <code>create(new Object[0])</code>
     *
     * @return The wrapped new object, to be used for further reflection.
     * @throws ReflectException
     *             If any reflection exception occurred.
     * @see #create(Object...)
     */
    public Reflect create() throws ReflectException {
        return create(new Object[0]);
    }

    /**
     * Call a constructor.
     * <p>
     * This is roughly equivalent to {@link Constructor#newInstance(Object...)}. If the wrapped object is a
     * {@link Class}, then this will create a new object of that class. If the wrapped object is any other
     * {@link Object}, then this will create a new object of the same type.
     * <p>
     * Just like {@link Constructor#newInstance(Object...)}, this will try to wrap primitive types or unwrap primitive
     * type wrappers if applicable. If several constructors are applicable, by that rule, the first one encountered is
     * called. i.e. when calling
     *
     * <pre>
     *  <code>
     *  on(C.class).create(1, 1);
     *  </code>
     * </pre>
     *
     * The first of the following constructors will be applied:
     *
     * <pre>
     * <code>
     *  public C(int param1, Integer param2);
     *  public C(Integer param1, int param2);
     *  public C(Number param1, Number param2);
     *  public C(Number param1, Object param2);
     *  public C(int param1, Object param2);
     * </code>
     * </pre>
     *
     * @param args
     *            The constructor arguments
     * @return The wrapped new object, to be used for further reflection.
     * @throws ReflectException
     *             If any reflection exception occurred.
     */
    public Reflect create(final Object... args) throws ReflectException {
        final Class<?>[] types = types(args);

        // Try invoking the "canonical" constructor, i.e. the one with exact
        // matching argument types
        try {
            final Constructor<?> constructor = type().getDeclaredConstructor(types);
            return on(constructor, args);
        }

        // If there is no exact match, try to find one that has a "similar"
        // signature if primitive argument types are converted to their wrappers
        catch (final NoSuchMethodException e) {
            for (final Constructor<?> constructor : type().getConstructors()) {
                if (match(constructor.getParameterTypes(), types)) { return on(constructor, args); }
            }

            throw new ReflectException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Reflect && object.equals(((Reflect) obj).get());
    }

    /**
     * Get a wrapped field.
     * <p>
     * This is roughly equivalent to {@link Field#get(Object)}. If the wrapped object is a {@link Class}, then this will
     * wrap a static member field. If the wrapped object is any other {@link Object}, then this wrap an instance member
     * field.
     *
     * @param name
     *            The field name
     * @return The wrapped field
     * @throws ReflectException
     *             If any reflection exception occurred.
     */
    public Reflect field(final String name) throws ReflectException {
        try {

            // Try getting a public field
            final Field field = type().getField(name);
            return on(field.get(object));
        } catch (final Exception e1) {

            // Try again, getting a non-public field
            try {
                return on(accessible(getDeclaredField(type(), name)).get(object));
            } catch (final Exception e2) {
                throw new ReflectException(e2);
            }
        }
    }

    // ---------------------------------------------------------------------
    // Object API
    // ---------------------------------------------------------------------

    /**
     * Get a Map containing field names and wrapped values for the fields' values.
     * <p>
     * If the wrapped object is a {@link Class}, then this will return static fields. If the wrapped object is any other
     * {@link Object}, then this will return instance fields.
     * <p>
     * These two calls are equivalent
     *
     * <pre>
     * <code>
     *  on(object).field("myField");
     *  on(object).fields().get("myField");
     * </code>
     * </pre>
     *
     * @return A map containing field names and wrapped values.
     */
    public Map<String, Reflect> fields() {
        final Map<String, Reflect> result = new LinkedHashMap<>();

        for (final Field field : type().getFields()) {
            if (!isClass ^ Modifier.isStatic(field.getModifiers())) {
                final String name = field.getName();
                result.put(name, field(name));
            }
        }
        return result;
    }

    /**
     * Get the wrapped object
     *
     * @param <T>
     *            A convenience generic parameter for automatic unsafe casting
     * @return cast Type Object
     */
    public <T> T get() {
        return (T) object;
    }

    /**
     * Get a field value.
     * <p>
     * This is roughly equivalent to {@link Field#get(Object)}. If the wrapped object is a {@link Class}, then this will
     * get a value from a static member field. If the wrapped object is any other {@link Object}, then this will get a
     * value from an instance member field.
     * <p>
     * If you want to "navigate" to a wrapped version of the field, use {@link #field(String)} instead.
     * 
     * @param <T>
     *            A convenience generic parameter for automatic unsafe casting
     * @param name
     *            The field name
     * @return The field value
     * @throws ReflectException
     *             If any reflection exception occurred.
     * @see #field(String)
     */
    public <T> T get(final String name) throws ReflectException {
        return field(name).get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return object.hashCode();
    }

    // ---------------------------------------------------------------------
    // Utility methods
    // ---------------------------------------------------------------------

    /**
     * Set a field value.
     * <p>
     * This is roughly equivalent to {@link Field#set(Object, Object)}. If the wrapped object is a {@link Class}, then
     * this will set a value to a static member field. If the wrapped object is any other {@link Object}, then this will
     * set a value to an instance member field.
     *
     * @param name
     *            The field name
     * @param value
     *            The new field value
     * @return The same wrapped object, to be used for further reflection.
     * @throws ReflectException
     *             If any reflection exception occurred.
     */
    public Reflect set(final String name, final Object value) throws ReflectException {
        try {

            // Try setting a public field
            final Field field = type().getField(name);
            field.set(object, unwrap(value));
            return this;
        } catch (final Exception e1) {

            // Try again, setting a non-public field
            try {
                accessible(type().getDeclaredField(name)).set(object, unwrap(value));
                return this;
            } catch (final Exception e2) {
                throw new ReflectException(e2);
            }
        }
    }

    @Override
    public String toString() {
        return object.toString();
    }

    /**
     * Get the type of the wrapped object.
     * 
     * @return 类类型
     * @see Object#getClass()
     */
    public Class<?> type() {
        if (isClass) { return (Class<?>) object; }
        return object.getClass();
    }

    /**
     * Searches a method with the exact same signature as desired.
     * <p>
     * If a public method is found in the class hierarchy, this method is returned. Otherwise a private method with the
     * exact same signature is returned. If no exact match could be found, we let the {@code NoSuchMethodException} pass
     * through.
     */
    private Method exactMethod(final String name, final Class<?>[] types) throws NoSuchMethodException {
        final Class<?> type = type();

        // first priority: find a public method with exact signature match in class hierarchy
        try {
            return type.getMethod(name, types);
        }

        // second priority: find a private method with exact signature match on declaring class
        catch (final NoSuchMethodException e) {
            return type.getDeclaredMethod(name, types);
        }
    }

    /**
     * Determines if a method has a "similar" signature, especially if wrapping primitive argument types would result in
     * an exactly matching signature.
     */
    private boolean isSimilarSignature(final Method possiblyMatchingMethod, final String desiredMethodName, final Class<?>[] desiredParamTypes) {
        return possiblyMatchingMethod.getName().equals(desiredMethodName) && match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
    }

    /**
     * Check whether two arrays of types match, converting primitive types to their corresponding wrappers.
     */
    private boolean match(final Class<?>[] declaredTypes, final Class<?>[] actualTypes) {
        if (declaredTypes.length == actualTypes.length) {
            for (int i = 0; i < actualTypes.length; i++) {
                if (!wrapper(declaredTypes[i]).isAssignableFrom(wrapper(actualTypes[i]))) { return false; }
            }
            return true;
        }
        return false;
    }

    /**
     * Searches a method with a similar signature as desired using
     * {@link #isSimilarSignature(java.lang.reflect.Method, String, Class[])}.
     * <p>
     * First public methods are searched in the class hierarchy, then private methods on the declaring class. If a
     * method could be found, it is returned, otherwise a {@code NoSuchMethodException} is thrown.
     */
    private Method similarMethod(final String name, final Class<?>[] types) throws NoSuchMethodException {
        final Class<?> type = type();

        // first priority: find a public method with a "similar" signature in class hierarchy
        // similar interpreted in when primitive argument types are converted to their wrappers
        for (final Method method : type.getMethods()) {
            if (isSimilarSignature(method, name, types)) { return method; }
        }

        // second priority: find a non-public method with a "similar" signature on declaring class
        for (final Method method : type.getDeclaredMethods()) {
            if (isSimilarSignature(method, name, types)) { return method; }
        }

        throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types) + " could be found on type " + type() + ".");
    }
}
