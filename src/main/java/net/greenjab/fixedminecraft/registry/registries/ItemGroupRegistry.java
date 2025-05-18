package net.greenjab.fixedminecraft.registry.registries;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.loot.LootTables;

import static net.minecraft.loot.LootDataType.LOOT_TABLES;

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
                    }).build();

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, "fixed", FIXED);

        String[] tables = {"fish", "junk", "mid", "treasure"};
        Identifier lootTableId = FixedMinecraft.id("gameplay/fixed_fishing/" + tables[0]);
        LootTables.LOOT_TABLES.add(RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId));

    }
}
