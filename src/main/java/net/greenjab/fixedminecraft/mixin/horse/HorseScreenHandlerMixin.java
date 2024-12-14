package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(HorseScreenHandler.class)
public class HorseScreenHandlerMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/HorseScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 0), index = 0)
    private Slot stackedSaddles(Slot par1, @Local(argsOnly = true) Inventory inventory, @Local(argsOnly = true) AbstractHorseEntity entity) {
        return new Slot(inventory, 0, 8, 18) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.SADDLE) && !this.hasStack() && entity.canBeSaddled();
            }

            @Override
            public boolean isEnabled() {
                return entity.canBeSaddled();
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        };
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/HorseScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 1), index = 0)
    private Slot bindingHorseArmour(Slot par1, @Local(argsOnly = true) Inventory inventory, @Local(argsOnly = true) AbstractHorseEntity entity) {
        return new Slot(inventory, 1, 8, 36) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return entity.isHorseArmor(stack);
            }

            @Override
            public boolean isEnabled() {
                return entity.hasArmorSlot();
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                ItemStack itemStack = this.getStack();
                return (itemStack.isEmpty() || playerEntity.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack)) && super.canTakeItems(playerEntity);
            }
        };
    }
}
