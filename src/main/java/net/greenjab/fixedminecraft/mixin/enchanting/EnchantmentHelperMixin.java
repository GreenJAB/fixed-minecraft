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

    // // Debug output
    // @Inject(method = "getPossibleEntries", at = @At("HEAD"))
    // private static void printStart(int power, ItemStack stack, boolean treasureAllowed,
    //                                CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
    //     System.out.println("--- EnchantmentHelper#getPossibleEntries");
    // }
    //
    // @Inject(method = "getPossibleEntries", at = @At("RETURN"))
    // private static void printEnd(int power, ItemStack stack, boolean treasureAllowed,
    //                              CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
    //     Map<Enchantment, Integer> enchantments = new HashMap<>();
    //     cir.getReturnValue().forEach((entry -> {enchantments.put(entry.enchantment, entry.level);}));
    //     System.out.println("possible enchantments: " + enchantments);
    //     System.out.println("----");
    // }
    //
    // @Inject(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    // private static void checkEnchantmentCapacity_(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir,
    //                                              @Local Enchantment enchantment, @Local(ordinal = 1) int levelRef) {
    //     System.out.println("added enchantment " + enchantment + "at level " + levelRef + " to the list");
    // }


    // actual changes
    /**
     * Changing functionality to make it work based on enchanting power (target getMinPower)
     */
    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMinPower(I)I"))
    private static int checkEnchantmentCapacity(Enchantment enchantment, int level, @Local(argsOnly = true) ItemStack itemStack) {
        int capacity = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(itemStack);
        int enchPower = FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantment, level);
        // System.out.println("power: " + enchPower + ", capacity: "+  capacity);

        // check if item can hold the enchantment
        if (capacity < enchPower) {
            // System.out.println("not enough capacity");
            return Integer.MAX_VALUE;
        }
        // System.out.println("item can hold enchantment power");

        // return the enchantment power of the enchantment at the given level to the >= check
        // System.out.println("passed power to >= check");
        return enchPower;
    }

    /**
     * Changing functionality to make it work based on enchanting power (target getMaxPower)
     */
    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxPower(I)I"))
    private static int bypassCheck(Enchantment enchantment, int level, @Local(argsOnly = true) int power) {
        // bypass this check
        // System.out.println("bypassed getMaxPower check; argument power: " + power);
        return power;
    }


    // /**
    //  * Makes the enchanting costs based on the enchantment power
    //  */
    // @Inject(method = "calculateRequiredExperienceLevel", at = @At("HEAD"), cancellable = true)
    // private static void costBasedOnEnchantmentPower(Random random, int slotIndex, int bookshelfCount, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
    //     cir.setReturnValue(FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(stack));
    // }


    // The 'generateEnchantments' method changes the 'level' argument based on the slot index
    // The following two mixins undergo this modification
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
