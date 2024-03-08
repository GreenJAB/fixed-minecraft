package net.greenjab.fixedminecraft.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.greenjab.fixedminecraft.helpers.TextureHelper;
import net.greenjab.fixedminecraft.util.IntPoint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import net.greenjab.fixedminecraft.api.event.HUDOverlayEvent;

import java.util.Random;
import java.util.Vector;

object HUDOverlayHandler
{
    lateinit var INSTANCE: HUDOverlayHandler;

    var unclampedFlashAlpha = 0f;
    var flashAlpha = 0f;
    var alphaDir: Byte = 1;
    var foodIconsOffset = 0;
    var needDisableBlend = false;

    val FOOD_BAR_HEIGHT  = 39;

    //public final Vector<IntPoint> healthBarOffsets = new Vector<>();
    var foodBarOffsets: Vector<IntPoint> = Vector<IntPoint>();

    var random: Random =  Random();

    fun init()
    {
        INSTANCE = HUDOverlayHandler;
    }




    fun onPreRender(context: DrawContext)
    {
        foodIconsOffset = FOOD_BAR_HEIGHT;

        // If ModConfig.INSTANCE is null then we're probably still in the init phase
        //if (ModConfig.INSTANCE == null)
        //    return;

        //if (!ModConfig.INSTANCE.showFoodExhaustionHudUnderlay)
       //     return;

        var mc = MinecraftClient.getInstance();
        var player = mc.player;
        requireNotNull(player)

        val right = mc.window.scaledWidth / 2 + 91
        val top = mc.window.scaledHeight - foodIconsOffset
        val exhaustion = player.hungerManager.exhaustion

        val renderEvent = HUDOverlayEvent.Exhaustion(exhaustion, right, top, context)
        HUDOverlayEvent.Exhaustion.EVENT.invoker().interact(renderEvent)
        if (!renderEvent.isCanceled) {
            drawExhaustionOverlay(renderEvent, mc, 1f)
        }
    }

    fun generateBarOffsets(top: Int, left: Int, right: Int, ticks: Int, player: PlayerEntity)
    {
        //final int preferHealthBars = 10;
        var preferFoodBars = 10;

        /*final float maxHealth = player.getMaxHealth();
        final float absorptionHealth = (float) Math.ceil(player.getAbsorptionAmount());

        int healthBars = (int) Math.ceil((maxHealth + absorptionHealth) / 2.0F);
        // When maxHealth + absorptionHealth is greater than Integer.INT_MAX,
        // Minecraft will disable heart rendering due to a quirk of MathHelper.ceil.
        // We have a much lower threshold since there's no reason to get the offsets
        // for thousands of hearts.
        // Note: Infinite and > INT_MAX absorption has been seen in the wild.
        // This will effectively disable rendering whenever health is unexpectedly large.
        if (healthBars < 0 || healthBars > 1000) {
            healthBars = 0;
        }

        int healthRows = (int) Math.ceil((float) healthBars / (float) preferHealthBars);

        int healthRowHeight = Math.max(10 - (healthRows - 2), 3);

        boolean shouldAnimatedHealth = false;*/
        var shouldAnimatedFood = false;

        // when some mods using custom render, we need to least provide an option to cancel animation
        if (true/*ModConfig.INSTANCE.showVanillaAnimationsOverlay*/)
        {
            var hungerManager = player.getHungerManager();

            // in vanilla saturation level is zero will show hunger animation
            var saturationLevel  = hungerManager.getSaturationLevel();
            var foodLevel = hungerManager.getFoodLevel();
            shouldAnimatedFood = saturationLevel <= 0.0F && ticks % (foodLevel * 3 + 1) == 0;

            // in vanilla health is too low (below 5) will show heartbeat animation
            // when regeneration will also show heartbeat animation, but we don't need now
            //shouldAnimatedHealth = Math.ceil(player.getHealth()) <= 4;
        }

        // hard code in `InGameHUD`
        random.setSeed( (ticks * 312871)as Long);

        // adjust the size
        //if (healthBarOffsets.size() != healthBars)
        //   healthBarOffsets.setSize(healthBars);

        if (foodBarOffsets.size != preferFoodBars)
            foodBarOffsets.setSize(preferFoodBars);

        // left alignment, multiple rows, reverse
        /*for (int i = healthBars - 1; i >= 0; --i)
        {
            int row = (int) Math.ceil((float) (i + 1) / (float) preferHealthBars) - 1;
            int x = left + i % preferHealthBars * 8;
            int y = top - row * healthRowHeight;
            // apply the animated offset
            if (shouldAnimatedHealth)
                y += random.nextInt(2);

            // reuse the point object to reduce memory usage
            IntPoint point = healthBarOffsets.get(i);
            if (point == null)
            {
                point = new IntPoint();
                healthBarOffsets.set(i, point);
            }

            point.x = x - left;
            point.y = y - top;
        }*/

        // right alignment, single row
        for ( i in 0 until preferFoodBars)
        {
            var x = right - i * 8 - 9;
            var y = top;

            // apply the animated offset
            if (shouldAnimatedFood)
                y += random.nextInt(3) - 1;

            // reuse the point object to reduce memory usage
            var point = foodBarOffsets.get(i);
            if (point == null)
            {
                point = IntPoint();
                foodBarOffsets.set(i, point);
            }

            point.x = x - right;
            point.y = y - top;
        }
    }

