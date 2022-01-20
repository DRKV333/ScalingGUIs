package spazley.scalingguis.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class BetterMethodVisitor extends MethodVisitor {

    public BetterMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }

    public final void visitMethodInsn(int opcode, Type owner, Method method) {
        visitMethodInsn(opcode, owner, method, false);
    }

    public final void visitMethodInsn(int opcode, Type owner, Method method, boolean itf) {
        mv.visitMethodInsn(opcode, owner.getInternalName(), method.getName(), method.getDescriptor(), itf);
    }

    public final void visitFieldInsn(int opcode, Type owner, Field field) {
        mv.visitFieldInsn(opcode, owner.getInternalName(), field.getName(), field.getDescriptor());
    }

    public final AnnotationVisitor visitAnnotation(Type type, boolean visible) {
        return mv.visitAnnotation(type.getDescriptor(), visible);
    }

    public final void visitLocalVariable(String name, Type type, String signature, Label start, Label end, int index) {
        mv.visitLocalVariable(name, type.getDescriptor(), signature, start, end, index);
    }

    public final void visitTypeInsn(int opcode, Type type) {
        mv.visitTypeInsn(opcode, type.getInternalName());
    }
}
