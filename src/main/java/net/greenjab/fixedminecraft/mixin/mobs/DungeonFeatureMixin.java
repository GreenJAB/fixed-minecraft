package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.DungeonFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DungeonFeature.class)
public class DungeonFeatureMixin {
    @ModifyExpressionValue(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/DungeonFeature;getMobSpawnerEntity(Lnet/minecraft/util/math/random/Random;)Lnet/minecraft/entity/EntityType;"))
    private EntityType<?> biomeVariant(EntityType<?> original, @Local BlockPos blockPos, @Local StructureWorldAccess structureWorldAccess) {
        RegistryEntry<Biome> biome =  structureWorldAccess.getBiome(blockPos);
        if (original == EntityType.ZOMBIE) {
            if (biome.isIn(BiomeTags.DESERT_PYRAMID_HAS_STRUCTURE)) {
                return EntityType.HUSK;
            }
            if (biome.isIn(BiomeTags.IS_OCEAN)) {
                return EntityType.DROWNED;
            }
        }
        if (original == EntityType.SKELETON) {
            if (biome.isIn(BiomeTags.SPAWNS_SNOW_FOXES)) {
                return EntityType.STRAY;
            }
            if (biome.isIn(BiomeTags.SWAMP_HUT_HAS_STRUCTURE)) {
                return EntityType.BOGGED;
            }
        }
        if (original == EntityType.SPIDER) {
            if (biome.isIn(BiomeTags.IS_MOUNTAIN)) {
                return EntityType.CAVE_SPIDER;
            }
        }
        return original;
    }
}
