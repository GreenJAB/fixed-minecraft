package net.greenjab.fixedminecraft.registry.registries;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class BiomeAdditions {

    public static void registerBiomeAdds() {
        System.out.println("register BiomeAdds");

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_EMERALD);
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_emerald"));

        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_ancient_debris_lava"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.BASALT_DELTAS), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_ancient_debris_extra"));

        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.IS_FROZEN), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_packed_ice"));
        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.IS_FROZEN), GenerationStep.Decoration.VEGETAL_DECORATION, of("cave_snow"));

        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_OCEAN), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_copper_extra"));

        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_BADLANDS), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_terracotta"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_BADLANDS), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_gold_extra"));

        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.DESERT), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_lapis_extra"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.DESERT), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_sandstone"));

        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_coal_extra"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), GenerationStep.Decoration.VEGETAL_DECORATION, of("vines_underground_extra"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), GenerationStep.Decoration.VEGETAL_DECORATION, of("vines_underground_extra2"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), GenerationStep.Decoration.VEGETAL_DECORATION, of("vines_underground_extra3"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.HAS_RUINED_PORTAL_SWAMP), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_coal_extra"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.HAS_RUINED_PORTAL_SWAMP), GenerationStep.Decoration.VEGETAL_DECORATION, of("vines_underground_extra"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.HAS_RUINED_PORTAL_SWAMP), GenerationStep.Decoration.VEGETAL_DECORATION, of("vines_underground_extra2"));
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.HAS_RUINED_PORTAL_SWAMP), GenerationStep.Decoration.VEGETAL_DECORATION, of("vines_underground_extra3"));

        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.OLD_GROWTH_BIRCH_FOREST), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_mossy_cobblestone"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.OLD_GROWTH_BIRCH_FOREST), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_iron_extra"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.OLD_GROWTH_PINE_TAIGA), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_mossy_cobblestone"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.OLD_GROWTH_PINE_TAIGA), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_iron_extra"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.OLD_GROWTH_SPRUCE_TAIGA), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_mossy_cobblestone"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.OLD_GROWTH_SPRUCE_TAIGA), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_iron_extra"));

        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.PALE_GARDEN), GenerationStep.Decoration.VEGETAL_DECORATION, of("cave_hanging_moss"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.PALE_GARDEN), GenerationStep.Decoration.VEGETAL_DECORATION, of("cave_pale_carpet"));

        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.MUSHROOM_FIELDS), GenerationStep.Decoration.UNDERGROUND_ORES, of("ore_redstone_extra"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.MUSHROOM_FIELDS), GenerationStep.Decoration.VEGETAL_DECORATION, of("mushrooms_extra"));
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.MUSHROOM_FIELDS), GenerationStep.Decoration.VEGETAL_DECORATION, of("mushrooms_extra2"));



        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.DESERT), MobCategory.MONSTER, EntityType.ENDERMAN, 100, 1, 4);
    }

    public static ResourceKey<PlacedFeature> of(String id) {
        return ResourceKey.create(Registries.PLACED_FEATURE, FixedMinecraft.id(id));
    }
}
