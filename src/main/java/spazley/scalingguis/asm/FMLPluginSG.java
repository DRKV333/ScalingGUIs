package spazley.scalingguis.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions("spazley.scalingguis.asm")
public class FMLPluginSG implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { TooltipTransformer.class.getCanonicalName() };
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
