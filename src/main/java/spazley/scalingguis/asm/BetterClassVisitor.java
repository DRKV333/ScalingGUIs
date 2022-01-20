package spazley.scalingguis.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class BetterClassVisitor extends ClassVisitor {

    public BetterClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    public final AnnotationVisitor visitAnnotation(Type type, boolean visible) {
        return cv.visitAnnotation(type.getDescriptor(), visible);
    }

    public final FieldVisitor visitField(int access, Field field) {
        return visitField(access, field, null, null);
    }

    public final FieldVisitor visitField(int access, Field field, String signature) {
        return visitField(access, field, signature, null);
    }

    public final FieldVisitor visitField(int access, Field field, String signature, Object value) {
        return cv.visitField(access, field.getName(), field.getDescriptor(), signature, value);
    }

    public final BetterMethodVisitor visitMethod(int access, Method method) {
        return visitMethod(access, method, null, null);
    }

    public final BetterMethodVisitor visitMethod(int access, Method method, String signature) {
        return visitMethod(access, method, signature, null);
    }

    public final BetterMethodVisitor visitMethod(int access, Method method, String signature, String[] exceptions) {
        return visitBetterMethod(access, method.getName(), method.getDescriptor(), signature, exceptions);
    }

    public final BetterMethodVisitor visitBetterMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new BetterMethodVisitor(api, cv.visitMethod(access, name, desc, signature, exceptions));
    }
}
