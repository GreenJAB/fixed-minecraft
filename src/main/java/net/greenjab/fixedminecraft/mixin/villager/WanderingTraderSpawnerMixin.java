package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner;
import net.minecraft.world.level.LevelReader;

@Mixin(WanderingTraderSpawner.class)
public abstract class WanderingTraderSpawnerMixin {

    @ModifyExpressionValue(method = "findSpawnPositionNear", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/SpawnPlacementType;isSpawnPositionOk(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntityType;)Z"))
    private boolean naturalSpawnsWanderingTrader(boolean original, @Local(argsOnly = true) LevelReader level,
                                                 @Local(argsOnly = true) BlockPos referencePosition) {
        return original && level.getBlockState(referencePosition.below()).is(BlockTags.AZALEA_ROOT_REPLACEABLE);
    }

    @ModifyConstant(method = "spawn",constant = @Constant(intValue = 10))
    private int moreChanceOfSpawning(int constant){
        return 2;
    }

    @ModifyConstant(method = "tick",constant = @Constant(intValue = 75))
    private int reach100PercentSpawnChance(int constant){
        return 100;
    }
}
