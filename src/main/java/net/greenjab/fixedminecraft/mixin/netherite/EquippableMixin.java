package net.greenjab.fixedminecraft.mixin.netherite;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Equippable.class)
public abstract class EquippableMixin {

    @Redirect(method = "swapWithEquipmentSlot", at = @At(value = "INVOKE",
          target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
    ))
    private ItemStack noNetheriteFix2(Player instance, EquipmentSlot equipmentSlot) {
        return instance.equipment.get(equipmentSlot); }
}
