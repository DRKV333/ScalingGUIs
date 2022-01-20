package spazley.scalingguis.asm;

import org.objectweb.asm.Type;

public class Field {
    
    private final String name;

    private final String desc;

    private final Type type;

    public Field(String name, String desc) {
        this.name = name;
        this.desc = desc;
        this.type = Type.getType(desc);
    }

    public Field(String name, Type type) {
        this.name = name;
        this.type = type;
        this.desc = type.getDescriptor();
    }

    public static Field getField(java.lang.reflect.Field field) {
        return new Field(field.getName(), Type.getDescriptor(field.getType()));
    }

    public String getName() { return name; }

    public String getDescriptor() { return desc; }

    public Type getType() { return type; }

    @Override
    public String toString() {
        return name + desc;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Field))
            return false;
        Field other = (Field)obj;
        return name.equals(other.name) && desc.equals(other.desc);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ desc.hashCode();
    }
}
