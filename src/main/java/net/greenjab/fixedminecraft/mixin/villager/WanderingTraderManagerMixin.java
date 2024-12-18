package net.greenjab.fixedminecraft.mixin.villager;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(WanderingTraderManager.class)
public abstract class WanderingTraderManagerMixin {
    @Shadow
    private int spawnTimer;

    @Shadow
    private int spawnChance;

    @Shadow
    private int spawnDelay;

    @Shadow
    @Final
    private ServerWorldProperties properties;

    @Shadow
    @Final
    private Random random;

    @Shadow
    protected abstract boolean trySpawn(ServerWorld world);

    @Shadow
    @Nullable
    protected abstract BlockPos getNearbySpawnPos(WorldView world, BlockPos pos, int range);

    @Shadow
    protected abstract boolean doesNotSuffocateAt(BlockView world, BlockPos pos);

    @Shadow
    protected abstract void spawnLlama(ServerWorld world, WanderingTraderEntity wanderingTrader, int range);

    @Redirect(method = "getNearbySpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SpawnHelper;canSpawn(Lnet/minecraft/entity/SpawnRestriction$Location;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z"))
    private boolean naturalSpawnsWanderingTrader(SpawnRestriction.Location location, WorldView world, BlockPos pos,
                                                 @Nullable EntityType<?> entityType) {
        return SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, pos, EntityType.WANDERING_TRADER) && world.getBlockState(pos.down()).isIn(BlockTags.AZALEA_ROOT_REPLACEABLE) ;
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
            int i = 48;
            PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
            Optional<BlockPos> optional = pointOfInterestStorage.getPosition(
                    /* method_44010 */ poiType -> poiType.matchesKey(PointOfInterestTypes.MEETING),
                    /* method_19631 */ pos -> true,
                    blockPos,
                    48,
                    PointOfInterestStorage.OccupationStatus.ANY
            );

            BlockPos blockPos2 = (BlockPos)optional.orElse(blockPos);
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

    @Inject(method = "spawn", at = @At("HEAD"), cancellable = true)
    private void temp(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfoReturnable<Integer> cir){


        if (!world.getGameRules().getBoolean(GameRules.DO_TRADER_SPAWNING)) {
            cir.setReturnValue(0);
        } else if (--this.spawnTimer > 0) {
            cir.setReturnValue(0);
        } else {
            this.spawnTimer = 1200;
            this.spawnDelay -= 1200;
            this.properties.setWanderingTraderSpawnDelay(this.spawnDelay);
            if (this.spawnDelay > 0) {
                cir.setReturnValue(0);
            } else {
                this.spawnDelay = 24000;
                if (!world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                    cir.setReturnValue(0);
                } else {
                    int i = this.spawnChance;
                    this.spawnChance = MathHelper.clamp(this.spawnChance + 25, 25, 100);
                    this.properties.setWanderingTraderSpawnChance(this.spawnChance);
                    if (this.random.nextInt(100) > i) {
                        cir.setReturnValue(0);
                    } else if (this.trySpawn(world)) {
                        this.spawnChance = 25;
                        cir.setReturnValue(1);
                    } else {
                        cir.setReturnValue(0);
                    }
                }
            }
        }
    }
}
