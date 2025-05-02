package net.greenjab.fixedminecraft.mixin.netherite;

import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EquippableComponent.class)
public class EquippableComponentMixin {

    @Redirect(method = "equip", at = @At(value = "INVOKE",
                                         target = "Lnet/minecraft/entity/player/PlayerEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"
    ))
    private ItemStack noNetheriteFix2(PlayerEntity instance, EquipmentSlot equipmentSlot) {
        return instance.equipment.get(equipmentSlot); }
}
