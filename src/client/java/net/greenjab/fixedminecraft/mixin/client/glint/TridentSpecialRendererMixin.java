package net.greenjab.fixedminecraft.mixin.client.glint;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.TridentSpecialRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Credit: Pepperoni-Jabroni */
@Mixin(TridentSpecialRenderer.class)
public abstract class TridentSpecialRendererMixin {

    @WrapOperation(method = "submit", at = @At(value = "INVOKE",
                                               target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;entityGlint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType setEnchantTheRainbowItemStack(Operation<RenderType> original) {
        return EnchantGlint.getEntityGlint();
    }
}
