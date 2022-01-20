package spazley.scalingguis.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HookTargetVisitor extends BetterClassVisitor implements Opcodes {

    private HookDescription hookDesc;
    private String targetMethodName;
    private String targetMethodDesc;
    private int majorVersion;

    public HookTargetVisitor(int api, ClassVisitor cv, HookDescription hookDesc) {
        super(api, cv);

        this.hookDesc = hookDesc;
        targetMethodName = hookDesc.getTargetMethod().getName();
        targetMethodDesc = hookDesc.getTargetMethod().getDescriptor();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (!name.equals(hookDesc.getTargetType().getInternalName()))
            throw new IllegalStateException("Visited class does not match target class.");

        majorVersion = version &  0xFFFF;

        cv.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals(targetMethodName) && desc.equals(targetMethodDesc))
            return new TargetMethodVisitor(api, cv.visitMethod(access, name, desc, signature, exceptions));
        else
            return cv.visitMethod(access, name, desc, signature, exceptions);
    }

    private class TargetMethodVisitor extends HookJumpMethodVisitor {

        public TargetMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void hookVisitCode() {
            mv.visitCode();

            Label label = new Label();

            visitMethodInsn(INVOKESTATIC, hookDesc.getHookType(), HookMembers.METHOD_ISORIGCALL);
            visitJumpInsn(IFNE, label);

            if (hookDesc.getIsTargetInstance())
                hookArgFromThis();
            hookArgsFromMethodArgs(hookDesc.getTargetMethod(), hookDesc.getIsTargetInstance(), false);

            visitMethodInsn(INVOKESTATIC, hookDesc.getHookType(), hookDesc.getCallTopHookMethod());

            visitInsn(hookDesc.getTargetMethod().getReturnType().getOpcode(IRETURN));

            visitLabel(label);

            if (majorVersion >= V1_6) {
                visitFrame(F_SAME, 0, null, 0, null);
            }
        }
    }
}
