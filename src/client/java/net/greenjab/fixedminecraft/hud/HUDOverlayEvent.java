package net.greenjab.fixedminecraft.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Credit: Squeek502 */
public class HUDOverlayEvent
{
    public int x;
    public int y;
    public GuiGraphicsExtractor context;
    public boolean isCanceled = false;

    public static class Saturation extends HUDOverlayEvent {
        public final float saturationLevel;
        public Saturation(float saturationLevel, int x, int y, GuiGraphicsExtractor context) {
            super(x, y, context);
            this.saturationLevel = saturationLevel;
        }
    }

    private HUDOverlayEvent(int x, int y, GuiGraphicsExtractor context) {
        this.x = x;
        this.y = y;
        this.context = context;
    }

}
