package spazley.scalingguis.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import net.minecraft.launchwrapper.Launch;

public class BoundHookLoader implements Opcodes {
    
    private class DummyClassCloader extends ClassLoader {
        
        public DummyClassCloader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    private DummyClassCloader loader = new DummyClassCloader(Launch.classLoader);

    private int hookCount = 0;

    public Class<?> createBoundHook(HookDescription desc) {
        Type bound = Type.getObjectType(hookName());

        ClassWriter cw = new ClassWriter(0);
        BetterClassVisitor cv = new BetterClassVisitor(ASM5, cw);

        cv.visit(V1_8, ACC_PUBLIC, bound.getInternalName(), null, desc.getHookType().getInternalName(), null);

        Field next = new Field(HookMembers.FIELD_BOUNDHOOKS_NEXT_NAME, desc.getHookType().getDescriptor());
        cv.visitField(ACC_PRIVATE | ACC_FINAL, next).visitEnd();
        Field nextnext = new Field(HookMembers.FIELD_BOUNDHOOKS_NEXTNEXT_NAME, desc.getHookType().getDescriptor());
        cv.visitField(ACC_PRIVATE | ACC_FINAL, nextnext).visitEnd();

        //TODO
        Method hookMethod = new Method("hook", desc.getHookMethodDesc());

        HookJumpMethodVisitor mv = new HookJumpMethodVisitor(ASM5, cv.visitMethod(ACC_PUBLIC, hookMethod));
        mv.visitCode();
        mv.hookArgFromField(bound, next);
        mv.hookArgsFromMethodArgs(hookMethod, true, true);
        mv.hookArgFromField(bound, nextnext);
        mv.visitMethodInsn(INVOKEVIRTUAL, desc.getHookType(), hookMethod);
        mv.visitInsn(hookMethod.getReturnType().getOpcode(IRETURN));
        mv.visitMaxs(hookMethod.getReturnType().getSize(), 0);
        mv.visitEnd();

        BetterMethodVisitor mvc = cv.visitMethod(ACC_PUBLIC, new Method(HookMembers.METHOD_INIT.getName(), Type.VOID_TYPE, new Type[] { desc.getHookType(), desc.getHookType() }));
        mvc.visitCode();
        mvc.visitVarInsn(ALOAD, 0);
        mvc.visitVarInsn(ALOAD, 1);
        mvc.visitFieldInsn(PUTFIELD, bound, next);
        mvc.visitVarInsn(ALOAD, 0);
        mvc.visitVarInsn(ALOAD, 2);
        mvc.visitFieldInsn(PUTFIELD, bound, nextnext);
        mvc.visitVarInsn(ALOAD, 0);
        mvc.visitMethodInsn(INVOKESPECIAL, desc.getHookType(), HookMembers.METHOD_INIT);
        mvc.visitInsn(RETURN);
        mvc.visitMaxs(2, 3);
        mvc.visitEnd();

        cv.visitEnd();

        return loader.defineClass(bound.getClassName(), cw.toByteArray());
    }

    private String hookName() {
        return HookMembers.CLASS_BOUNDHOOKS_INTERNAL_NAME + hookCount++;
    }
}
