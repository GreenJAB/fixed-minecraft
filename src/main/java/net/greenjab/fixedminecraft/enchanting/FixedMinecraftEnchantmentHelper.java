package net.greenjab.fixedminecraft.enchanting;

import net.minecraft.block.Block;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        // TODO
        // check material
        // probably type
        // ...other things
        return 40;
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

    /**
     * Counts nearby bookshelves; turned vanilla code snippet into dedicated method
     * @param world The world the enchanting table is in
     * @param enchantingTablePosition Its position as a net.minecraft.util.math.BlockPos
     * @return Bookshelf-count capped to 15
     */
    public static int countAccessibleBookshelves(World world, BlockPos enchantingTablePosition) {
        int bookShelfCount = 0;
        for(BlockPos p : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
            if (EnchantingTableBlock.canAccessPowerProvider(world, enchantingTablePosition, p)) {
                bookShelfCount++;
            }
        }
        // System.out.println("bookshelf count before math.min: " + bookShelfCount);
        bookShelfCount = Math.min(bookShelfCount, 15);
        // System.out.println("bookshelf count: " + bookShelfCount);
        return bookShelfCount;
    }

    /**
     * Similar to net.minecraft.block.EnchantingTableBlock.canAccessPowerProvider(World w, BlockPos tablePos, BlockPos providerOffset).
     * Checks whether the enchanting table at the given position can access the given block.
     * @param world The world the enchanting table is in
     * @param enchantingTablePosition It's position as a net.minecraft.util.math.BlockPos
     * @param providerOffset Given provider offset from the enchanting table's position
     * @param block net.minecraft.block.Block to look for
     * @return true if block matches and can be accessed, false otherwise
     */
    public static boolean canAccessBlock(World world, BlockPos enchantingTablePosition, BlockPos providerOffset, Block block) {
        return world.getBlockState(enchantingTablePosition.add(providerOffset)).isOf(block)
               && world.getBlockState(enchantingTablePosition.add(providerOffset.getX() / 2, providerOffset.getY(), providerOffset.getZ() / 2))
                       .isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }
}
