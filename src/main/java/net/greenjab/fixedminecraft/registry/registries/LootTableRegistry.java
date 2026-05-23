package net.greenjab.fixedminecraft.registry.registries;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.other.ExplorationCompassLootFunction;
import net.greenjab.fixedminecraft.registry.other.WanderingTraderSpecialLootFunction;
import net.greenjab.fixedminecraft.registry.other.LibrarianBookLootFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootTableRegistry {

    public static final ResourceKey<LootTable> SWAMP_HUT = registerLoot_Table("chests/swamp_hut");
    public static final ResourceKey<LootTable> SUPER_CHARGED_CREEPER_PLAYER_LOOT_TABLE = registerLoot_Table("gameplay/other/super_charged_creeper_player");
    private static ResourceKey<LootTable> registerLoot_Table(String id) {
        return registerLootTable(ResourceKey.create(Registries.LOOT_TABLE, FixedMinecraft.id(id)));
    }
    private static ResourceKey<LootTable> registerLootTable(ResourceKey<LootTable> key) {
        if (BuiltInLootTables.LOCATIONS.add(key)) {
            return key;
        } else {
            throw new IllegalArgumentException(key.identifier() + " is already a registered built-in loot table");
        }
    }

    public static void registerLootTable() {
        System.out.println("register LootTables");
        Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, FixedMinecraft.id("exploration_compass"), ExplorationCompassLootFunction.CODEC);
        Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, FixedMinecraft.id("wandering_trader_special"), WanderingTraderSpecialLootFunction.CODEC);
        Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, FixedMinecraft.id("librarian_book"), LibrarianBookLootFunction.CODEC);
    }
}
