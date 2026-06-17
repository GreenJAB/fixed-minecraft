package net.greenjab.fixedminecraft.registry.registries;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.EntityTypePredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

public class LootTableAdditions {

    public static void registerLootTableAdds() {
        System.out.println("register LootTableAdds");

        LootTableEvents.MODIFY.register((key, tableBuilder, source, holder) -> {
            if (key==BuiltInLootTables.CHARGED_CREEPER) {
                LootItemCondition.Builder predicate = LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(holder.lookupOrThrow(Registries.ENTITY_TYPE), EntityType.PLAYER)));
                LootPool.Builder poolBuilder = LootPool.lootPool().add(NestedLootTable.lootTableReference(LootTableRegistry.SUPER_CHARGED_CREEPER_PLAYER_LOOT_TABLE).when(predicate));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        /*LootTableEvents.MODIFY.register((key, tableBuilder, source, holder) -> {
            HolderLookup.RegistryLookup<Enchantment> enchantments = holder.lookupOrThrow(Registries.ENCHANTMENT);
	      if (key==BuiltInLootTables.IGLOO_CHEST) {
	          LootPool.Builder pool = LootPool.lootPool()
                      .add(LootItem.lootTableItem(Items.BOOK).setWeight(1)
                              .apply(new EnchantWithLevelsFunction.Builder(UniformGenerator.between(5.0F, 19.0F))))
                      .add(LootItem.lootTableItem(Items.BOOK).setWeight(1)
                              .apply(new EnchantRandomlyFunction.Builder().withOneOf(enchantments.getOrThrow(ModTags.IGLOO_EBOOKS))))
                      .setRolls(UniformGenerator.between(1.0F, 2.0F));
	          tableBuilder.withPool(pool);
	      }
	  });*/
        //FabricLootTableBuilder.modifyPools

    }

    public static ResourceKey<PlacedFeature> of(String id) {
        return ResourceKey.create(Registries.PLACED_FEATURE, FixedMinecraft.id(id));
    }
}
