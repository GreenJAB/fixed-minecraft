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


    @Inject(method = "getDisplayOptions", at = @At("RETURN"), cancellable = true)
    private static void armorHudOption(GameOptions options, CallbackInfoReturnable<SimpleOption<?>[]> cir){
        cir.setReturnValue(new SimpleOption[]{
                options.getMaxFps(), options.getEnableVsync(), options.getInactivityFpsLimit(), options.getGuiScale(), options.getFullscreen(), options.getGamma(),
                FixedMinecraftClient.newArmorHud,
                FixedMinecraftClient.fog_21_6
        });
    }
}