    fun onRender(context: DrawContext)
    {
        // If ModConfig.INSTANCE is null then we're probably still in the init phase
        //if (ModConfig.INSTANCE == null)
        //    return;

        if (!shouldRenderAnyOverlays())
            return;

        val mc: MinecraftClient = MinecraftClient.getInstance()
        val player: PlayerEntity = mc.player!!
        val stats = player.hungerManager

        val top = mc.window.scaledHeight - foodIconsOffset
        val left = mc.window.scaledWidth / 2 - 91
        val right = mc.window.scaledWidth / 2 + 91

        // generate at the beginning to avoid ArrayIndexOutOfBoundsException
        generateBarOffsets(top, left, right, mc.inGameHud.getTicks(), player);

        // notify everyone that we should render saturation hud overlay
        val saturationRenderEvent = HUDOverlayEvent.Saturation(stats.getSaturationLevel(), right, top, context);

        // cancel render overlay event when configuration disabled.
        //if (!ModConfig.INSTANCE.showSaturationHudOverlay)
        //    saturationRenderEvent.isCanceled = true;

        // notify everyone that we should render saturation hud overlay
        if (!saturationRenderEvent.isCanceled)
            HUDOverlayEvent.Saturation.EVENT.invoker().interact(saturationRenderEvent);

        // draw saturation overlay
        if (!saturationRenderEvent.isCanceled)
            drawSaturationOverlay(saturationRenderEvent, mc, 0.0f, 1.0f);

        // try to get the item stack in the player hand
        /*ItemStack heldItem = player.getMainHandStack();
        if (ModConfig.INSTANCE.showFoodValuesHudOverlayWhenOffhand && !FoodHelper.canConsume(heldItem, player))
            heldItem = player.getOffHandStack();

        boolean shouldRenderHeldItemValues = !heldItem.isEmpty() && FoodHelper.canConsume(heldItem, player);
        if (!shouldRenderHeldItemValues)
        {
            resetFlash();
            return;
        }

        // restored hunger/saturation overlay while holding food
        FoodValues modifiedFoodValues = FoodHelper.getModifiedFoodValues(heldItem, player);
        FoodValuesEvent foodValuesEvent = new FoodValuesEvent(player, heldItem, FoodHelper.getDefaultFoodValues(heldItem), modifiedFoodValues);
        FoodValuesEvent.EVENT.invoker().interact(foodValuesEvent);
        modifiedFoodValues = foodValuesEvent.modifiedFoodValues;*/

        // draw health overlay if needed
        /*if (shouldShowEstimatedHealth(heldItem, modifiedFoodValues))
        {
            float foodHealthIncrement = FoodHelper.getEstimatedHealthIncrement(heldItem, modifiedFoodValues, player);
            float currentHealth = player.getHealth();
            float modifiedHealth = Math.min(currentHealth + foodHealthIncrement, player.getMaxHealth());

            // only create object when the estimated health is successfully
            HUDOverlayEvent.HealthRestored healthRenderEvent = null;
            if (currentHealth < modifiedHealth)
                healthRenderEvent = new HUDOverlayEvent.HealthRestored(modifiedHealth, heldItem, modifiedFoodValues, left, top, context);

            // notify everyone that we should render estimated health hud
            if (healthRenderEvent != null)
                HUDOverlayEvent.HealthRestored.EVENT.invoker().interact(healthRenderEvent);

            if (healthRenderEvent != null && !healthRenderEvent.isCanceled)
                drawHealthOverlay(healthRenderEvent, mc, flashAlpha);
        }*/

        /*if (ModConfig.INSTANCE.showFoodValuesHudOverlay)
        {
            // notify everyone that we should render hunger hud overlay
            HUDOverlayEvent.HungerRestored hungerRenderEvent = new HUDOverlayEvent.HungerRestored(stats.getFoodLevel(), heldItem, modifiedFoodValues, right, top, context);
            HUDOverlayEvent.HungerRestored.EVENT.invoker().interact(hungerRenderEvent);
            if (hungerRenderEvent.isCanceled)
                return;

            // calculate the final hunger and saturation
            int foodHunger = modifiedFoodValues.hunger;
            float foodSaturationIncrement = modifiedFoodValues.getSaturationIncrement();

            // draw hunger overlay
            drawHungerOverlay(hungerRenderEvent, mc, foodHunger, flashAlpha, FoodHelper.isRotten(heldItem));

            int newFoodValue = stats.getFoodLevel() + foodHunger;
            float newSaturationValue = stats.getSaturationLevel() + foodSaturationIncrement;

            // draw saturation overlay of gained
            if (!saturationRenderEvent.isCanceled)
            {
                float saturationGained = newSaturationValue > newFoodValue ? newFoodValue - stats.getSaturationLevel() : foodSaturationIncrement;
                drawSaturationOverlay(saturationRenderEvent, mc, saturationGained, flashAlpha);
            }
        }*/
    }

