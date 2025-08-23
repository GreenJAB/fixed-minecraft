package net.greenjab.fixedminecraft.registry.registries;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class ItemGroupRegistry {

    public static final ItemGroup FIXED = FabricItemGroup.builder().displayName(Text.translatable("itemgroup.fixed"))
            .icon( () -> new ItemStack(ItemRegistry.DRAGON_FIREWORK_ROCKET))
            .entries(
                     (displayContext, entries) -> {
                        entries.add(ItemRegistry.DRAGON_FIREWORK_ROCKET);
                        entries.add(ItemRegistry.MAP_BOOK);
                        entries.add(ItemRegistry.CHAINMAIL_HORSE_ARMOR);
                        entries.add(ItemRegistry.NETHERITE_HORSE_ARMOR);

                        entries.add(ItemRegistry.BROKEN_TOTEM);
                        entries.add(ItemRegistry.ECHO_TOTEM);
                        entries.add(ItemRegistry.ECHO_FRUIT);

                        entries.add(ItemRegistry.NETHERITE_ANVIL);
                        entries.add(ItemRegistry.CHIPPED_NETHERITE_ANVIL);
                        entries.add(ItemRegistry.DAMAGED_NETHERITE_ANVIL);

                        entries.add(ItemRegistry.COPPER_RAIL);
                        entries.add(ItemRegistry.EXPOSED_COPPER_RAIL);
                        entries.add(ItemRegistry.WEATHERED_COPPER_RAIL);
                        entries.add(ItemRegistry.OXIDIZED_COPPER_RAIL);
                        entries.add(ItemRegistry.WAXED_COPPER_RAIL);
                        entries.add(ItemRegistry.WAXED_EXPOSED_COPPER_RAIL);
                        entries.add(ItemRegistry.WAXED_WEATHERED_COPPER_RAIL);
                        entries.add(ItemRegistry.WAXED_OXIDIZED_COPPER_RAIL);
                        entries.add(ItemRegistry.PATINA);
                        entries.add(ItemRegistry.REDSTONE_LANTERN);


                         entries.add(BlockRegistry.AZALEA_PLANKS);
                         entries.add(BlockRegistry.AZALEA_LOG);
                         entries.add(BlockRegistry.STRIPPED_AZALEA_LOG);
                         entries.add(BlockRegistry.AZALEA_WOOD);
                         entries.add(BlockRegistry.STRIPPED_AZALEA_WOOD);
                         entries.add(ItemRegistry.AZALEA_SIGN);
                         entries.add(ItemRegistry.AZALEA_HANGING_SIGN);
                         entries.add(BlockRegistry.AZALEA_PRESSURE_PLATE);
                         entries.add(BlockRegistry.AZALEA_TRAPDOOR);
                         entries.add(BlockRegistry.AZALEA_BUTTON);
                         entries.add(BlockRegistry.AZALEA_STAIRS);
                         entries.add(BlockRegistry.AZALEA_SLAB);
                         entries.add(BlockRegistry.AZALEA_FENCE_GATE);
                         entries.add(BlockRegistry.AZALEA_FENCE);
                         entries.add(BlockRegistry.AZALEA_DOOR);
                         entries.add(ItemRegistry.AZALEA_BOAT);
                         entries.add(ItemRegistry.AZALEA_CHEST_BOAT);
                    }).build();


    public static void register() {
        Registry.register(Registries.ITEM_GROUP, "fixed", FIXED);
    }
}
