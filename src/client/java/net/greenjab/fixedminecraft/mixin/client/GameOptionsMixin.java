package net.greenjab.fixedminecraft.mixin.client;

import com.mojang.serialization.Codec;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
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

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Shadow
    @Final
    private static Text CHUNK_FADE_TOOLTIP;
    @Unique
    SimpleOption<Double> newChunkFade = new SimpleOption<>(
            "options.chunkFade",
            SimpleOption.constantTooltip(CHUNK_FADE_TOOLTIP),
            (optionText, value) -> value <= 0.0
            ? Text.translatable("options.chunkFade.none")
            : Text.translatable("options.chunkFade.seconds", new Object[]{String.format(Locale.ROOT, "%.2f", value)}),
            new SimpleOption.ValidatingIntSliderCallbacks(0, 40)
                    .withModifier(/* method_76249 */ ticks -> ticks / 20.0, /* method_76255 */ seconds -> (int)(seconds * 20.0), true),
            Codec.doubleRange(0.0, 2.0),
            0.0,
             value -> {}
    );

    @Inject(method = "acceptProfiledOptions", at = @At("TAIL"))
    private void armorHudOption(GameOptions.OptionVisitor visitor, CallbackInfo ci){
        visitor.accept("newArmorHud", FixedMinecraftClient.newArmorHud);
        visitor.accept("fog_21_6", FixedMinecraftClient.fog_21_6);
    }

    @ModifyArg(method = "acceptProfiledOptions", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/option/GameOptions$OptionVisitor;accept(Ljava/lang/String;Lnet/minecraft/client/option/SimpleOption;)V", ordinal = 2
    ), index = 1)
    private SimpleOption<Double> setNewChunkFade(SimpleOption<Double> option){
        return newChunkFade;
    }

    @Inject(method = "getChunkFade", at = @At(value = "HEAD"), cancellable = true)
    private void getNewChunkFade(CallbackInfoReturnable<SimpleOption<Double>> cir) {
        cir.setReturnValue(newChunkFade);
    }

}
