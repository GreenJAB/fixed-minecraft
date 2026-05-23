package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoSettingsScreenMixin {

    @Inject(method = "displayOptions", at = @At("RETURN"), cancellable = true)
    private static void armorHudOption(Options options, CallbackInfoReturnable<OptionInstance<?>[]> cir){
        cir.setReturnValue(new OptionInstance[]{
                options.framerateLimit(), options.enableVsync(), options.inactivityFpsLimit(), options.guiScale(), options.fullscreen(), options.gamma(),
                FixedMinecraftClient.newArmorHud,
                FixedMinecraftClient.fog_21_6
        });
    }
}
