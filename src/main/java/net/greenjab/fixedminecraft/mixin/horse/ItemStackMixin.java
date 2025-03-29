package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    //TODO test
    /*@Shadow
    public abstract boolean hasEnchantments();

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void enchantableHorseArmor(CallbackInfoReturnable<Boolean> cir) {
       // if (((ItemStack)(Object)this).getItem() instanceof AnimalArmorItem animalArmorItem) {
         //   if (animalArmorItem.getBreakSound() == SoundEvents.ENTITY_ITEM_BREAK) {
        if (((ItemStack)(Object)this).getComponents().contains(DataComponentTypes.EQUIPPABLE)) {
            //if (animalArmorItem.getBreakSound() == SoundEvents.ENTITY_ITEM_BREAK) {
            if (((ItemStack)(Object)this).getComponents().get(DataComponentTypes.EQUIPPABLE).equipSound() == SoundEvents.ENTITY_HORSE_ARMOR) {
                cir.setReturnValue(!this.hasEnchantments());
            }
        }
    }*/
}
