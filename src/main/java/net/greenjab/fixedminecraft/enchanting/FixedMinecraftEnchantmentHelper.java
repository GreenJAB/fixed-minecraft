package net.greenjab.fixedminecraft.enchanting;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.block.Block;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.random.Random;

import java.util.*;
import java.util.stream.Stream;

public class FixedMinecraftEnchantmentHelper {

    public static final int POWER_WHEN_MAX_LEVEL = 12;

    public static int getEnchantmentPower(RegistryEntry<Enchantment> enchantment, int level) {
        return (int) Math.ceil(enchantment.isIn(EnchantmentTags.CURSE) ?
                Math.min(-1, curseEnchantmentPowerFunction(enchantment.value(), level)) : Math.max(1, enchantmentPowerFunction(enchantment, level)));
    }

    private static double enchantmentPowerFunction(RegistryEntry<Enchantment> enchantment, int level) {
        // 10 * ()^1.6 oder 20 * ()^2

        return (enchantment.isIn(EnchantmentTags.TREASURE) ? 1.5 : 1) * (POWER_WHEN_MAX_LEVEL+enchantment.value().getMaxLevel()-5) * Math.pow((double) level / enchantment.value().getMaxLevel(), 2);
    }

    private static double curseEnchantmentPowerFunction(Enchantment enchantment, int level) {
        return (-5) * Math.pow((double) level / enchantment.getMaxLevel(), 1.6);
    }

    public static int getEnchantmentCapacity(ItemStack itemStack) {
        if (itemStack.isOf(Items.ENCHANTED_BOOK)||itemStack.isOf(Items.BOOK)) return 50;


        //DynamicRegistryManager dynamicRegistryManager = itemStack.getWorld().getRegistryManager();

        List<EnchantmentLevelEntry> list = getPossibleEntries(100, itemStack);
        int power = 0;
        //System.out.println(itemStack);
        for (EnchantmentLevelEntry enchantmentLevelEntry : list) {
            power += FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
            //System.out.println(enchantmentLevelEntry.enchantment.getIdAsString() + ", " + power);
        }
        boolean isGold = itemStack.isIn(ItemTags.PIGLIN_LOVED);
        return Math.min((int)Math.ceil(power*(isGold?0.75f:0.43f)), 50);
    }

    /*public static List<EnchantmentLevelEntry> getPossibleEntries(ItemStack stack) {
        List<EnchantmentLevelEntry> list = Lists.newArrayList();
        Item item = stack.getItem();
        Iterator<Enchantment> var6 = Registries.ENCHANTMENT.iterator();
            while(true) {
                Enchantment enchantment;
                do {
                    if (!var6.hasNext()) {
                        return list;
                    }

                    enchantment = var6.next();
                } while(!FixedMinecraftEnchantmentHelper.horseArmorCheck(enchantment, item));
                if (!enchantment.isCursed()) list.add(new EnchantmentLevelEntry(enchantment, enchantment.getMaxLevel()));
            }
    }*/

