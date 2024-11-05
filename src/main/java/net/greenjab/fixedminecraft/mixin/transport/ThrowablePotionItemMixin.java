package net.greenjab.fixedminecraft.mixin.transport;

import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.ThrowablePotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ThrowablePotionItem.class)
public class ThrowablePotionItemMixin {
    @ModifyConstant(method = "use", constant = @Constant(floatValue = 0.5f))
    private float longerLingeringThrows(float constant){
        ThrowablePotionItem TPI = (ThrowablePotionItem)(Object)this;
        if (TPI instanceof LingeringPotionItem) {
            return 0.8f;
        }
        return constant;
    }
}
