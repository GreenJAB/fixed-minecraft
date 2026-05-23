package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MonsterRoomFeature.class)
public abstract class MonsterRoomFeatureMixin {
    @ModifyExpressionValue(method = "place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/MonsterRoomFeature;randomEntityId(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/entity/EntityType;"))
    private EntityType<?> biomeVariant(EntityType<?> original, @Local BlockPos origin, @Local WorldGenLevel level) {
        Holder<Biome> biome =  level.getBiome(origin);
        if (original == EntityType.ZOMBIE) {
            if (biome.is(BiomeTags.HAS_DESERT_PYRAMID)) {
                return EntityType.HUSK;
            }
            if (biome.is(BiomeTags.IS_OCEAN)) {
                return EntityType.DROWNED;
            }
        }
        if (original == EntityType.SKELETON) {
            if (biome.is(BiomeTags.SPAWNS_SNOW_FOXES)) {
                return EntityType.STRAY;
            }
            if (biome.is(BiomeTags.HAS_SWAMP_HUT)) {
                return EntityType.BOGGED;
            }
        }
        if (original == EntityType.SPIDER) {
            if (biome.is(BiomeTags.IS_MOUNTAIN)) {
                return EntityType.CAVE_SPIDER;
            }
        }
        return original;
    }
}