    public static List<EnchantmentLevelEntry> getPossibleEntries(int level, ItemStack stack) {
        List<EnchantmentLevelEntry> list = Lists.<EnchantmentLevelEntry>newArrayList();
        boolean bl = stack.isOf(Items.BOOK);

        World world =  Objects.requireNonNull(FixedMinecraft.INSTANCE.getSERVER()).getOverworld();

        Registry<Enchantment> optional = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        //Stream<RegistryEntry<Enchantment>> possibleEnchantments = (optional).stream();
        Iterator<Enchantment> e = optional.stream().iterator();
        while (e.hasNext()) {
            Enchantment enchantment = e.next();
            RegistryEntry<Enchantment> ench = optional.getEntry(enchantment);
            //System.out.println(ench.getIdAsString());
            if (!ench.isIn(EnchantmentTags.CURSE) && enchantment.isAcceptableItem(stack)) list.add(new EnchantmentLevelEntry(optional.getEntry(enchantment), enchantment.getMaxLevel()));

            /*for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
                System.out.println(enchantment.description() + ", " + enchantment.getMinPower(j) + ", " + enchantment.getMaxPower(j) + ", " + level);
                if (level >= enchantment.getMinPower(j) && level <= enchantment.getMaxPower(j)) {
                    list.add(new EnchantmentLevelEntry(optional.getEntry(enchantment), j));
                    break;
                }
            }*/
        }


        /*possibleEnchantments.filter( enchantment -> (enchantment.value()).isPrimaryItem(stack) || bl)
        //possibleEnchantments.filter( enchantment -> FixedMinecraftEnchantmentHelper.horseArmorCheck(enchantment, stack) || bl)
                .forEach( enchantmentx -> {
                    Enchantment enchantment = enchantmentx.value();

                    for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
                        if (level >= enchantment.getMinPower(j) && level <= enchantment.getMaxPower(j)) {
                            list.add(new EnchantmentLevelEntry(enchantmentx, j));
                            break;
                        }
                    }
                });*/
        return list;
    }

    public static int getOccupiedEnchantmentCapacity(ItemStack itemStack, boolean atLeast1) {
        int power = 0;
        ItemEnchantmentsComponent enchantmentLevelsMap = EnchantmentHelper.getEnchantments(itemStack);
        for (RegistryEntry<Enchantment> enchantment : enchantmentLevelsMap.getEnchantments()) {
            int add = FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantment, enchantmentLevelsMap.getLevel(enchantment));
            power += add;
        }
        boolean isGold = itemStack.isIn(ItemTags.PIGLIN_LOVED);
        return Math.max((int)Math.ceil(power*(isGold?0.5:1)),atLeast1?1:0);
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
        bookShelfCount = Math.min(bookShelfCount, 15);
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


    public static ItemStack applySuperEnchants(ItemStack IS, Random random) {
        if (!IS.isOf(Items.ENCHANTED_BOOK)) {
            ItemStack IS2 = IS.getItem().getDefaultStack();
            ItemEnchantmentsComponent map = EnchantmentHelper.getEnchantments(IS);

            boolean isSuper = false;
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(IS));
            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : map.getEnchantmentEntries()) {
                RegistryEntry<Enchantment> registryEntry = entry.getKey();
                Enchantment e = registryEntry.value();
                int i = entry.getIntValue();
                if (e.getMaxLevel() != 1) {
                    if (random.nextFloat() < 0.05f) {
                        i = e.getMaxLevel() + 1;
                        isSuper = true;
                    }
                }
                builder.set(registryEntry, i);

            }

            if (isSuper) {
               // System.out.println("super");
                //IS2.getOrCreateSubNbt("Super");
                IS2.set(DataComponentTypes.REPAIR_COST, 1);
                ItemEnchantmentsComponent outputEnchants = EnchantmentHelper.getEnchantments(IS2);
                for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : outputEnchants.getEnchantmentEntries()) {
                    RegistryEntry<Enchantment> registryEntry = entry.getKey();
                    if (registryEntry.equals(Enchantments.MENDING)) {
                        builder.set(registryEntry, 0);
                        //builder.remove(registryEntry);
                        //builder.remove(Enchantments.MENDING);
                    }
                }
                //map.remove(Enchantments.MENDING);
            }
                EnchantmentHelper.set(IS2, builder.build());
            //EnchantmentHelper.set(map, IS2);
            return IS2;
        } else {
            return IS;
        }
    }

    public static int enchantLevel(ItemStack stack, String name) {
        int level = 0;
        ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        for (RegistryEntry<Enchantment> e : stack.getEnchantments().getEnchantments()) {
            if (e.getIdAsString().toLowerCase().contains(name.toLowerCase())) {
                level += itemEnchantmentsComponent.getLevel(e);
            }
        }
        return level;
    }
}
