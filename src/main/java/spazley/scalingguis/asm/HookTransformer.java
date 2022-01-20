package spazley.scalingguis.asm;

import net.minecraft.launchwrapper.IClassTransformer;

public class HookTransformer implements IClassTransformer {

    private HookProcessor processor = new HookProcessor(); 

    public HookTransformer() {
        FMLPluginSG.manager.configureProcessor(processor);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        return processor.process(transformedName, basicClass);
    }
    
}
