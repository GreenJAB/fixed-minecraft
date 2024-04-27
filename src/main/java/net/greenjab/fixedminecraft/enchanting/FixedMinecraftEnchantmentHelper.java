package net.greenjab.fixedminecraft.enchanting;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

public class FixedMinecraftEnchantmentHelper {

    // please rename lol
    public static final int POWER_WHEN_MAX_LEVEL = 20;

    public static int getEnchantmentPower(Enchantment enchantment, int level) {
        return (int) Math.round(enchantment.isCursed() ?
                Math.min(-1, curseEnchantmentPowerFunction(enchantment, level)) : Math.max(1, enchantmentPowerFunction(enchantment, level)));
    }

    private static double enchantmentPowerFunction(Enchantment enchantment, int level) {
        // 10 * ()^1.6 oder 20 * ()^2
        return (enchantment.isTreasure() ? 1.5 : 1) * POWER_WHEN_MAX_LEVEL * Math.pow((double) level / enchantment.getMaxLevel(), 2);
    }

    private static double curseEnchantmentPowerFunction(Enchantment enchantment, int level) {
        return (-10) * Math.pow((double) level / enchantment.getMaxLevel(), 1.6);
    }

    public static int getEnchantmentCapacity(ItemStack itemStack) {
        // check material
        // probably type
        // ...other things
        return 10;
        // 35 -> one max lvl and one lower level enchantment with 20 being the max enchantment power or
        //    3 max level and one mid-tier enchantment with 10 being the max enchantment power
    }

    public static int getOccupiedEnchantmentCapacity(ItemStack itemStack) {
        int power = 0;
        Map<Enchantment, Integer> enchantmentLevelsMap = EnchantmentHelper.get(itemStack);

        System.out.println(enchantmentLevelsMap);

        for (Enchantment enchantment : enchantmentLevelsMap.keySet()) {
            int add = FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantment, enchantmentLevelsMap.get(enchantment));

            System.out.println("enchantment: " + enchantment + " with level: " + enchantmentLevelsMap.get(enchantment) + " has " + add + " ench power");

            power += add;
        }
        return power;
    }

    @Deprecated
    public static List<List<EnchantmentLevelEntry>> createUniqueOptions(int count) {
        return null;
    }
}
