package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.WorldView;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(WanderingTraderManager.class)
public abstract class WanderingTraderManagerMixin {

    @Shadow
    @Final
    private ServerWorldProperties properties;

    @Shadow
    @Final
    private Random random;

    @Shadow
    @Nullable
    protected abstract BlockPos getNearbySpawnPos(WorldView world, BlockPos pos, int range);

    @Shadow
    protected abstract boolean doesNotSuffocateAt(BlockView world, BlockPos pos);

    @Shadow
    protected abstract void spawnLlama(ServerWorld world, WanderingTraderEntity wanderingTrader, int range);

    @ModifyExpressionValue(method = "getNearbySpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/SpawnLocation;isSpawnPositionOk(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z"))
    private boolean naturalSpawnsWanderingTrader(boolean original, @Local(argsOnly = true) WorldView world, @Local(argsOnly = true) BlockPos pos) {
        return original && world.getBlockState(pos.down()).isIn(BlockTags.AZALEA_ROOT_REPLACEABLE);
    }

    @Inject(method = "trySpawn", at = @At("HEAD"), cancellable = true)
    private void temp(ServerWorld world, CallbackInfoReturnable<Boolean> cir){
        PlayerEntity playerEntity = world.getRandomAlivePlayer();
        if (playerEntity == null) {
            cir.setReturnValue(true);
        } else if (this.random.nextInt(2) == 0) {
            cir.setReturnValue(false);
        } else {
            BlockPos blockPos = playerEntity.getBlockPos();
            PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
            Optional<BlockPos> optional = pointOfInterestStorage.getPosition(
                    /* method_44010 */ poiType -> poiType.matchesKey(PointOfInterestTypes.MEETING),
                    /* method_19631 */ pos -> true,
                    blockPos,
                    48,
                    PointOfInterestStorage.OccupationStatus.ANY
            );

            BlockPos blockPos2 = optional.orElse(blockPos);
            BlockPos blockPos3 = this.getNearbySpawnPos(world, blockPos2, 48);
            if (blockPos3 != null && this.doesNotSuffocateAt(world, blockPos3)) {
                if (world.getBiome(blockPos3).isIn(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)) {
                    cir.setReturnValue(false);
                    return;
                }

                WanderingTraderEntity wanderingTraderEntity = EntityType.WANDERING_TRADER.spawn(world, blockPos3, SpawnReason.EVENT);
                if (wanderingTraderEntity != null) {
                    for (int j = 0; j < 2; j++) {
                        this.spawnLlama(world, wanderingTraderEntity, 4);
                    }

                    this.properties.setWanderingTraderId(wanderingTraderEntity.getUuid());
                    wanderingTraderEntity.setDespawnDelay(48000);
                    wanderingTraderEntity.setWanderTarget(blockPos2);
                    wanderingTraderEntity.setPositionTarget(blockPos2, 16);
                    cir.setReturnValue(true);
                    return;
                }
            }
            cir.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "spawn",constant = @Constant(intValue = 75))
    private int temp(int constant){
        return 100;
    }
}
