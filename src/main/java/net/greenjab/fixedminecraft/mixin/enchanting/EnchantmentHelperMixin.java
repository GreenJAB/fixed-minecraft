package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getPossibleEntries", at = @At("HEAD"))
    private static void printStart(int power, ItemStack stack, boolean treasureAllowed,
                                   CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        System.out.println("EnchantmentHelper#getPossibleEntries");
    }

    @Inject(method = "getPossibleEntries", at = @At("RETURN"))
    private static void printEnd(int power, ItemStack stack, boolean treasureAllowed,
                                 CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        System.out.println("----");
    }

    @Inject(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private static void checkEnchantmentCapacity_(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir,
                                                 @Local Enchantment enchantment, @Local(ordinal = 1) int levelRef) {
        System.out.println("added enchantment " + enchantment + "at level " + levelRef + " to the list");
    }

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMinPower(I)I"))
    private static int checkEnchantmentCapacity(Enchantment enchantment, int level, @Local(argsOnly = true) ItemStack itemStack) {
        int capacity = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(itemStack);
        int enchPower = FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantment, level);
        System.out.println("power: " + enchPower + ", capacity: "+  capacity);

        // check if item can hold the enchantment
        if (capacity < enchPower) {
            System.out.println("not enough capacity");
            return Integer.MAX_VALUE;
        }

        // return the enchantment power of the enchantment at the given level to the >= check
        System.out.println("passed power to >= check");
        return enchPower;
    }

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxPower(I)I"))
    private static int bypassCheck(Enchantment enchantment, int level, @Local(argsOnly = true) int power) {
        // bypass this check
        System.out.println("bypassed getMaxPower check; argument power: " + power);
        return power;
    }

    // FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantment, level) > power -> continue;
    // FMEH.getEPower(enchantment, level) <= maxPower -> list.add

    // maxPower >= FMEH.getPower(enchantment, level) && FMEH.getECapacity(stack) > FMEH.getPower(enchantment, level) -> list.add

    @Inject(method = "calculateRequiredExperienceLevel", at = @At("HEAD"), cancellable = true)
    private static void costBasedOnEnchantmentPower(Random random, int slotIndex, int bookshelfCount, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(stack));
    }
}
