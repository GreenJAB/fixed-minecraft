package net.greenjab.fixedminecraft.hud;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.util.IntPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

import java.util.Random;
import java.util.Vector;

/** Credit: Squeek502 */
public class HUDOverlayHandler {

    private static final int FOOD_BAR_HEIGHT = 39;

    private static final Vector<IntPoint> foodBarOffsets = new Vector<>();

    private static final Random random = new Random();

    public static void onRender(GuiGraphicsExtractor context) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        assert player != null;
        FoodData stats = player.getFoodData();

        int top = mc.getWindow().getGuiScaledHeight() - FOOD_BAR_HEIGHT;
        int right = mc.getWindow().getGuiScaledWidth() / 2 + 91;

        generateBarOffsets(top, right, mc.gui.getGuiTicks(), player);

        HUDOverlayEvent.Saturation saturationRenderEvent = new HUDOverlayEvent.Saturation(stats.getSaturationLevel(), right, top, context);

        if (!saturationRenderEvent.isCanceled) {
            drawSaturationOverlay(saturationRenderEvent, mc.gui.getGuiTicks());
        }

    }

    private static void drawSaturationOverlay(GuiGraphicsExtractor context, Float saturationLevel, int right, int top, int ticks) {
        float modifiedSaturation = Mth.clamp(saturationLevel, 0.0f, 20.0f);
        int endSaturationBar = Mth.ceil(modifiedSaturation / 2);
        int iconSize = 9;
        for (int i = 0; i < endSaturationBar; i++) {
            IntPoint offset = foodBarOffsets.get(i);
            if (offset == null) continue;
            int x = right + offset.x;
            int y = top + offset.y;

            float u = 0f;
            float effectiveSaturationOfBar = (modifiedSaturation / 2.0F) - i;
            if (effectiveSaturationOfBar > .75) u = 3f * iconSize;
            else if (effectiveSaturationOfBar > .50) u = 2f * iconSize;
            else if (effectiveSaturationOfBar > .25) u = 1f * iconSize;

            context.blit(RenderPipelines.GUI_TEXTURED, FixedMinecraft.id("textures/gui/sprites/stamina.png"), x, y, u, 0, iconSize, iconSize, 36, 18,  ARGB.white(1F));
            if ((saturationLevel <=6 && saturationLevel>0) || Minecraft.getInstance().player.hasEffect(MobEffects.HUNGER))
                context.blit(RenderPipelines.GUI_TEXTURED, FixedMinecraft.id("textures/gui/sprites/stamina.png"), x, y, u, iconSize, iconSize, iconSize, 36, 18,  ARGB.white((float) (Math.sin(ticks/6f) + 1) / 2f));
        }
        if (saturationLevel <=0) {
            for (int i = 0; i < 10; i++) {
                IntPoint offset = foodBarOffsets.get(i);
                if (offset == null) continue;
                context.blit(RenderPipelines.GUI_TEXTURED, FixedMinecraft.id("textures/gui/sprites/stamina.png"),
                        right + offset.x, top + offset.y,  3f * iconSize, iconSize, iconSize, iconSize, 36, 18,  ARGB.white((float) (Math.sin(ticks/3f) + 1) / 2f));
            }
        }
    }

    private static void drawSaturationOverlay(HUDOverlayEvent.Saturation event, int ticks) {
        drawSaturationOverlay(event.context, event.saturationLevel, event.x, event.y, ticks);
    }

    private static void generateBarOffsets(int top, int right, int ticks, Player player) {
        int preferFoodBars = 10;
        var shouldAnimatedFood = false;
        FoodData hungerManager = player.getFoodData();

        // in vanilla saturation level is zero will show hunger animation
        float saturationLevel = hungerManager.getSaturationLevel();
        int foodLevel = hungerManager.getFoodLevel();
        shouldAnimatedFood = saturationLevel <= 0.0F && ticks % (foodLevel * 3 + 1) == 0;

        // hard code in `InGameHUD`
        random.setSeed((ticks * 312871L));

        if (foodBarOffsets.size() != preferFoodBars)
            foodBarOffsets.setSize(preferFoodBars);

        // right alignment, single row
        for (int i = 0; i < preferFoodBars; i++) {
            int x = right - i * 8 - 9;
            int y = top;

            // apply the animated offset
            if (shouldAnimatedFood)
                y += random.nextInt(3) - 1;

            // reuse the point object to reduce memory usage
            var point = foodBarOffsets.get(i);
            if (point == null) {
                point = new IntPoint();
                foodBarOffsets.set(i, point);
            }

            point.x = x - right;
            point.y = y - top;
        }
    }
}
