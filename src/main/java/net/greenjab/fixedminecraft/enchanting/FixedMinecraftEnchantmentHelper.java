package net.greenjab.fixedminecraft.enchanting;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public class FixedMinecraftEnchantmentHelper {
    public static int getEnchantmentPower(Enchantment enchantment, int level) {
        return (int) Math.round(Math.max(1D, enchantmentPowerFunction(enchantment, level)));
    }

    private static double enchantmentPowerFunction(Enchantment enchantment, int level) {
        return (enchantment.isTreasure() ? 1.5 : 1) * 20 * Math.pow((double) level / enchantment.getMaxLevel(), 2);
    }

    public static int getMaximumEnchantmentPower(ItemStack itemStack) {
        // itemStack.getItem().asItem().asItem().asItem().asItem().getEnchantability();
        // PickaxeItem
        return 30;
    }
}
