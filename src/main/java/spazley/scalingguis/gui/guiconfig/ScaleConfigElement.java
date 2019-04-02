package spazley.scalingguis.gui.guiconfig;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries;

public class ScaleConfigElement extends ConfigElement
{

    protected Object defaultValue;
    protected Object minValue;
    protected Object maxValue;


/*
    protected Class<? extends GuiEditArrayEntries.IArrayEntry> arrayEntryClass;
*/
    protected Class<? extends GuiConfigEntries.IConfigEntry> configEntryClass;


    public ScaleConfigElement(Property prop)
    {
        super(prop);
    }

/*
    public ScaleConfigElement setCustomEditListEntryClass(Class<? extends GuiEditArrayEntries.IArrayEntry> clazz)
    {
        this.arrayEntryClass = clazz;
        return this;
    }

    @Override
    public Class<? extends GuiEditArrayEntries.IArrayEntry> getArrayEntryClass()
    {
        return arrayEntryClass;
    }
*/

    public ScaleConfigElement setCustomListEntryClass(Class<? extends GuiConfigEntries.IConfigEntry> clazz)
    {
        this.configEntryClass = clazz;
        return this;
    }

    @Override
    public Class<? extends GuiConfigEntries.IConfigEntry> getConfigEntryClass()
    {
        return configEntryClass;
    }

    @Override
    public void set(Object value)
    {
        super.set(value);
    }




}
