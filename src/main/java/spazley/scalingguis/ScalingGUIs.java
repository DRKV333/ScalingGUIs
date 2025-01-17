package spazley.scalingguis;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import spazley.scalingguis.handlers.ClientEventHandler;
import spazley.scalingguis.handlers.ConfigHandler;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = ScalingGUIs.MODID, name = ScalingGUIs.NAME, version = ScalingGUIs.VERSION, guiFactory = "spazley.scalingguis.gui.guiconfig.ConfigGuiFactory")
public class ScalingGUIs
{
    public static final String MODID = "scalingguis";
    public static final String NAME = "ScalingGUIs";
    public static final String VERSION = "@VERSION@";

    public static Logger logger;


    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        //if (FMLClientHandler.instance().hasOptifine()) {
            //logger.error("Optifine present. Disabling ScalingGUIs.");
        //} else {

            new ClientEventHandler();
            //new ConfigHandler();

            File configFile = new File("config/ScalingGUIs/ScalingGUIs.cfg");
            ConfigHandler.config = new Configuration(configFile, true);
            //ConfigHandler.initConfigs();
        //}
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        logger.info("ScalingGUIs FMLPostInitializationEvent");
        ConfigHandler.initConfigs();
    }




}

