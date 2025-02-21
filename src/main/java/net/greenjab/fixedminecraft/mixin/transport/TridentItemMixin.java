package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Iterator;

@Mixin(TridentItem.class)
public class TridentItemMixin {
    /**
     * Extends the duration of the riptide effect based on the enchantment level, similar to rocket boosts.
     */
    @ModifyArg(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;useRiptide(IFLnet/minecraft/item/ItemStack;)V"), index = 0)
    private int modifyRiptideTicks(int riptideTicks, @Local(argsOnly = true) ItemStack stack) {

        return 15 + FixedMinecraftEnchantmentHelper.enchantLevel(stack, "riptide") * 5;
    }
}
