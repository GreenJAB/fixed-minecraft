package net.greenjab.fixedminecraft.mixin.netherite;

import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "getEquippedStack", at = @At("RETURN"), cancellable = true)
    private void noNetherite(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemStack = cir.getReturnValue();
        if (itemStack.isIn(ModTags.UNBREAKABLE) && itemStack.willBreakNextUse()) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Redirect(method = "swapHandStacks", at = @At(value = "INVOKE",
                                                  target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"
    ))
    private ItemStack noNetheriteFix1(LivingEntity instance, EquipmentSlot slot) {
        return instance.equipment.get(slot); }
}
