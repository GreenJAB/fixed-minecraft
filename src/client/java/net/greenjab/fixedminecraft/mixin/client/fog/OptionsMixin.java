package net.greenjab.fixedminecraft.mixin.client.fog;

import com.mojang.serialization.Codec;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(Options.class)
public abstract class OptionsMixin {

    @Shadow
    @Final
    private static Component GRAPHICS_TOOLTIP_CHUNK_FADE;
    @Unique
    OptionInstance<Double> newChunkFade = new OptionInstance<>(
            "options.chunkFade",
            OptionInstance.cachedConstantTooltip(GRAPHICS_TOOLTIP_CHUNK_FADE),
            (_, value) -> value <= 0.0
            ? Component.translatable("options.chunkFade.none")
            : Component.translatable("options.chunkFade.seconds", String.format(Locale.ROOT, "%.2f", value)),
            new OptionInstance.IntRange(0, 40)
                    .xmap(/* method_76249 */ ticks -> ticks / 20.0, seconds -> (int)(seconds * 20.0), true),
            Codec.doubleRange(0.0, 2.0),
            0.0,
            _ -> {}
    );

    @Inject(method = "processDumpedOptions", at = @At("TAIL"))
    private void armorHudOption(Options.OptionAccess access, CallbackInfo ci){
        access.process("newArmorHud", FixedMinecraftClient.newArmorHud);
        access.process("fog_21_6", FixedMinecraftClient.fog_21_6);
    }

    @ModifyArg(method = "processDumpedOptions", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Options$OptionAccess;process(Ljava/lang/String;Lnet/minecraft/client/OptionInstance;)V", ordinal = 2
    ), index = 1)
    private OptionInstance<Double> setNewChunkFade(OptionInstance<Double> option){
        return newChunkFade;
    }

    @Inject(method = "chunkSectionFadeInTime", at = @At(value = "HEAD"), cancellable = true)
    private void getNewChunkFade(CallbackInfoReturnable<OptionInstance<Double>> cir) {
        cir.setReturnValue(newChunkFade);
    }

}
