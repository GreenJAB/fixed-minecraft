package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMinPower(I)I"))
    private static int checkEnchantmentCapacity(Enchantment enchantment, int level, @Local(argsOnly = true) ItemStack itemStack) {
        int capacity = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(itemStack);
        int enchPower = FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantment, level);

        if (capacity < enchPower) {
            return Integer.MAX_VALUE;
        }
        return enchPower;
    }

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxPower(I)I"))
    private static int bypassCheck(Enchantment enchantment, int level, @Local(argsOnly = true) int power) {
        return power;
    }

    @Inject(method = "generateEnchantments", at = @At("HEAD"))
    private static void saveOriginalLevelArgument(Random random, ItemStack stack, int level, boolean treasureAllowed,
                                                  CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir,
                                                  @Share("lvl") LocalIntRef levelReference) {
        levelReference.set(level);
    }

    @ModifyArg(method = "generateEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getPossibleEntries(ILnet/minecraft/item/ItemStack;Z)Ljava/util/List;"), index = 0)
    private static int ignoreArgumentManipulationShenanigans(int power, @Share("lvl") LocalIntRef levelReference) {
        return levelReference.get();
    }
}
