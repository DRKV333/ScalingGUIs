package spazley.scalingguis.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class HookMembers {
    
    private HookMembers() { }

    public static final String CLASS_BOUNDHOOKS_INTERNAL_NAME = "boundhookspackage/Hook";
    public static final String FIELD_BOUNDHOOKS_NEXT_NAME = "next";
    public static final String FIELD_BOUNDHOOKS_NEXTNEXT_NAME = "nextnext";

    public static final Type CLASS_OBJECT = Type.getType(Object.class);

    public static final Type CLASS_THREADLOCALBOOLEAN = Type.getType(ThreadLocalBoolean.class);

    public static final Field FIELD_ISORIGCALLFLAG = new Field("isOrigCallFlag", CLASS_THREADLOCALBOOLEAN);
    public static final String FIELD_TOPHOOK_NAME = "topHook";

    public static final String METHOD_CALLTOPHOOK_NAME = "callTopHook";
    public static final Method METHOD_ISORIGCALL = new Method("isOrigCall", Type.BOOLEAN_TYPE, new Type[0]);
    public static final Method METHOD_INIT = new Method("<init>", Type.VOID_TYPE, new Type[0]);
    public static final Method METHOD_CLINIT = new Method("<clinit>", Type.VOID_TYPE, new Type[0]);

    public static final Method METHOD_THREADLOCALBOOLEAN_SETVALUE = Method.getMethod(getMethod(ThreadLocalBoolean.class, "setValue", void.class, boolean.class));
    public static final Method METHOD_THREADLOCALBOOLEAN_CHECKANDRESET = Method.getMethod(getMethod(ThreadLocalBoolean.class, "checkAndReset", boolean.class));
    
    static {
        getConstructor(ThreadLocalBoolean.class);
    }

    private static java.lang.reflect.Method getMethod(Class<?> clazz, String name, Class<?> returnType, Class<?>... parameterTypes) {
        try {
            java.lang.reflect.Method method = clazz.getMethod(name, parameterTypes);
            if (method.getReturnType() != returnType)
                throw new IllegalArgumentException("Return type did not match.");
            return method;
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException("Could not find method.", e);
        }
    }

    private static java.lang.reflect.Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException("Could not find constructor.", e);
        }
    }
}
