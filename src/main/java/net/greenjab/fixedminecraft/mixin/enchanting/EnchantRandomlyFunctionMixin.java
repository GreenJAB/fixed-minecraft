package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraftEnchantmentHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantRandomlyFunction.class)
public abstract class EnchantRandomlyFunctionMixin {

    @Shadow @Final private boolean includeAdditionalCostComponent;

    @ModifyReturnValue(method = "run", at = @At("RETURN"))
    private ItemStack applySuperEnchant(ItemStack original, @Local RandomSource random) {
        if (this.includeAdditionalCostComponent) return original;
        return FixedMinecraftEnchantmentHelper.applySuperEnchants(original, random);
    }
}
