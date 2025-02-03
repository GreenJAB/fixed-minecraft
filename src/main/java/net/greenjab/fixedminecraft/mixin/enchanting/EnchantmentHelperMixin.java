package net.greenjab.fixedminecraft.mixin.enchanting;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {


    @Inject(method = "getPossibleEntries", at = @At(value = "HEAD"), cancellable = true)
    private static void checkEnchantmentCapacity1(int level, ItemStack stack, Stream<RegistryEntry<Enchantment>> possibleEnchantments,
                                                  CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        List<EnchantmentLevelEntry> list = Lists.<EnchantmentLevelEntry>newArrayList();
        boolean bl = stack.isOf(Items.BOOK);
        //int capacity = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(stack);
        possibleEnchantments.filter(/* method_60143 */ enchantment -> ((Enchantment)enchantment.value()).isPrimaryItem(stack) || bl)
                .forEach(/* method_60106 */ enchantmentx -> {
                    Enchantment enchantment = enchantmentx.value();

                    for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
                    //for (int j = level; j >= checkEnchantmentCapacity(enchantmentx, level, stack); j--) {
                        int enchPower = FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantmentx, j);
                        //System.out.println(stack + ", " + capacity + ", " + level + ", " + enchantment.getIdAsString() + ", " + enchPower);
                        //if (capacity < enchPower) {
                        if (level >= enchPower) {

                        //if (level >= enchantment.getMinPower(j) && level <= enchantment.getMaxPower(j)) {
                            list.add(new EnchantmentLevelEntry(enchantmentx, j));
                            break;
                        }
                    }
                });
        cir.setReturnValue(list);
        cir.cancel();
    }

    @Unique
    private static int checkEnchantmentCapacity(RegistryEntry<Enchantment> enchantment, int level, ItemStack itemStack) {
        int capacity = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(itemStack);
        int enchPower = FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantment, level);
        System.out.println(itemStack + ", " + capacity + ", " + level + ", " + enchantment.getIdAsString() + ", " + enchPower);
        if (capacity < enchPower) {
            return Integer.MAX_VALUE;
        }
        return enchPower;
    }

    /*@Unique
    private static boolean horseArmorCheck (Item item, Enchantment enchantment) {
        return FixedMinecraftEnchantmentHelper.horseArmorCheck(enchantment, item);
    }*/


    /*@Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMinPower(I)I"))
    private static int checkEnchantmentCapacity(Enchantment enchantment, int level, @Local(argsOnly = true) ItemStack itemStack) {

    }*/

    /*@Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxPower(I)I"))
    private static int bypassCheck(Enchantment enchantment, int level, @Local(argsOnly = true) int power) {
        return power;
    }*/

    /*@ModifyExpressionValue(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private static boolean horseArmorCheck (boolean original, @Local() Item item, @Local Enchantment enchantment) {
        return FixedMinecraftEnchantmentHelper.horseArmorCheck(enchantment, item);
    }*/

    @Inject(method = "generateEnchantments", at = @At("HEAD"))
    private static void saveOriginalLevelArgument(Random random, ItemStack stack, int level,
                                                  Stream<RegistryEntry<Enchantment>> possibleEnchantments,
                                                  CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir,
                                                  @Share("lvl") LocalIntRef levelReference) {
        levelReference.set(level);
    }

    @ModifyArg(method = "generateEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getPossibleEntries(ILnet/minecraft/item/ItemStack;Ljava/util/stream/Stream;)Ljava/util/List;"), index = 0)
    private static int ignoreArgumentManipulationShenanigans(int power, @Share("lvl") LocalIntRef levelReference) {
        return levelReference.get();
    }
}
