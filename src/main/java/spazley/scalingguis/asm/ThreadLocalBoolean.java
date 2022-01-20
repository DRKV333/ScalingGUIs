package spazley.scalingguis.asm;

public class ThreadLocalBoolean extends ThreadLocal<Boolean> {
    @Override
    protected Boolean initialValue() {
        return false;
    }

    public boolean getValue() {
        return get();
    }

    public void setValue(boolean value) {
        set(value);
    }

    public boolean checkAndReset() {
        boolean isSet = get();
        if (isSet)
            set(false);
        return isSet;
    }
}