package spazley.scalingguis.gui.guiconfig;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;

public class GuiClassStringEntry extends GuiConfigEntries.StringEntry {

    public GuiClassStringEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
    {
        super(owningScreen, owningEntryList, prop);
    }

    public void setTextField(String text)
    {
        this.textFieldValue.setText(text);
    }

    @Override
    public boolean saveConfigElement()
    {

        return super.saveConfigElement();
    }
}
