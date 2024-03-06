package net.green_jab.fixed_minecraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.green_jab.fixed_minecraft.FixedMinecraft;
import net.green_jab.fixed_minecraft.block.ModBlocks;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup FIXED = Registry.register(Registries.ITEM_GROUP,
            new Identifier(FixedMinecraft.MOD_ID,"fixed"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.fixed"))
                    .icon(() -> new ItemStack(ModItems.DRAGON_FIREWORK_ROCKET)).entries((displayContext, entries) -> {
                        //ModItemGroups.addFireworkRockets(entries, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
                        entries.add(ModItems.DRAGON_FIREWORK_ROCKET);
                        entries.add(ModItems.MAP_BOOK);
                        entries.add(ModBlocks.NETHERITE_ANVIL);
                        entries.add(ModBlocks.CHIPPED_NETHERITE_ANVIL);
                        entries.add(ModBlocks.DAMAGED_NETHERITE_ANVIL);
                    }).build());
    public static void registerItemGroups(){
        FixedMinecraft.LOGGER.info("Registering Item Groups for " + FixedMinecraft.MOD_ID);
    }

    private static void addFireworkRockets(ItemGroup.Entries entries, ItemGroup.StackVisibility visibility) {
        for (byte b : FireworkRocketItem.FLIGHT_VALUES) {
            ItemStack itemStack = new ItemStack(ModItems.DRAGON_FIREWORK_ROCKET);
            FireworkRocketItem.setFlight(itemStack, b);
            entries.add(itemStack, visibility);
        }
    }
}
