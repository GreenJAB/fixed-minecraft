package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.block.InfestedBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InfestedBlock.class)
public abstract class InfestedBlockMixin{
    @Redirect(method = "onStacksDropped", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;hasAnyEnchantmentsIn(Lnet/minecraft/item/ItemStack;Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean silkDropsSilverfish(ItemStack stack, TagKey<Enchantment> tag) {
        return false;
    }
}
