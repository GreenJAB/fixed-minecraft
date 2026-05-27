package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldOpenFlows.class)
public abstract class CreateWorldScreenMixin {

    @ModifyExpressionValue(method = "openWorldCheckWorldStemCompatibility", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WorldData;worldGenSettingsLifecycle()Lcom/mojang/serialization/Lifecycle;"))
    private Lifecycle skipExperimentalScreen(Lifecycle original) {
        return Lifecycle.stable();
    }
}
