package spazley.scalingguis.gui.guiconfig;

import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.config.GuiSelectString;
import cpw.mods.fml.client.config.IConfigElement;
import org.lwjgl.input.Keyboard;

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

        this.entriesList = new GuiSelectStringEntriesSG(this, this.mc, this.configElement, this.selectableValues);
    }

    @Override
    protected void keyTyped(char eventChar, int eventKey)
    {
        if (eventKey == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(parentScreen);
        }
    }
}
