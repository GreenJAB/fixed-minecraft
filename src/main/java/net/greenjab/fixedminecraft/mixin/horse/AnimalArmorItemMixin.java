package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalArmorItem.class)
public abstract class AnimalArmorItemMixin {

    /*@ModifyArg(method = "<init>(Lnet/minecraft/item/equipment/ArmorMaterial;Lnet/minecraft/item/AnimalArmorItem$Type;Lnet/minecraft/registry/entry/RegistryEntry;ZLnet/minecraft/item/Item$Settings;)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/equipment/ArmorMaterial;applyBodyArmorSettings(Lnet/minecraft/item/Item$Settings;Lnet/minecraft/registry/entry/RegistryEntry;ZLnet/minecraft/registry/entry/RegistryEntryList;)Lnet/minecraft/item/Item$Settings;"
    ), index = 0)
    private static Item.Settings enchantableHorseArmor(Item.Settings settings) {
        return settings.enchantable(1);
    }*/

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void enchantableHorseArmor(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!stack.hasEnchantments());
        cir.cancel();
    }
}
