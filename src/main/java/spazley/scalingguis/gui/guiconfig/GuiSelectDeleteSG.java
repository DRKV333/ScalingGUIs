package spazley.scalingguis.gui.guiconfig;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiSelectStringEntries;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.FMLLog;
import org.lwjgl.input.Keyboard;

import java.util.Map;

public class GuiSelectDeleteSG extends GuiSelectStringSG
{
    public GuiSelectDeleteSG(GuiScreen parentScreen, IConfigElement configElement, int slotIndex, Map<Object, String> selectableValues, Object currentValue, boolean enabled)
    {
        super(parentScreen, configElement, slotIndex, selectableValues, currentValue, enabled);
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    protected void keyTyped(char eventChar, int eventKey)
    {
        if (eventKey == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(((GuiConfig)parentScreen).parentScreen);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        if (button.id == 2000)
        {
            this.mc.displayGuiScreen(((GuiConfig)parentScreen).parentScreen);
        }
    }


}
