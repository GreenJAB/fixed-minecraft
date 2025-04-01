package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.slot.ArmorSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(HorseScreenHandler.class)
public class HorseScreenHandlerMixin {

    /*@ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/HorseScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 0), index = 0)
    private Slot stackedSaddles(Slot par1, @Local(ordinal = 1) Inventory inventory, @Local(argsOnly = true) AbstractHorseEntity entity) {
        return new ArmorSlot(inventory, entity, EquipmentSlot.SADDLE, 0, 8, 18, Identifier.ofVanilla("container/slot/saddle"))  {
            @Override
            public boolean isEnabled() {
                return entity.canUseSlot(EquipmentSlot.SADDLE) && entity.getType().isIn(EntityTypeTags.CAN_EQUIP_SADDLE);
            }
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        };
    }*/
}
