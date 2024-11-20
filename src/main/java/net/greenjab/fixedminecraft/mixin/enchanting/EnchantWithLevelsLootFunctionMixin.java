package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantWithLevelsLootFunction.class)
public class EnchantWithLevelsLootFunctionMixin {

    @Inject(method = "process", at = @At("RETURN"), cancellable = true)
    private void applySuperEnchant(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir, @Local Random random) {
        ItemStack IS = cir.getReturnValue();
        cir.setReturnValue(FixedMinecraftEnchantmentHelper.applySuperEnchants(IS, random));
    }
}
