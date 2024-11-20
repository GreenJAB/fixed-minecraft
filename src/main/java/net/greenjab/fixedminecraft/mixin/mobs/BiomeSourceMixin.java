package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.CustomData;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Set;

@Mixin(BiomeSource.class)
public class BiomeSourceMixin {
    @Redirect(method = "locateBiome(Lnet/minecraft/util/math/BlockPos;IIILjava/util/function/Predicate;Lnet/minecraft/world/biome/source/util/MultiNoiseUtil$MultiNoiseSampler;Lnet/minecraft/world/WorldView;)Lcom/mojang/datafixers/util/Pair;",
            at = @At(value = "INVOKE",target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    private boolean check3(Set<RegistryEntry<Biome>> instance, Object o,
                           @Local RegistryEntry<Biome> registryEntry ) {
        if (instance.size()>50) {
            return registryEntry.matchesKey(CustomData.biomeSearch);
        }
        return instance.contains(registryEntry);
    }
}
