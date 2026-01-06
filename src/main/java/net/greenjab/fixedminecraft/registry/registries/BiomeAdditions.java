package net.greenjab.fixedminecraft.registry.registries;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.OrePlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;

public class BiomeAdditions {

    public static void registerBiomeAdds() {
        System.out.println("registerBiomeAdds");

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_EMERALD);
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_emerald"));

        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_ancient_debris_lava"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.BASALT_DELTAS), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_ancient_debris_extra"));

        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.IS_FROZEN), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_packed_ice"));
        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.IS_FROZEN), GenerationStep.Feature.VEGETAL_DECORATION, of("cave_snow"));

        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_OCEAN), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_copper_extra"));

        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_BADLANDS), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_terracotta"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_BADLANDS), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_gold_extra"));

        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.DESERT), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_lapis_extra"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.DESERT), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_sandstone"));

        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_coal_extra"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), GenerationStep.Feature.VEGETAL_DECORATION, of("vines_underground_extra"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), GenerationStep.Feature.VEGETAL_DECORATION, of("vines_underground_extra2"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), GenerationStep.Feature.VEGETAL_DECORATION, of("vines_underground_extra3"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_coal_extra"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE), GenerationStep.Feature.VEGETAL_DECORATION, of("vines_underground_extra"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE), GenerationStep.Feature.VEGETAL_DECORATION, of("vines_underground_extra2"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE), GenerationStep.Feature.VEGETAL_DECORATION, of("vines_underground_extra3"));

        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.OLD_GROWTH_BIRCH_FOREST), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_mossy_cobblestone"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.OLD_GROWTH_BIRCH_FOREST), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_iron_extra"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.OLD_GROWTH_PINE_TAIGA), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_mossy_cobblestone"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.OLD_GROWTH_PINE_TAIGA), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_iron_extra"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_mossy_cobblestone"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_iron_extra"));

        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.PALE_GARDEN), GenerationStep.Feature.VEGETAL_DECORATION, of("cave_hanging_moss"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.PALE_GARDEN), GenerationStep.Feature.VEGETAL_DECORATION, of("cave_pale_carpet"));

        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.MUSHROOM_FIELDS), GenerationStep.Feature.UNDERGROUND_ORES, of("ore_redstone_extra"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.MUSHROOM_FIELDS), GenerationStep.Feature.VEGETAL_DECORATION, of("mushrooms_extra"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.MUSHROOM_FIELDS), GenerationStep.Feature.VEGETAL_DECORATION, of("mushrooms_extra2"));



        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.DESERT), SpawnGroup.MONSTER, EntityType.ENDERMAN, 100, 1, 4);
    }

    public static RegistryKey<PlacedFeature> of(String id) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, FixedMinecraft.id(id));
    }
}
