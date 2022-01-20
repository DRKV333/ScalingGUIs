package spazley.scalingguis.asm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.objectweb.asm.Opcodes;

public class HookManager {

    private final HashMap<HookDescription, Class<?>> hooksWithBoundListener = new HashMap<>();

    private BoundHookLoader boundHookLoader = null;

    public void addHook(HookDescription hook) {
        hooksWithBoundListener.putIfAbsent(hook, null);
    }

    public void addHookFromStatics(Class<?> staticsClass) {
        for (java.lang.reflect.Field field : staticsClass.getDeclaredFields()) {
            int mod = field.getModifiers();
            if (field.getType() == HookDescription.class && Modifier.isPublic(mod) && Modifier.isStatic(mod)) {
                try {
                    addHook((HookDescription)field.get(null));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalArgumentException("Could not load all fields from specified class.", e);
                }
            }
        }
    }
    
    public void configureProcessor(HookProcessor processor) {
        for (HookDescription hook : hooksWithBoundListener.keySet()) {
            processor.addTransformer(hook.getHookType().getClassName(), x -> new HookClassVisitor(Opcodes.ASM5, x, hook));
            processor.addTransformer(hook.getTargetType().getClassName(), x -> new HookTargetVisitor(Opcodes.ASM5, x, hook));
        }
    }

    public void installHookListener(HookDescription desc, Object listener) {
        if (boundHookLoader == null)
            boundHookLoader = new BoundHookLoader();

        Class<?> bound = hooksWithBoundListener.computeIfAbsent(desc, boundHookLoader::createBoundHook);

        java.lang.reflect.Field topHook = null;
        try {
            topHook = Class.forName(desc.getHookType().getClassName()).getField(HookMembers.FIELD_TOPHOOK_NAME);
        } catch (NoSuchFieldException | SecurityException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        Object currentTop = null;
        try {
            currentTop = topHook.get(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Object boundInst = null;
        try {
            boundInst = bound.getConstructors()[0].newInstance(listener, currentTop);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | SecurityException e) {
            e.printStackTrace();
        }

        try {
            topHook.set(null, boundInst);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