    fun drawSaturationOverlay(context: DrawContext, saturationGained: Float, saturationLevel: Float, mc: MinecraftClient, right: Int, top: Int, alpha: Float)
    {
        if (saturationLevel + saturationGained < 0)
            return;

        enableAlpha(alpha);

        var modifiedSaturation = Math.max(0.0f, Math.min(saturationLevel + saturationGained, 20.0f))
        var startSaturationBar = 0
        var endSaturationBar = (modifiedSaturation / 2).toInt()

        // when require rendering the gained saturation, start should relocation to current saturation tail.
        if (saturationGained != 0.0f)
            startSaturationBar =  Math.max(saturationLevel / 2.0f, 0.0f) as Int;

        var iconSize = 9;

        for (i in startSaturationBar until endSaturationBar)
        {
            // gets the offset that needs to be render of icon
            val offset = foodBarOffsets.get(i)
            if (offset == null)
                continue

            val x = right + offset.x
            val y = top + offset.y

            var v = 0
            var u = 0

            val effectiveSaturationOfBar = (modifiedSaturation / 2.0F) - i

            if (effectiveSaturationOfBar >= 1)
                u = 3 * iconSize;
            else if (effectiveSaturationOfBar > .5)
                u = 2 * iconSize;
            else if (effectiveSaturationOfBar > .25)
                u = 1 * iconSize;

            context.drawTexture(TextureHelper.MOD_ICONS, x, y, u, v, iconSize, iconSize);
        }

        disableAlpha(alpha);
    }

    fun drawHungerOverlay(context: DrawContext, hungerRestored: Int, foodLevel: Int, mc: MinecraftClient, right: Int, top: Int, alpha: Float, useRottenTextures: Boolean)
    {
        if (hungerRestored <= 0)
            return;

        enableAlpha(alpha);

        val modifiedFood = Math.max(0, Math.min(20, foodLevel + hungerRestored))

        val startFoodBars = Math.max(0, foodLevel / 2)
        val endFoodBars = modifiedFood / 2

        val iconStartOffset = 16
        val iconSize = 9

        for ( i in startFoodBars until endFoodBars)
        {
            // gets the offset that needs to be render of icon
            val offset = foodBarOffsets.get(i)
            if (offset == null)
                continue

            val x = right + offset.x
            val y = top + offset.y

            val backgroundSprite = TextureHelper.getFoodTexture(useRottenTextures, TextureHelper.FoodType.EMPTY)

            // very faint background
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha * 0.25F);
            context.drawGuiTexture(backgroundSprite, x, y, iconSize, iconSize);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

            val isHalf = i * 2 + 1 == modifiedFood
            val iconSprite = TextureHelper.getFoodTexture(useRottenTextures, if (isHalf) TextureHelper.FoodType.HALF else TextureHelper.FoodType.FULL)

            context.drawGuiTexture(iconSprite, x, y, iconSize, iconSize);
        }

