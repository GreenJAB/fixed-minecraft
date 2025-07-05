package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


@Mixin(CakeBlock.class)
public class CakeBlockMixin {

    @ModifyConstant(method = "tryEat", constant = @Constant(floatValue = 0.1f))
    private static float cakeStamina(float constant) {
        return 0.6F;
    }

}
