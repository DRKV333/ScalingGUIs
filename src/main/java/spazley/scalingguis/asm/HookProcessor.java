package spazley.scalingguis.asm;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.function.UnaryOperator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

public class HookProcessor {
    
    private final HashMap<String, UnaryOperator<ClassVisitor>> transformers = new HashMap<>();
    private boolean trace = false;

    public void addTransformer(String targetClass, UnaryOperator<ClassVisitor> transformer) {
        transformers.compute(targetClass, (k, v) -> v == null ? transformer : x -> transformer.apply(v.apply(x)));
    }

    public void setTrace(boolean value) { trace = value; }

    public byte[] process(String className, byte[] orig) {
        UnaryOperator<ClassVisitor> transfromer = transformers.getOrDefault(className, null);
        if (transfromer == null)
            return orig;

        ClassReader reader = new ClassReader(orig);
        ClassWriter writer = new ClassWriter(reader, 0);

        ClassVisitor transformed = writer;
        if (trace)
            transformed = new CheckClassAdapter(new TraceClassVisitor(writer, new PrintWriter(System.out)));
        transformed = transfromer.apply(transformed);

        reader.accept(transformed, 0);
        
        return writer.toByteArray();
    }
}
