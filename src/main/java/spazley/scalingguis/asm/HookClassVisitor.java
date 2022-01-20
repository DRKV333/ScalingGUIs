package spazley.scalingguis.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

public class HookClassVisitor extends BetterClassVisitor implements Opcodes {

    private HookDescription hookDesc;

    private Method hookMethod;
    private boolean haveClinit;
    private boolean haveInit;

    public HookClassVisitor(int api, ClassVisitor cv, HookDescription hookDesc) {
        super(api, cv);
        this.hookDesc = hookDesc;
    }

    private int makeNotAbstract(int access) {
        if ((access & ACC_ABSTRACT) == 0) {
            throw new IllegalStateException("The hook class and the hook method should be abstract.");
        } else {
            return access ^ ACC_ABSTRACT;
        }
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (!name.equals(hookDesc.getHookType().getInternalName()))
            throw new IllegalStateException("Visited class does not match hook class.");
        
        hookMethod = null;
        haveClinit = false;
        haveInit = false;

        cv.visit(version, makeNotAbstract(access), name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (desc.equals(hookDesc.getHookMethodDesc())) {
            if (hookMethod != null)
                throw new IllegalStateException("Hook class should only have one hook method.");
            hookMethod = new Method(name, desc);
            visitHookMethod(makeNotAbstract(access), signature, exceptions);
            return null;
        } else if (name.equals(HookMembers.METHOD_CLINIT.getName()))  {
            haveClinit = true;
            return new ClinitVisitor(api, cv.visitMethod(access, name, desc, signature, exceptions));
        } else {
            if (name.equals(HookMembers.METHOD_INIT.getName()) && desc.equals(HookMembers.METHOD_INIT.getDescriptor()))
                haveInit = true;
            return cv.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    private void visitHookMethod(int access, String signature, String[] exceptions) {
        HookJumpMethodVisitor mv = new HookJumpMethodVisitor(api, cv.visitMethod(access, hookMethod.getName(), hookMethod.getDescriptor(), signature, exceptions));
        mv.visitCode();

        mv.visitFieldInsn(GETSTATIC, hookDesc.getHookType(), HookMembers.FIELD_ISORIGCALLFLAG);
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn(INVOKEVIRTUAL, HookMembers.CLASS_THREADLOCALBOOLEAN, HookMembers.METHOD_THREADLOCALBOOLEAN_SETVALUE);

        mv.hookArgsFromMethodArgs(hookMethod, true, true);

        int invokeOp = hookDesc.getIsTargetInstance() ? INVOKEVIRTUAL : INVOKESTATIC;
        mv.visitMethodInsn(invokeOp, hookDesc.getTargetType(), hookDesc.getTargetMethod());

        mv.visitInsn(hookMethod.getReturnType().getOpcode(IRETURN));

        mv.visitMaxs(2 + hookMethod.getReturnType().getSize(), 0);

        mv.visitEnd();
    }

    @Override
    public void visitEnd() {
        if (hookMethod == null)
            throw new IllegalStateException("No hook method was found.");

        if (!haveInit)
            throw new IllegalStateException("Hook class should have a parameterless constructor.");

        visitFieldIsOrigCallFlag();
        visitFieldTopHook();
        visitMethodCallTopHook();
        visitMethodIsOrigCall();

        if (!haveClinit)
            visitMethodClinit();

        cv.visitEnd();
    }

    private void visitFieldIsOrigCallFlag() {
        FieldVisitor fv = visitField(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, HookMembers.FIELD_ISORIGCALLFLAG);
        fv.visitEnd();
    }

    private void visitFieldTopHook() {
        FieldVisitor fv = visitField(ACC_PUBLIC | ACC_STATIC | ACC_VOLATILE, hookDesc.getTopHookField());
        fv.visitEnd();
    }

    private void visitMethodCallTopHook() {
        HookJumpMethodVisitor mv = new HookJumpMethodVisitor(api, visitMethod(ACC_PUBLIC | ACC_STATIC, hookDesc.getCallTopHookMethod()));
        mv.visitCode();

        mv.hookArgFromStatic(hookDesc.getHookType(), hookDesc.getTopHookField());
        mv.hookArgsFromMethodArgs(hookMethod, false, true);
        mv.hookArgFromNull();

        mv.visitMethodInsn(INVOKEVIRTUAL, hookDesc.getHookType(), hookMethod);
        mv.visitInsn(hookMethod.getReturnType().getOpcode(IRETURN));

        mv.visitMaxs(hookMethod.getReturnType().getSize(), 0);

        mv.visitEnd();
    }

    private void visitMethodIsOrigCall() {
        BetterMethodVisitor mv = visitMethod(ACC_PUBLIC | ACC_STATIC, HookMembers.METHOD_ISORIGCALL);
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, hookDesc.getHookType(), HookMembers.FIELD_ISORIGCALLFLAG);
        mv.visitMethodInsn(INVOKEVIRTUAL, HookMembers.CLASS_THREADLOCALBOOLEAN, HookMembers.METHOD_THREADLOCALBOOLEAN_CHECKANDRESET);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(1, 0);
        mv.visitEnd();
    }

    private class ClinitVisitor extends BetterMethodVisitor {

        public ClinitVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }
        
        @Override
        public void visitCode() {
            mv.visitCode();

            visitTypeInsn(NEW, HookMembers.CLASS_THREADLOCALBOOLEAN);
            mv.visitInsn(DUP);
            visitMethodInsn(INVOKESPECIAL, HookMembers.CLASS_THREADLOCALBOOLEAN, HookMembers.METHOD_INIT);
            visitFieldInsn(PUTSTATIC, hookDesc.getHookType(), HookMembers.FIELD_ISORIGCALLFLAG);

            visitTypeInsn(NEW, hookDesc.getHookType());
            mv.visitInsn(DUP);
            visitMethodInsn(INVOKESPECIAL, hookDesc.getHookType(), HookMembers.METHOD_INIT);
            visitFieldInsn(PUTSTATIC, hookDesc.getHookType(), hookDesc.getTopHookField());
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            if (maxStack < 2)
                maxStack = 2;
            mv.visitMaxs(maxStack, maxLocals);
        }
    }

    private void visitMethodClinit() {
        MethodVisitor mv = new ClinitVisitor(api, visitMethod(ACC_STATIC, HookMembers.METHOD_CLINIT));
        mv.visitCode();
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 0);
        mv.visitEnd();
    }
}
