package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.world.level.block.CakeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {

    @ModifyConstant(method = "eat", constant = @Constant(floatValue = 0.1f))
    private static float cakeStamina(float constant) {
        return 0.6F;
    }

}
