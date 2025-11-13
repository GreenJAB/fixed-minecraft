package net.greenjab.fixedminecraft.mixin.client.glint;

import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Pepperoni-Jabroni */
@Mixin(TridentEntityRenderer.class)
public abstract class TridentEntityRendererMixin {

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/projectile/TridentEntity;Lnet/minecraft/client/render/entity/state/TridentEntityRenderState;F)V", at = @At(
            value = "HEAD"
    ))
    private void setEnchantTheRainbowItemStack(TridentEntity tridentEntity, TridentEntityRenderState tridentEntityRenderState, float f,
                                               CallbackInfo ci) {
        EnchantGlint.setTargetStack(tridentEntity.getItemStack());
    }
}
