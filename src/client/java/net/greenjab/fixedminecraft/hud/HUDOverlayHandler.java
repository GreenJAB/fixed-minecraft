package net.greenjab.fixedminecraft.hud;

import net.greenjab.fixedminecraft.util.ExhaustionHelper;
import net.greenjab.fixedminecraft.util.IntPoint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.Vector;

public class HUDOverlayHandler
{
    private static int foodIconsOffset = 0;

    private static final int FOOD_BAR_HEIGHT = 39;

    private static Vector<IntPoint> foodBarOffsets = new Vector<>();

    private static Random random = new Random();


    public static void onRender(DrawContext context) {
        foodIconsOffset = FOOD_BAR_HEIGHT;
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        HungerManager stats = player.getHungerManager();

        int top = mc.getWindow().getScaledHeight() - foodIconsOffset;
        int left = mc.getWindow().getScaledWidth() / 2 - 91;
        int right = mc.getWindow().getScaledWidth() / 2 + 91;

        generateBarOffsets(top, left, right, mc.inGameHud.getTicks(), player);

        HUDOverlayEvent.Saturation saturationRenderEvent = new HUDOverlayEvent.Saturation(stats.getSaturationLevel(), right, top, context);

        if (!saturationRenderEvent.isCanceled) {
            drawSaturationOverlay(saturationRenderEvent, mc);
        }

    }

    private static void drawSaturationOverlay(DrawContext context, Float saturationLevel, MinecraftClient mc, int right, int top) {

        float modifiedSaturation = Math.max(0.0f, Math.min(saturationLevel, 20.0f));
        int endSaturationBar = (int) Math.ceil(modifiedSaturation / 2);
        int iconSize = 9;

        for (int i = 0; i < endSaturationBar; i++) {
            // gets the offset that needs to be render of icon
            IntPoint offset = foodBarOffsets.get(i);
            if (offset == null)
                continue;

            int x = right + offset.x;
            int y = top + offset.y;

            float v = 0f;
            float u = 0f;

            float effectiveSaturationOfBar = (modifiedSaturation / 2.0F) - i;

            if (effectiveSaturationOfBar > .75)
                u = 3f * iconSize;
            else if (effectiveSaturationOfBar > .50)
                u = 2f * iconSize;
            else if (effectiveSaturationOfBar > .25)
                u = 1f * iconSize;
            context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("fixedminecraft", "textures/icons.png"), x, y, u, v, iconSize, iconSize, 256, 256,  ColorHelper.getWhite(1F));
        }

    }

    private static void drawExhaustionOverlay(DrawContext context, float exhaustion, MinecraftClient mc, int right, int top) {
        float maxExhaustion = 1.0f;
        // clamp between 0 and 1
        float ratio = Math.min(Math.max(exhaustion / maxExhaustion, 0), 1);
        int width = (int) (ratio * 81);
        int height = 9;


        context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("fixedminecraft", "textures/icons.png"), right - width, top, 81f - width, 18f, width, height, 128, 16,
                ColorHelper.getWhite(1F));
    }


    private static void drawSaturationOverlay(HUDOverlayEvent.Saturation event, MinecraftClient mc) {
        drawSaturationOverlay(event.context, event.saturationLevel, mc, event.x, event.y);
    }


    private static void generateBarOffsets(int top, int left, int right, int ticks, PlayerEntity player) {
        int preferFoodBars = 10;
        var shouldAnimatedFood = false;
        HungerManager hungerManager = player.getHungerManager();

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
