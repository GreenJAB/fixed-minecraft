package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.component.type.ConsumableComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ConsumableComponents.class)
public class ConsumableComponentsMixin {

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 100, ordinal = 0))
    private static int longerGappleRegen(int constant) {
        return 200;
    }
}
