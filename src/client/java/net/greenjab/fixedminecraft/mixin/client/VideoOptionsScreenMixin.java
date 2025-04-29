package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
