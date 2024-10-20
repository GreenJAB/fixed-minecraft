package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(EnchantWithLevelsLootFunction.class)
public class EnchantWithLevelsLootFunctionMixin {

    @Inject(method = "process", at = @At("RETURN"), cancellable = true)
    private void applySuperEnchant(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir, @Local Random random) {
        ItemStack IS = cir.getReturnValue();
        cir.setReturnValue(FixedMinecraftEnchantmentHelper.applySuperEnchants(IS, random));
        /*if (!IS.isOf(Items.ENCHANTED_BOOK)) {
            ItemStack IS2 = IS.getItem().getDefaultStack();
            Map<Enchantment, Integer> map = EnchantmentHelper.get(IS);
            Iterator iter = map.keySet().iterator();
            boolean isSuper = false;
            while (iter.hasNext()) {
                Enchantment e = (Enchantment) iter.next();
                int i = (Integer) map.get(e);
                if (e.getMaxLevel() != 1) {
                    if (random.nextFloat() < 1.03f) {
                        i = e.getMaxLevel() + 1;
                        isSuper = true;
                    }
                }
                map.put(e, i);
            }
            if (isSuper) {
                IS2.getOrCreateSubNbt("Super");
                map.remove(Enchantments.MENDING);
            }
            EnchantmentHelper.set(map, IS2);
            cir.setReturnValue(IS2);
        } else {
            cir.setReturnValue(IS);
        }*/
    }

}
