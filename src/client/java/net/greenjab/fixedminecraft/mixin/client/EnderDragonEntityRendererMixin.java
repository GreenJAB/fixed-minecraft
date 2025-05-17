package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EnderDragonEntityRenderer.DragonEntityModel.class)
public class EnderDragonEntityRendererMixin {

    @ModifyConstant(method = "render", constant = @Constant(floatValue = 5.0f, ordinal = 2))
    private float properTailDirection(float value) {
        return -value/2.0f;
    }
}
