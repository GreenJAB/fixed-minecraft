package net.greenjab.fixedminecraft.hud;

import net.minecraft.client.gui.DrawContext;

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
        //public static Event<EventHandler<Saturation>> EVENT = EventHandler.createArrayBacked();
    }

    private HUDOverlayEvent(int x, int y, DrawContext context) {
        this.x = x;
        this.y = y;
        this.context = context;
    }

}
