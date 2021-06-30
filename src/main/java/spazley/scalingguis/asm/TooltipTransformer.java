package spazley.scalingguis.asm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import net.minecraft.launchwrapper.IClassTransformer;

public class TooltipTransformer implements IClassTransformer {

    private static List<HookEventListener> listeners = new ArrayList<>();

    public static void addListener(HookEventListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(HookEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name == null || transformedName == null || basicClass == null || basicClass.length == 0) {
            return basicClass;
        }

        if (transformedName.equals("net.minecraft.client.gui.GuiScreen")) {
            FMLRelaunchLog.info("ScalingGUIs: Found GuiScreen class for tooltip patch", name, transformedName);
            return transformGuiScreen(basicClass);
        }

        return basicClass;
    }

    private byte[] transformGuiScreen(byte[] orig) {

        ClassReader reader = new ClassReader(orig);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);

        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM5, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (name.equals("drawHoveringText")) {
                    FMLRelaunchLog.info("ScalingGUIs: Found drawHoveringText");
                    return new MethodVisitor(Opcodes.ASM5, cv.visitMethod(access, name, desc, signature, exceptions)) {
                        @Override
                        public void visitCode() {
                            mv.visitCode();
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(TooltipTransformer.class), "firePre", "()V", false);
                        }

                        @Override
                        public void visitInsn(int opcode) {
                            if (opcode == Opcodes.RETURN)
                                mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(TooltipTransformer.class), "firePost", "()V", false);
                            mv.visitInsn(opcode);
                        }
                    };
                }
                return cv.visitMethod(access, name, desc, signature, exceptions);
            }
        };

        reader.accept(visitor, ClassReader.SKIP_FRAMES);

        return writer.toByteArray();
    }

    public static void firePre() {
        for (HookEventListener hookEventListener : listeners) {
            hookEventListener.pre();
        }
    }
    
    public static void firePost() {
        for (HookEventListener hookEventListener : listeners) {
            hookEventListener.post();
        }
    }
}
