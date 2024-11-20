package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.block.InfestedBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InfestedBlock.class)
public abstract class InfestedBlockMixin{
    @Redirect(method = "onStacksDropped", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I"))
    public int silkDropsSilverfish(Enchantment enchantment, ItemStack stack) {
        return 0;
    }
}
