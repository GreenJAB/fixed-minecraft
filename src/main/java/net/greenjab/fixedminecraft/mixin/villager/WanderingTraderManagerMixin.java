package net.greenjab.fixedminecraft.mixin.villager;

import net.greenjab.fixedminecraft.data.ModTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WanderingTraderManager.class)
public abstract class WanderingTraderManagerMixin {
    @Redirect(method = "getNearbySpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SpawnHelper;canSpawn(Lnet/minecraft/entity/SpawnRestriction$Location;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z"))
    private boolean rebalancedWanderingTrader(SpawnRestriction.Location location, WorldView world, BlockPos pos,
                                              @Nullable EntityType<?> entityType) {
        return SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, pos, EntityType.WANDERING_TRADER) && world.getBlockState(pos.down()).isIn(BlockTags.AZALEA_ROOT_REPLACEABLE) ;
    }
}
