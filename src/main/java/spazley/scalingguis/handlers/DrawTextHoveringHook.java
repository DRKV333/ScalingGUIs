package spazley.scalingguis.handlers;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public abstract class DrawTextHoveringHook {
    
    public abstract void hook(GuiScreen self, List<String> textLines, int x, int y, FontRenderer fontRenderer, DrawTextHoveringHook orig);
}
