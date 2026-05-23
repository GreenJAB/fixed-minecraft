package net.greenjab.fixedminecraft.mixin.client.glint;

import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Pepperoni-Jabroni */
@Mixin(ThrownTridentRenderer.class)
public abstract class ThrownTridentRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;F)V", at = @At(
            value = "HEAD"
    ))
    private void setEnchantTheRainbowItemStack(ThrownTrident entity, ThrownTridentRenderState state, float partialTicks, CallbackInfo ci) {
        EnchantGlint.setTargetStack(entity.getWeaponItem());
    }
}
