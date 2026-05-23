package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.model.monster.dragon.EnderDragonModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EnderDragonModel.class)
public abstract class EnderDragonModelMixin {

    @ModifyConstant(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;)V", constant = @Constant(floatValue = 5.0f, ordinal = 2))
    private float properTailDirection(float value) {
        return -value/2.0f;
    }
}
