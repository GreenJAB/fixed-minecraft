package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Inject(method = "acceptProfiledOptions", at = @At("TAIL"))
    private void armorHudOption(GameOptions.OptionVisitor visitor, CallbackInfo ci){
        visitor.accept("newArmorHud", FixedMinecraftClient.newArmorHud);
    }
}
