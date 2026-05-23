package net.greenjab.fixedminecraft.mixin.netherite;

import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract void setDamageValue(int value);

    @Shadow
    public abstract int getMaxDamage();

    @Inject(method = "isBroken", at = @At("RETURN"), cancellable = true)
    private void dontBreakNetherite(CallbackInfoReturnable<Boolean> cir) {
       if (cir.getReturnValue()) {
           ItemStack itemStack = (ItemStack) (Object)this;
           if (itemStack.is(ModTags.UNBREAKABLE)) {
               this.setDamageValue(this.getMaxDamage()-1);
               cir.setReturnValue(false);
           }
       }
   }

}
