package net.greenjab.fixedminecraft.hud;

import net.minecraft.client.gui.DrawContext;

/** Credit: Squeek502 */
public class HUDOverlayEvent
{
    public int x;
    public int y;
    public DrawContext context;
    public boolean isCanceled = false;

    public static class Saturation extends HUDOverlayEvent {
        public final float saturationLevel;
        public Saturation(float saturationLevel, int x, int y, DrawContext context) {
            super(x, y, context);
            this.saturationLevel = saturationLevel;
        }
    }

    private HUDOverlayEvent(int x, int y, DrawContext context) {
        this.x = x;
        this.y = y;
        this.context = context;
    }

}
