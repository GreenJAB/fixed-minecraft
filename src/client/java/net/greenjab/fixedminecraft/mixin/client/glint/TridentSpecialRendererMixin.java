package net.greenjab.fixedminecraft.mixin.client.glint;

import net.minecraft.client.renderer.special.TridentSpecialRenderer;
import org.spongepowered.asm.mixin.Mixin;

/** Credit: Pepperoni-Jabroni */
@Mixin(TridentSpecialRenderer.class)
public abstract class TridentSpecialRendererMixin {

    /*@WrapOperation(method = "submit", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;entitySolidGlint(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType setEnchantTheRainbowItemStack(Identifier texture, Operation<RenderType> original) {
        return EnchantGlint.getEntityGlint();
    }*/
}
