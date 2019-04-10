package spazley.scalingguis.gui.guiconfig;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSelectString;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.Map;

public class GuiSelectStringSG extends GuiSelectString {

    public GuiSelectStringSG(GuiScreen parentScreen, IConfigElement configElement, int slotIndex, Map<Object, String> selectableValues, Object currentValue, boolean enabled)
    {
        super(parentScreen, configElement, slotIndex, selectableValues, currentValue, enabled);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.entryList = new GuiSelectStringEntriesSG(this, this.mc, this.configElement, this.selectableValues);
    }
}
