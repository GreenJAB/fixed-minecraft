package net.greenjab.fixedminecraft.mixin.enchanting;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.AbstractList;
import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {


    @Inject(method = "getPossibleEntries", at = @At(value = "HEAD"), cancellable = true)
    private static void checkEnchantmentCapacity1(int level, ItemStack stack, Stream<RegistryEntry<Enchantment>> possibleEnchantments,
                                                  CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        List<EnchantmentLevelEntry> list = Lists.newArrayList();
        boolean bl = stack.isOf(Items.BOOK);
        possibleEnchantments.filter(/* method_60143 */ enchantment -> (enchantment.value()).isPrimaryItem(stack) || bl)
                .forEach(/* method_60106 */ enchantmentx -> {
                    Enchantment enchantment = enchantmentx.value();
                    for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
                        int enchPower = FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantmentx, j);
                        if (level >= enchPower) {
                            list.add(new EnchantmentLevelEntry(enchantmentx, j));
                            break;
                        }
                    }
                });
        cir.setReturnValue(list);
        cir.cancel();
    }

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

    @Inject(method = "getProtectionAmount", at = @At("RETURN"), cancellable = true)
    private static void chainmailProtection(ServerWorld world, LivingEntity user, DamageSource damageSource,
                                            CallbackInfoReturnable<Float> cir) {
        float i = cir.getReturnValueF();
        if (user instanceof AbstractHorseEntity) i*=2;
        if (damageSource.isIn(DamageTypeTags.IS_FIRE)) return;
        if (damageSource.isIn(DamageTypeTags.IS_FALL)) return;
        if (damageSource.isIn(DamageTypeTags.IS_EXPLOSION)) return;
        if (damageSource.isIn(DamageTypeTags.IS_PROJECTILE)) return;
        if (damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
        for (ItemStack equipment : user.getArmorItems()) {
            if (equipment.getItem().getName().getString().toLowerCase().contains("chain")) i++;
        }
        cir.setReturnValue(i);
    }
}
