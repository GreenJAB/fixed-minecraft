package net.greenjab.fixedminecraft.registry.registries;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.greenjab.fixedminecraft.registry.other.ExplorationCompassLootFunction;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.predicates.entity.EntityTypePredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jspecify.annotations.NonNull;
import static net.greenjab.fixedminecraft.registry.ModTags.*;

public class LootTableAdditions {

    public static void registerLootTableAdds() {
        System.out.println("register LootTableAdds");

        LootTableEvents.MODIFY.register((key, tableBuilder, _, holder) -> {
            HolderLookup.RegistryLookup<Enchantment> enchantments = holder.lookupOrThrow(Registries.ENCHANTMENT);

            if (key==BuiltInLootTables.ABANDONED_MINESHAFT) {
                tableBuilder.pool(bookPool(enchantments, ABANDONED_MINESHAFT_EBOOKS).build());
            } else if (key==BuiltInLootTables.ANCIENT_CITY) {
                tableBuilder.pool(bookPool(enchantments, ANCIENT_CITY_EBOOKS).build());
                tableBuilder.pool(LootPool.lootPool().add(LootItem.lootTableItem(Items.AIR).setWeight(5))
                        .add(enchantedArmor(enchantments, Items.DIAMOND_HELMET))
                        .add(enchantedArmor(enchantments, Items.DIAMOND_CHESTPLATE))
                        .add(enchantedArmor(enchantments, Items.DIAMOND_BOOTS))
                        .build());
                tableBuilder.pool(LootPool.lootPool().add(LootItem.lootTableItem(Items.ECHO_SHARD)).build());
            } else if (key==BuiltInLootTables.BASTION_TREASURE) {
                tableBuilder.pool(bookPoolPlus(enchantments, BASTION_TREASURE_EBOOKS, 1, 20).build());
            } else if (key==BuiltInLootTables.BURIED_TREASURE) {
                tableBuilder.pool(bookPoolPlus(enchantments, BURIED_TREASURE_EBOOKS, 3, 20).build());
            } else if (key==BuiltInLootTables.DESERT_PYRAMID) {
                tableBuilder.pool(bookPool(enchantments, DESERT_PYRAMID_EBOOKS).build());
            } else if (key==BuiltInLootTables.END_CITY_TREASURE) {
                tableBuilder.pool(bookPoolPlus(enchantments, END_CITY_TREASURE_EBOOKS, 1, 30).build());
            } else if (key==BuiltInLootTables.IGLOO_CHEST) {
                tableBuilder.pool(bookPoolPlus(enchantments, IGLOO_CHEST_EBOOKS, 3, 15).build());
            } else if (key==BuiltInLootTables.JUNGLE_TEMPLE) {
                tableBuilder.pool(bookPool2(enchantments, JUNGLE_TEMPLE_EBOOKS).build());
            } else if (key==BuiltInLootTables.NETHER_BRIDGE) {
                tableBuilder.pool(bookPoolPlus(enchantments, NETHER_BRIDGE_EBOOKS, 1, 15).build());
            } else if (key==BuiltInLootTables.PILLAGER_OUTPOST) {
                tableBuilder.pool(bookPool2(enchantments, PILLAGER_OUTPOST_EBOOKS).build());
            } else if (key==BuiltInLootTables.RUINED_PORTAL) {
                tableBuilder.pool(bookPoolPlus(enchantments, RUINED_PORTAL_EBOOKS, 10).build());
            } else if (key==BuiltInLootTables.SHIPWRECK_TREASURE) {
                tableBuilder.pool(bookPoolPlus(enchantments, SHIPWRECK_TREASURE_EBOOKS, 15).build());
            } else if (key==BuiltInLootTables.SIMPLE_DUNGEON) {
                tableBuilder.pool(bookPool2(enchantments, SIMPLE_DUNGEON_EBOOKS).build());
            } else if (key==BuiltInLootTables.STRONGHOLD_LIBRARY) {
                tableBuilder.pool(bookPool(enchantments, STRONGHOLD_LIBRARY_EBOOKS).build());
            } else if (key==BuiltInLootTables.UNDERWATER_RUIN_BIG) {
                tableBuilder.pool(bookPool2(enchantments, UNDERWATER_RUIN_BIG_EBOOKS).build());
            } else if (key==BuiltInLootTables.WOODLAND_MANSION) {
                tableBuilder.pool(bookPool(enchantments, WOODLAND_MANSION_EBOOKS).build());

            } else if (key==BuiltInLootTables.FISHING_TREASURE) {
                tableBuilder.modifyPools(builder -> builder
                        .add(LootItem.lootTableItem(Items.BOOK).setWeight(1)
                                .apply(new EnchantRandomlyFunction.Builder().withOneOf(enchantments.getOrThrow(FISHING_TREASURE_EBOOKS)))));
            } else if (key==BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE) {
                tableBuilder.modifyPools(builder -> builder
                        .add(LootItem.lootTableItem(Items.BOOK).setWeight(4)
                                .apply(new EnchantRandomlyFunction.Builder().withOneOf(enchantments.getOrThrow(TRAIL_RUINS_EBOOKS))))
                        .add(LootItem.lootTableItem(Items.BOOK).setWeight(2)
                                .apply(new EnchantWithLevelsFunction.Builder(ConstantValue.exactly(20))
                                        .withOptions(enchantments.get(EnchantmentTags.ON_RANDOM_LOOT).map(named -> named)))));
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, _, _) -> {
            if (key==BuiltInLootTables.SIMPLE_DUNGEON) {
                tableBuilder.pool(LootPool.lootPool().add(LootItem.lootTableItem(Items.AIR).setWeight(2))
                        .add(LootItem.lootTableItem(Items.MAP)
                                .apply(new ExplorationMapFunction.Builder().setDestination(StructureTags.ON_TRIAL_CHAMBERS_MAPS)
                                        .setMapDecoration(MapDecorationTypes.TRIAL_CHAMBERS).setSkipKnownStructures(false).setZoom((byte)2))
                                .apply(SetNameFunction.setName(Component.translatable("filled_map.trial_chambers"), SetNameFunction.Target.ITEM_NAME))).build());
            } else if (key==BuiltInLootTables.PILLAGER_OUTPOST) {
                tableBuilder.pool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.MAP).setWeight(5)
                                .apply(new ExplorationMapFunction.Builder().setDestination(StructureTags.ON_WOODLAND_EXPLORER_MAPS)
                                        .setMapDecoration(MapDecorationTypes.WOODLAND_MANSION).setSkipKnownStructures(false).setZoom((byte)2))
                                .apply(SetNameFunction.setName(Component.translatable("filled_map.mansion"), SetNameFunction.Target.ITEM_NAME)))
                        .add(LootItem.lootTableItem(Items.MAP)
                                .apply(new ExplorationMapFunction.Builder().setDestination(StructureTags.ON_DESERT_VILLAGE_MAPS)
                                        .setMapDecoration(MapDecorationTypes.DESERT_VILLAGE).setSkipKnownStructures(false).setZoom((byte)2))
                                .apply(SetNameFunction.setName(Component.translatable("filled_map.village_desert"), SetNameFunction.Target.ITEM_NAME)))
                        .add(LootItem.lootTableItem(Items.MAP)
                                .apply(new ExplorationMapFunction.Builder().setDestination(StructureTags.ON_PLAINS_VILLAGE_MAPS)
                                        .setMapDecoration(MapDecorationTypes.PLAINS_VILLAGE).setSkipKnownStructures(false).setZoom((byte)2))
                                .apply(SetNameFunction.setName(Component.translatable("filled_map.village_plains"), SetNameFunction.Target.ITEM_NAME)))
                        .add(LootItem.lootTableItem(Items.MAP)
                                .apply(new ExplorationMapFunction.Builder().setDestination(StructureTags.ON_SAVANNA_VILLAGE_MAPS)
                                        .setMapDecoration(MapDecorationTypes.SAVANNA_VILLAGE).setSkipKnownStructures(false).setZoom((byte)2))
                                .apply(SetNameFunction.setName(Component.translatable("filled_map.village_savanna"), SetNameFunction.Target.ITEM_NAME)))
                        .add(LootItem.lootTableItem(Items.MAP)
                                .apply(new ExplorationMapFunction.Builder().setDestination(StructureTags.ON_SNOWY_VILLAGE_MAPS)
                                        .setMapDecoration(MapDecorationTypes.SNOWY_VILLAGE).setSkipKnownStructures(false).setZoom((byte)2))
                                .apply(SetNameFunction.setName(Component.translatable("filled_map.village_snowy"), SetNameFunction.Target.ITEM_NAME)))
                        .add(LootItem.lootTableItem(Items.MAP)
                                .apply(new ExplorationMapFunction.Builder().setDestination(StructureTags.ON_TAIGA_VILLAGE_MAPS)
                                        .setMapDecoration(MapDecorationTypes.TAIGA_VILLAGE).setSkipKnownStructures(false).setZoom((byte)2))
                                .apply(SetNameFunction.setName(Component.translatable("filled_map.village_taiga"), SetNameFunction.Target.ITEM_NAME))).build());
            } else if (key==BuiltInLootTables.BURIED_TREASURE) {
                tableBuilder.pool(LootPool.lootPool().add(LootItem.lootTableItem(Items.MAP)
                        .apply(new ExplorationMapFunction.Builder().setDestination(StructureTags.ON_OCEAN_EXPLORER_MAPS)
                                .setMapDecoration(MapDecorationTypes.OCEAN_MONUMENT).setSkipKnownStructures(false).setZoom((byte)2))
                        .apply(SetNameFunction.setName(Component.translatable("filled_map.monument"), SetNameFunction.Target.ITEM_NAME))).build());
            } else if (key==BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE) {
                tableBuilder.modifyPools(builder ->
                        builder.add(LootItem.lootTableItem(Items.COMPASS).apply(new ExplorationCompassLootFunction.Builder())));
            } else if (key==BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY||key==BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY) {
                tableBuilder.modifyPools(builder ->
                        builder.add(LootItem.lootTableItem(Items.MAP).apply(new ExplorationMapFunction.Builder().setDestination(ModTags.ON_TRAIL_RUIN_MAPS).setMapDecoration(MapDecorationRegistry.TRAIL_RUINS))));
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, _, holder) -> {
            HolderLookup.RegistryLookup<Enchantment> enchantments = holder.lookupOrThrow(Registries.ENCHANTMENT);
            if (key==BuiltInLootTables.SIMPLE_DUNGEON) {
                tableBuilder.pool(LootPool.lootPool().add(LootItem.lootTableItem(Items.AIR).setWeight(16))
                        .add(enchantedArmor(enchantments, Items.LEATHER_HORSE_ARMOR, 20, 3))
                        .add(enchantedArmor(enchantments, ItemRegistry.CHAINMAIL_HORSE_ARMOR, 20, 3))
                        .add(enchantedArmor(enchantments, Items.COPPER_HORSE_ARMOR, 20, 3))
                        .add(enchantedArmor(enchantments, Items.IRON_HORSE_ARMOR, 20, 2))
                        .add(enchantedArmor(enchantments, Items.GOLDEN_HORSE_ARMOR, 20, 2))
                        .add(enchantedArmor(enchantments, Items.DIAMOND_HORSE_ARMOR, 20, 1))
                        .build());
            } else if (key==BuiltInLootTables.DESERT_PYRAMID) {
                tableBuilder.pool(LootPool.lootPool().add(LootItem.lootTableItem(Items.AIR).setWeight(25))
                        .add(enchantedArmor(enchantments, Items.LEATHER_HORSE_ARMOR, 10, 3))
                        .add(enchantedArmor(enchantments, ItemRegistry.CHAINMAIL_HORSE_ARMOR, 10, 3))
                        .add(enchantedArmor(enchantments, Items.COPPER_HORSE_ARMOR, 10, 3))
                        .add(enchantedArmor(enchantments, Items.IRON_HORSE_ARMOR, 10, 2))
                        .add(enchantedArmor(enchantments, Items.GOLDEN_HORSE_ARMOR, 10, 2))
                        .add(enchantedArmor(enchantments, Items.DIAMOND_HORSE_ARMOR, 10, 1))
                        .build());
            } else if (key==BuiltInLootTables.END_CITY_TREASURE) {
                tableBuilder.pool(LootPool.lootPool().add(LootItem.lootTableItem(Items.AIR).setWeight(8))
                        .add(enchantedArmor(enchantments, Items.IRON_HORSE_ARMOR, 30, 1))
                        .add(enchantedArmor(enchantments, Items.GOLDEN_HORSE_ARMOR, 30, 1))
                        .add(enchantedArmor(enchantments, Items.DIAMOND_HORSE_ARMOR, 30, 2))
                        .build());
            } else if (key==BuiltInLootTables.ANCIENT_CITY) {
                tableBuilder.pool(LootPool.lootPool().add(LootItem.lootTableItem(Items.AIR).setWeight(4))
                        .add(enchantedArmor(enchantments, Items.DIAMOND_HORSE_ARMOR, 30, 1))
                        .build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, _, holder) -> {
            if (key==BuiltInLootTables.CHARGED_CREEPER) {
                tableBuilder.pool(LootPool.lootPool().add(NestedLootTable.lootTableReference(LootTableRegistry.CHARGED_CREEPER_PLAYER_LOOT_TABLE).when(LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(holder.lookupOrThrow(Registries.ENTITY_TYPE), EntityTypes.PLAYER))))).build());

                tableBuilder.pool(LootPool.lootPool().add(NestedLootTable.lootTableReference(LootTableRegistry.CHARGED_CREEPER_ZOMBIE_TABLE).when(LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(holder.lookupOrThrow(Registries.ENTITY_TYPE), EntityTypes.DROWNED))))).build());
                tableBuilder.pool(LootPool.lootPool().add(NestedLootTable.lootTableReference(LootTableRegistry.CHARGED_CREEPER_ZOMBIE_TABLE).when(LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(holder.lookupOrThrow(Registries.ENTITY_TYPE), EntityTypes.HUSK))))).build());

                tableBuilder.pool(LootPool.lootPool().add(NestedLootTable.lootTableReference(LootTableRegistry.CHARGED_CREEPER_SKELETON_TABLE).when(LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(holder.lookupOrThrow(Registries.ENTITY_TYPE), EntityTypes.BOGGED))))).build());
                tableBuilder.pool(LootPool.lootPool().add(NestedLootTable.lootTableReference(LootTableRegistry.CHARGED_CREEPER_SKELETON_TABLE).when(LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(holder.lookupOrThrow(Registries.ENTITY_TYPE), EntityTypes.STRAY))))).build());
                tableBuilder.pool(LootPool.lootPool().add(NestedLootTable.lootTableReference(LootTableRegistry.CHARGED_CREEPER_SKELETON_TABLE).when(LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(holder.lookupOrThrow(Registries.ENTITY_TYPE), EntityTypes.PARCHED))))).build());
            } else if (key == EntityTypes.CREEPER.getDefaultLootTable().get()) {
                tableBuilder.pool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_PIGSTEP))
                        .when(LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity().of(holder.lookupOrThrow(Registries.ENTITY_TYPE), EntityTypes.PIGLIN))).build());
                tableBuilder.pool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_OTHERSIDE))
                        .when(LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().located(LocationPredicate.Builder.inDimension(Level.END)))).build());
            } else if (key==EntityTypes.SNIFFER.getDefaultLootTable().get()) {
                tableBuilder.pool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_RELIC))
                        .when(LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity().of(holder.lookupOrThrow(Registries.ENTITY_TYPE), EntityTypes.CREEPER))).build());
            } else if (key==BuiltInLootTables.SNIFFER_DIGGING) {
                tableBuilder.modifyPools(builder ->
                        builder.add(LootItem.lootTableItem(Items.GOLDEN_DANDELION))
                                .add(NestedLootTable.lootTableReference(LootTableRegistry.SNIFFER_EXTRA)));
            } else if (key==EntityTypes.WARDEN.getDefaultLootTable().get()) {
                tableBuilder.pool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_5)).build());
            } else if (key==BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_CONSUMABLES) {
                tableBuilder.pool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_CREATOR))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_CREATOR_MUSIC_BOX))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_PRECIPICE))
                        .add(LootItem.lootTableItem(Items.AIR).setWeight(3))
                        .build());
            } else if (key==EntityTypes.ELDER_GUARDIAN.getDefaultLootTable().get()) {
                tableBuilder.pool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE).setWeight(2))
                        .add(LootItem.lootTableItem(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.AIR))
                        .build());
            } else if (key== EntityTypes.GOAT.getDefaultLootTable().get()) {
                LootPool.Builder poolBuilder = LootPool.lootPool().add(NestedLootTable.lootTableReference(LootTableRegistry.GOAT_MUTTON));
                tableBuilder.pool(poolBuilder.build());
            }
        });
    }

    private static LootPoolSingletonContainer.@NonNull Builder<?> enchantedArmor(HolderLookup.RegistryLookup<Enchantment> enchantments, Item armor, int level, int weight) {
        return LootItem.lootTableItem(armor).setWeight(weight)
                .apply(new EnchantWithLevelsFunction.Builder(ConstantValue.exactly(level))
                        .withOptions(enchantments.get(EnchantmentTags.ON_RANDOM_LOOT).map(named -> named)));
    }
    private static LootPoolSingletonContainer.@NonNull Builder<?> enchantedArmor(HolderLookup.RegistryLookup<Enchantment> enchantments, Item armor) {
        return enchantedArmor(enchantments, armor, 30, 1);
    }

    private static LootPool.Builder bookPoolPlus(HolderLookup.RegistryLookup<Enchantment> enchantments, TagKey<Enchantment> tag, int level){
        return bookPoolPlus(enchantments, tag, 2, level);
    }

    private static LootPool.Builder bookPoolPlus(HolderLookup.RegistryLookup<Enchantment> enchantments, TagKey<Enchantment> tag, int rolls, int level){
        return bookPool(enchantments, tag, rolls).add(LootItem.lootTableItem(Items.BOOK).setWeight(1)
                .apply(new EnchantWithLevelsFunction.Builder(ConstantValue.exactly(level))
                        .withOptions(enchantments.get(EnchantmentTags.ON_RANDOM_LOOT).map( named -> named))));
    }

    private static LootPool.Builder bookPool(HolderLookup.RegistryLookup<Enchantment> enchantments, TagKey<Enchantment> tag){
        return bookPool(enchantments, tag, 1);
    }

    private static LootPool.Builder bookPool2(HolderLookup.RegistryLookup<Enchantment> enchantments, TagKey<Enchantment> tag){
        return bookPool(enchantments, tag, 2);
    }

    private static LootPool.Builder bookPool(HolderLookup.RegistryLookup<Enchantment> enchantments, TagKey<Enchantment> tag, int rolls){
        return LootPool.lootPool().setRolls(ConstantValue.exactly(rolls))
                .add(LootItem.lootTableItem(Items.BOOK)
                        .apply(new EnchantRandomlyFunction.Builder().withOneOf(enchantments.getOrThrow(tag))).setWeight(1))
                .add(LootItem.lootTableItem(Items.AIR).setWeight(1));
    }

}
