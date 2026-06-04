package net.greenjab.fixedminecraft.registry.registries;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ItemGroupRegistry {

    public static final CreativeModeTab FIXED = FabricCreativeModeTab.builder().title(Component.translatable("itemgroup.fixedminecraft"))
            .icon( () -> new ItemStack(ItemRegistry.NETHERITE_ANVIL))
            .displayItems(
                     (_, entries) -> {
                        entries.accept(ItemRegistry.DRAGON_FIREWORK_ROCKET);
                        entries.accept(ItemRegistry.MAP_BOOK);
                        entries.accept(ItemRegistry.CHAINMAIL_HORSE_ARMOR);

                        entries.accept(ItemRegistry.BROKEN_TOTEM);
                        entries.accept(ItemRegistry.ECHO_TOTEM);
                        entries.accept(ItemRegistry.ECHO_FRUIT);

                        entries.accept(ItemRegistry.NETHERITE_ANVIL);
                        entries.accept(ItemRegistry.CHIPPED_NETHERITE_ANVIL);
                        entries.accept(ItemRegistry.DAMAGED_NETHERITE_ANVIL);

                        entries.accept(ItemRegistry.COPPER_RAIL);
                        entries.accept(ItemRegistry.EXPOSED_COPPER_RAIL);
                        entries.accept(ItemRegistry.WEATHERED_COPPER_RAIL);
                        entries.accept(ItemRegistry.OXIDIZED_COPPER_RAIL);
                        entries.accept(ItemRegistry.WAXED_COPPER_RAIL);
                        entries.accept(ItemRegistry.WAXED_EXPOSED_COPPER_RAIL);
                        entries.accept(ItemRegistry.WAXED_WEATHERED_COPPER_RAIL);
                        entries.accept(ItemRegistry.WAXED_OXIDIZED_COPPER_RAIL);
                        entries.accept(ItemRegistry.PATINA);
                        entries.accept(ItemRegistry.REDSTONE_LANTERN);


                         entries.accept(BlockRegistry.AZALEA_PLANKS);
                         entries.accept(BlockRegistry.AZALEA_LOG);
                         entries.accept(BlockRegistry.STRIPPED_AZALEA_LOG);
                         entries.accept(BlockRegistry.AZALEA_WOOD);
                         entries.accept(BlockRegistry.STRIPPED_AZALEA_WOOD);
                         entries.accept(ItemRegistry.AZALEA_SIGN);
                         entries.accept(ItemRegistry.AZALEA_HANGING_SIGN);
                         entries.accept(BlockRegistry.AZALEA_PRESSURE_PLATE);
                         entries.accept(BlockRegistry.AZALEA_TRAPDOOR);
                         entries.accept(BlockRegistry.AZALEA_BUTTON);
                         entries.accept(BlockRegistry.AZALEA_STAIRS);
                         entries.accept(BlockRegistry.AZALEA_SLAB);
                         entries.accept(BlockRegistry.AZALEA_FENCE_GATE);
                         entries.accept(BlockRegistry.AZALEA_FENCE);
                         entries.accept(BlockRegistry.AZALEA_DOOR);
                         entries.accept(ItemRegistry.AZALEA_BOAT);
                         entries.accept(ItemRegistry.AZALEA_CHEST_BOAT);
                         entries.accept(ItemRegistry.AZALEA_SHELF);

                         entries.accept(ItemRegistry.DISPENSER_MINECART);


                         entries.accept(ItemRegistry.SPEAR);
                         entries.accept(ItemRegistry.NAUTILUS_ARMOR);
                    }).build();


    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, "fixed", FIXED);
    }
}
