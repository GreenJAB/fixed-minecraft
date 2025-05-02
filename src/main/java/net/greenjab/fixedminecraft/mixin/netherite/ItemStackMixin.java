package net.greenjab.fixedminecraft.mixin.netherite;

import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract void setDamage(int damage);

    @Shadow
    public abstract int getMaxDamage();

    @Inject(method = "shouldBreak", at = @At("RETURN"), cancellable = true)
    private void dontBreakNetherite(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            ItemStack itemStack = (ItemStack) (Object)this;
            if (itemStack.isIn(ModTags.UNBREAKABLE)) {
                this.setDamage(this.getMaxDamage()-1);
                cir.setReturnValue(false);
            }
        }
    }

}
