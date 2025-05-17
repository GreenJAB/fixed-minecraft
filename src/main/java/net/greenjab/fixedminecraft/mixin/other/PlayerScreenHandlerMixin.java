package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.ArmorSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerScreenHandler.class)
public class PlayerScreenHandlerMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 2), index = 0)
    private Slot lapiscost(Slot par1, @Local(argsOnly = true) PlayerEntity owner, @Local(argsOnly = true) PlayerInventory inventory, @Local int i, @Local EquipmentSlot equipmentSlot, @Local Identifier identifier) {
        return new ArmorSlot(inventory,owner, equipmentSlot,  39 - i, 8, 8 + i * 18, identifier) {
            @Override
            public boolean canInsert(ItemStack stack) {
                if (equipmentSlot == EquipmentSlot.HEAD && stack.isIn(ItemTags.BANNERS)) {
                    return true;
                }
                return equipmentSlot == owner.getPreferredEquipmentSlot(stack);
            }
        };
    }

}
