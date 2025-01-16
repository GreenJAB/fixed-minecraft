package net.greenjab.fixedminecraft.registry

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry
import net.minecraft.loot.LootTable
import net.minecraft.loot.LootTables
import net.minecraft.registry.RegistryKey
import net.minecraft.world.GameRules

object LoottableRegistry {
    var FISHING_FISH: RegistryKey<LootTable> = LootTables.register("gameplay/fixed_fishing/fish")
    var FISHING_JUNK: RegistryKey<LootTable> = LootTables.register("gameplay/fixed_fishing/junk")
    var FISHING_MID: RegistryKey<LootTable> = LootTables.register("gameplay/fixed_fishing/mid")
    var FISHING_TREASURE: RegistryKey<LootTable> = LootTables.register("gameplay/fixed_fishing/treasure")

    fun register() {
    }
}
