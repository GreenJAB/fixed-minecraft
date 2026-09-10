package net.greenjab.fixedminecraft.mixin.phantom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Inject(method = "wakeUpAllPlayers", at = @At(value = "HEAD"))
    private void turnInsomniaIntoHealthBoost(CallbackInfo ci) {
        ServerLevel level = (ServerLevel)(Object)this;
        (level.players().stream().filter(LivingEntity::isSleeping).toList()).forEach(player -> {
            if (!player.hasEffect(MobEffectRegistry.INSOMNIA)) return;
            int i = player.getEffect(MobEffectRegistry.INSOMNIA).getAmplifier();
            player.removeEffect(MobEffectRegistry.INSOMNIA);
            player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, (i+1)*12*60*20, i, true, false, true));
            if (i == 4) {
                CriteriaTriggers.CONSUME_ITEM.trigger(player, Items.BED.red().getDefaultInstance());
            }
            player.heal(10);
        });
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/SleepStatus;areEnoughDeepSleeping(ILjava/util/List;)Z"))
    private boolean bedsNeedToBeSafe(SleepStatus instance, int sleepPercentageNeeded, List<ServerPlayer> players, Operation<Boolean> original) {
        AtomicBoolean willSkipNight = new AtomicBoolean(original.call(instance, sleepPercentageNeeded, players));
        ServerLevel level = (ServerLevel)(Object)this;
        if (!level.getGameRules().get(GameRuleRegistry.SAFE_SLEEP_REQUIREMENT)) return willSkipNight.get();
        if (level.getDifficulty() == Difficulty.PEACEFUL) return willSkipNight.get();
        if (willSkipNight.get()) {
            players.stream().filter(Player::isSleepingLongEnough).forEach(player -> {
                if (!player.isCreative()) {
                    int x = Mth.floor(player.getX());
                    int y = Mth.floor(player.getY());
                    int z = Mth.floor(player.getZ());
                    Zombie zombie = EntityTypes.ZOMBIE.create(level, EntitySpawnReason.TRIGGERED);
                    if (zombie != null) {
                        zombie.setTarget(player);
                        int i = 0;
                        if (level.getBrightness(LightLayer.BLOCK, player.blockPosition()) == 0) {
                            zombie.finalizeSpawn(level, level.getCurrentDifficultyAt(zombie.blockPosition()), EntitySpawnReason.TRIGGERED, null);
                            level.addFreshEntityWithPassengers(zombie);
                            zombie.setPos(player.position());
                            willSkipNight.set(false);
                            player.stopSleeping();
                            i = 500;
                        }
                        for (; i < 500; i++) {
                            int xt = x + Mth.nextInt(player.getRandom(), 7, 32) * Mth.nextInt(player.getRandom(), -1, 1);
                            int yt = y + Mth.nextInt(player.getRandom(), 7, 16) * Mth.nextInt(player.getRandom(), -1, 1);
                            int zt = z + Mth.nextInt(player.getRandom(), 7, 32) * Mth.nextInt(player.getRandom(), -1, 1);
                            BlockPos spawnPos = new BlockPos(xt, yt, zt);
                            if (SpawnPlacements.isSpawnPositionOk(EntityTypes.ZOMBIE, level, spawnPos)
                                &&
                                SpawnPlacements.checkSpawnRules(EntityTypes.ZOMBIE, level, EntitySpawnReason.TRIGGERED, spawnPos, level.getRandom())) {
                                zombie.setPos(xt, yt, zt);
                                zombie.setOnGround(true);
                                if (!level.hasNearbyAlivePlayer(xt, yt, zt, 7.0) && level.isUnobstructed(zombie) &&
                                    level.noCollision(zombie) &&
                                    !level.containsAnyLiquid(zombie.getBoundingBox())) {
                                    Path path = zombie.getNavigation().createPath(x, y, z, 1);
                                    if (path != null) {
                                        Vec3 v = path.getEndNode().asVec3();
                                        System.out.println(v.distanceToSqr(player.position()));
                                        if (v.distanceToSqr(player.position()) < 2.5) {
                                            zombie.finalizeSpawn(level, level.getCurrentDifficultyAt(zombie.blockPosition()), EntitySpawnReason.TRIGGERED, null);
                                            level.addFreshEntityWithPassengers(zombie);
                                            zombie.setPos(path.getEndNode().asVec3());
                                            willSkipNight.set(false);
                                            player.stopSleeping();
                                            i = 500;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
        return willSkipNight.get();
    }
}
