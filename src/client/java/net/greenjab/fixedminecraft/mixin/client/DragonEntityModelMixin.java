package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.render.entity.DragonEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DragonEntityModel.class)
public class DragonEntityModelMixin {

    @ModifyConstant(method = "setAngles(Lnet/minecraft/client/render/entity/state/EnderDragonEntityRenderState;)V", constant = @Constant(floatValue = 5.0f, ordinal = 2))
    private float properTailDirection(float value) {
        return -value/2.0f;
    }
}
