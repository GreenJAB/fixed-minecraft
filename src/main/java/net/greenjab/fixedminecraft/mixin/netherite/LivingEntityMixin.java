package net.greenjab.fixedminecraft.mixin.netherite;

import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "getItemBySlot", at = @At("RETURN"), cancellable = true)
    private void noNetherite(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemStack = cir.getReturnValue();
        if (itemStack.is(ModTags.UNBREAKABLE) && itemStack.nextDamageWillBreak()) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Redirect(method = "swapHandItems", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
    ))
    private ItemStack noNetheriteFix1(LivingEntity instance, EquipmentSlot slot) {
    return instance.equipment.get(slot); }
}
