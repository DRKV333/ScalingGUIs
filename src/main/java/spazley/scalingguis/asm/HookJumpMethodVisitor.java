package spazley.scalingguis.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class HookJumpMethodVisitor extends BetterMethodVisitor implements Opcodes {

    private int maxStack;
    private int maxLocals;

    public HookJumpMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }
    
    @Override
    public final void visitCode() {
        maxStack = 0;
        maxLocals = 0;
        hookVisitCode();
    }
    
    public void hookVisitCode() {
        mv.visitCode();
    }

    public void hookArgFromThis() {
        mv.visitVarInsn(ALOAD, 0);
        maxStack += 1;
    }

    public void hookArgFromStatic(Type owner, Field field) {
        visitFieldInsn(GETSTATIC, owner, field);
        maxStack += field.getType().getSize();
    }

    public void hookArgFromField(Type owner, Field field) {
        hookArgFromThis();
        visitFieldInsn(GETFIELD, owner, field);
        maxStack += field.getType().getSize();
    }

    public void hookArgFromNull() {
        mv.visitInsn(ACONST_NULL);
        maxStack += 1;
    }

    public void hookArgsFromMethodArgs(Method method, boolean isInstance, boolean skipLast) {
        int argIndex = isInstance ? 1 : 0;

        Type[] args = method.getArgumentTypes();

        for (int i = 0; i < args.length; i++) {
            if (!(skipLast && i == (args.length - 1)))
                mv.visitVarInsn(args[i].getOpcode(ILOAD), argIndex);
            argIndex += args[i].getSize();
        }

        maxStack += argIndex;
        if (isInstance)
            maxStack -= 1;

        if (maxLocals < argIndex)
            maxLocals = argIndex;
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        if (maxStack < this.maxStack)
            maxStack = this.maxStack;
        if (maxLocals < this.maxLocals)
            maxLocals = this.maxLocals;
        mv.visitMaxs(maxStack, maxLocals);
    }
}
