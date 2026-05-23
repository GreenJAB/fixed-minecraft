package net.greenjab.fixedminecraft.mixin.enchanting;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "getAvailableEnchantmentResults", at = @At(value = "HEAD"), cancellable = true)
    private static void checkEnchantmentCapacity1(int value, ItemStack itemStack, Stream<Holder<Enchantment>> source,
                                                  CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        boolean bl = itemStack.is(Items.BOOK);
        source.filter(enchantment -> (enchantment.value()).isPrimaryItem(itemStack) || bl)
                .forEach( enchantmentx -> {
                    Enchantment enchantment = enchantmentx.value();
                    for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
                        int enchPower = FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantmentx, j);
                        if (value >= enchPower) {
                            list.add(new EnchantmentInstance(enchantmentx, j));
                            break;
                        }
                    }
                });
        cir.setReturnValue(list);
        cir.cancel();
    }

    @Inject(method = "selectEnchantment", at = @At("HEAD"))
    private static void saveOriginalLevelArgument(RandomSource random, ItemStack itemStack, int enchantmentCost,
                                                  Stream<Holder<Enchantment>> source,
                                                  CallbackInfoReturnable<List<EnchantmentInstance>> cir,
                                                  @Share("lvl") LocalIntRef levelReference) {
        levelReference.set(enchantmentCost);
    }

    @ModifyArg(method = "selectEnchantment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getAvailableEnchantmentResults(ILnet/minecraft/world/item/ItemStack;Ljava/util/stream/Stream;)Ljava/util/List;"), index = 0)
    private static int ignoreArgumentManipulationShenanigans(int power, @Share("lvl") LocalIntRef levelReference) {
        return levelReference.get();
    }

    @Inject(method = "getDamageProtection", at = @At("RETURN"), cancellable = true)
    private static void chainmailProtection(ServerLevel serverLevel, LivingEntity victim, DamageSource source,
                                            CallbackInfoReturnable<Float> cir) {
        float i = cir.getReturnValueF();
        if (victim instanceof AbstractHorse) i*=2;
        if (source.is(DamageTypeTags.IS_FIRE)) return;
        if (source.is(DamageTypeTags.IS_FALL)) return;
        if (source.is(DamageTypeTags.IS_EXPLOSION)) return;
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return;
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
        for (ItemStack equipment : FixedMinecraft.getArmor(victim)) {
            if (equipment.getItem().toString().toLowerCase().contains("chainmail")) i++;
        }
        cir.setReturnValue(i);
    }
}