        disableAlpha(alpha);
    }

   /* public void drawHealthOverlay(DrawContext context, float health, float modifiedHealth, MinecraftClient mc, int right, int top, float alpha)
    {
        if (modifiedHealth <= health)
            return;

        enableAlpha(alpha);

        int fixedModifiedHealth = (int) Math.ceil(modifiedHealth);
        boolean isHardcore = mc.player.getWorld() != null && mc.player.getWorld().getLevelProperties().isHardcore();

        int startHealthBars = (int) Math.max(0, (Math.ceil(health) / 2.0F));
        int endHealthBars = (int) Math.max(0, Math.ceil(modifiedHealth / 2.0F));

        int iconSize = 9;

        for (int i = startHealthBars; i < endHealthBars; ++i)
        {
            // gets the offset that needs to be render of icon
            IntPoint offset = healthBarOffsets.get(i);
            if (offset == null)
                continue;

            int x = right + offset.x;
            int y = top + offset.y;

            Identifier backgroundSprite = TextureHelper.getHeartTexture(isHardcore, HeartType.CONTAINER);

            // very faint background
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha * 0.25F);
            context.drawGuiTexture(backgroundSprite, x, y, iconSize, iconSize);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

            boolean isHalf = i * 2 + 1 == fixedModifiedHealth;
            Identifier iconSprite = TextureHelper.getHeartTexture(isHardcore, isHalf ? HeartType.HALF : HeartType.FULL);

            context.drawGuiTexture(iconSprite, x, y, iconSize, iconSize);
        }

        disableAlpha(alpha);
    }*/

    fun drawExhaustionOverlay(context: DrawContext, exhaustion: Float, mc: MinecraftClient, right: Int, top: Int, alpha: Float)
    {
        val maxExhaustion  = 4.0f/*FoodHelper.MAX_EXHAUSTION*/;
        // clamp between 0 and 1
        val ratio = exhaustion / maxExhaustion.coerceIn(0f, 1f)
        val width = (ratio * 81).toInt()
        val height = 9.toInt()

        enableAlpha(.75f);
        context.drawTexture(TextureHelper.MOD_ICONS, right - width, top, 81 - width, 18, width, height);
        disableAlpha(.75f);
    }




    fun drawSaturationOverlay(event: HUDOverlayEvent.Saturation, mc: MinecraftClient, saturationGained: Float, alpha: Float)
    {
        drawSaturationOverlay(event.context, saturationGained, event.saturationLevel, mc, event.x, event.y, alpha);
    }

    /*private void drawHungerOverlay(HUDOverlayEvent.HungerRestored event, MinecraftClient mc, int hunger, float alpha, boolean useRottenTextures)
    {
        drawHungerOverlay(event.context, hunger, event.currentFoodLevel, mc, event.x, event.y, alpha, useRottenTextures);
    }*/

    /*private void drawHealthOverlay(HUDOverlayEvent.HealthRestored event, MinecraftClient mc, float alpha)
    {
        drawHealthOverlay(event.context, mc.player.getHealth(), event.modifiedHealth, mc, event.x, event.y, alpha);
    }*/

     fun drawExhaustionOverlay(event: HUDOverlayEvent.Exhaustion, mc: MinecraftClient, alpha: Float)
    {
        drawExhaustionOverlay(event.context, event.exhaustion, mc, event.x, event.y, alpha);
    }


    fun enableAlpha(alpha: Float) {
        needDisableBlend = !GL11.glIsEnabled(GL11.GL_BLEND);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    }

    fun disableAlpha(alpha:Float)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (needDisableBlend)
            RenderSystem.disableBlend();
    }

    fun shouldRenderAnyOverlays():Boolean
    {
        //return ModConfig.INSTANCE.showFoodValuesHudOverlay || ModConfig.INSTANCE.showSaturationHudOverlay || ModConfig.INSTANCE.showFoodHealthHudOverlay;
        return true;
    }


    fun onClientTick()
    {
        unclampedFlashAlpha += alphaDir * 0.125F;
        if (unclampedFlashAlpha >= 1.5F)
        {
            alphaDir = -1;
        }
        else if (unclampedFlashAlpha <= -0.5F)
        {
            alphaDir = 1;
        }
        flashAlpha = Math.max(0F, Math.min(1F, unclampedFlashAlpha)) * Math.max(0F, Math.min(1F, 0.65f/*ModConfig.INSTANCE.maxHudOverlayFlashAlpha*/));
    }

    fun resetFlash()
    {
         flashAlpha = 0.0f;
        unclampedFlashAlpha =flashAlpha;
        alphaDir = 1;
    }


   /* private boolean shouldShowEstimatedHealth(ItemStack hoveredStack, FoodValues modifiedFoodValues)
    {
        // then configuration cancel the render event
        if (!ModConfig.INSTANCE.showFoodHealthHudOverlay)
            return false;

        // Offsets size is set to zero intentionally to disable rendering when health is infinite.
        if (healthBarOffsets.size() == 0)
            return false;

        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        HungerManager stats = player.getHungerManager();

        // in the `PEACEFUL` mode, health will restore faster
        if (player.getWorld().getDifficulty() == Difficulty.PEACEFUL)
            return false;

        // when player has any changes health amount by any case can't show estimated health
        // because player will confused how much of restored/damaged healths
        if (stats.getFoodLevel() >= 18)
            return false;

        if (player.hasStatusEffect(StatusEffects.POISON))
            return false;

        if (player.hasStatusEffect(StatusEffects.WITHER))
            return false;

        if (player.hasStatusEffect(StatusEffects.REGENERATION))
            return false;

        return true;
    }*/



}
