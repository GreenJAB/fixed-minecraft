package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Inject(method = "canUseSlot", at = @At(value = "HEAD"), cancellable = true)
    private void muleArmourslot(EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir){
        MobEntity LE = (MobEntity) (Object)this;
        if (LE instanceof MuleEntity) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "isHorseArmor", at = @At(value = "HEAD"), cancellable = true)
    private void isHorseArmor(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        MobEntity LE = (MobEntity) (Object)this;
        if (LE instanceof MuleEntity) {
            cir.setReturnValue(stack.getItem() instanceof AnimalArmorItem animalArmorItem &&
                               animalArmorItem.getType() == AnimalArmorItem.Type.EQUESTRIAN);
            cir.cancel();
        }
    }
}
