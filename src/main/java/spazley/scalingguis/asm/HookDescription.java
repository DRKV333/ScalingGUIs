package spazley.scalingguis.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class HookDescription {

    private final Type targetType;
    private final Method targetMethod;
    private final boolean isTargetInstance;
    private final Type hookType;
    private final String hookMethodDesc;
    private final Field topHookField;
    private final Method callTopHookMethod;

    public HookDescription(String targetTypeIntName, String targetMethodName, String targetMethodDesc,
                            boolean isTargetInstance, String hookTypeIntName) {
        targetType = Type.getObjectType(targetTypeIntName);
        targetMethod = new Method(targetMethodName, targetMethodDesc);
        this.isTargetInstance = isTargetInstance;
        hookType = Type.getObjectType(hookTypeIntName);
        hookMethodDesc = makeMethodDesc(true);
        topHookField = new Field(HookMembers.FIELD_TOPHOOK_NAME, hookType);
        callTopHookMethod = new Method(HookMembers.METHOD_CALLTOPHOOK_NAME, makeMethodDesc(false));
    }

    public Type getTargetType() { return targetType; }

    public Method getTargetMethod() { return targetMethod; }

    public boolean getIsTargetInstance() { return isTargetInstance; }

    public Type getHookType() { return hookType; }

    public String getHookMethodDesc() { return hookMethodDesc; }

    public Field getTopHookField() { return topHookField; }

    public Method getCallTopHookMethod() { return callTopHookMethod; }

    private String makeMethodDesc(boolean includeOrig) {
        Type[] targetArgs = targetMethod.getArgumentTypes();
        
        int argsLength = targetArgs.length;
        if (isTargetInstance)
            argsLength++;
        if (includeOrig)
            argsLength++;

        Type[] hookArgs = new Type[argsLength];
        int startIdx;

        if (isTargetInstance) {
            hookArgs[0] = targetType;
            startIdx = 1;
        } else {
            startIdx = 0;
        }

        for (int i = 0; i < targetArgs.length; i++) {
            hookArgs[i + startIdx] = targetArgs[i];
        }

        if (includeOrig)
            hookArgs[argsLength - 1] = hookType;

        return Type.getMethodDescriptor(targetMethod.getReturnType(), hookArgs);
    }
}
