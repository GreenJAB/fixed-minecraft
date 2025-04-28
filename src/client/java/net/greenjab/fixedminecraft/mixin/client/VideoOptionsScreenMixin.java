package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(VideoOptionsScreen.class)
public class VideoOptionsScreenMixin  {


    @Inject(method = "getOptions", at = @At("RETURN"), cancellable = true)
    private static void armorHudOption(GameOptions gameOptions, CallbackInfoReturnable<SimpleOption<?>[]> cir){
        cir.setReturnValue(new SimpleOption[]{
                gameOptions.getGraphicsMode(),
                gameOptions.getViewDistance(),
                gameOptions.getChunkBuilderMode(),
                gameOptions.getSimulationDistance(),
                gameOptions.getAo(),
                gameOptions.getMaxFps(),
                gameOptions.getEnableVsync(),
                gameOptions.getInactivityFpsLimit(),
                gameOptions.getGuiScale(),
                gameOptions.getAttackIndicator(),
                gameOptions.getGamma(),
                gameOptions.getCloudRenderMode(),
                gameOptions.getFullscreen(),
                gameOptions.getParticles(),
                gameOptions.getMipmapLevels(),
                gameOptions.getEntityShadows(),
                gameOptions.getDistortionEffectScale(),
                gameOptions.getEntityDistanceScaling(),
                gameOptions.getFovEffectScale(),
                gameOptions.getShowAutosaveIndicator(),
                gameOptions.getGlintSpeed(),
                gameOptions.getGlintStrength(),
                gameOptions.getMenuBackgroundBlurriness(),
                gameOptions.getBobView(),
                gameOptions.method_71270(),
                FixedMinecraftClient.newArmorHud
        });
    }


}
