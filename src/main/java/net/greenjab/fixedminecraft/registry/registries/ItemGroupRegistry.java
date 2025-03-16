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
                    }).build();


    public static void register() {
        Registry.register(Registries.ITEM_GROUP, "fixed", FIXED);
    }
}
