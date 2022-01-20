package spazley.scalingguis.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions("spazley.scalingguis.asm")
public class FMLPluginSG implements IFMLLoadingPlugin {

    public static class Hooks
    {
        private Hooks() {}

        public static HookDescription drawHoveringText = new HookDescription(
            "net/minecraft/client/gui/GuiScreen", 
            "drawHoveringText", "(Ljava/util/List;IILnet/minecraft/client/gui/FontRenderer;)V", true,
            "spazley/scalingguis/handlers/DrawTextHoveringHook");
    }

    public static final HookManager manager = new HookManager();

    @Override
    public String[] getASMTransformerClass() {
        manager.addHookFromStatics(Hooks.class);
        return new String[] { HookTransformer.class.getCanonicalName() };
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> arg0) {
    }
}
