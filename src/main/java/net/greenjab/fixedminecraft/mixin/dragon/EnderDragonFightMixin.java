package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntityReference;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.end.DragonRespawnStage;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Mixin(EnderDragonFight.class)
public abstract class EnderDragonFightMixin {

    @Shadow
    private ServerLevel level;

    @Shadow
    private @Nullable BlockPos exitPortalLocation;

    @Shadow
    private boolean hasPreviouslyKilledDragon;

    @Shadow
    private boolean dragonKilled;

    @Shadow
    protected abstract void spawnExitPortal(boolean activated);

    @Shadow
    private @Nullable DragonRespawnStage respawnStage;

    @Shadow
    private int respawnTime;

    @Shadow
    private @Nullable List<EntityReference<EndCrystal>> respawnCrystals;

    @Shadow
    private ServerBossEvent dragonEvent;

    @Shadow
    private @Nullable UUID dragonUUID;

    @Shadow
    private BlockPos origin;

    @Inject(method = "tryRespawn()V", at = @At(value = "FIELD",
                                                  target = "Lnet/minecraft/world/level/dimension/end/EnderDragonFight;exitPortalLocation:Lnet/minecraft/core/BlockPos;", ordinal = 0, opcode = Opcodes.GETFIELD), cancellable = true)
    private void onlySpawnDragonWhenPlayersNearby(CallbackInfo ci) {
        List<ServerPlayer> list = this.level.getEntitiesOfClass(ServerPlayer.class, new AABB(-50, 0, -50, 50, 100, 50));
        if (list.isEmpty()) {
            ci.cancel();
        }
    }

    @Inject(method = "updatePlayers", at = @At(value = "HEAD"))
    private void omenBossBar(CallbackInfo ci) {
        if (!this.level.isClientSide()) {
            List<ServerPlayer> playerList = level.players();
            if (!this.hasPreviouslyKilledDragon && this.level.getDifficulty().getId() > 1) {
                for (ServerPlayer player : playerList) {
                    if (player.level().dimensionType() == this.level.dimensionType()) {
                        WorldBorder WB = new WorldBorder();
                        WB.setSize(700);
                        player.connection.send(new ClientboundInitializeBorderPacket(WB));
                    }
                }
            } else {
                for (ServerPlayer player : playerList) {
                    if (player.level().dimensionType() == this.level.dimensionType()) {
                        WorldBorder WB = new WorldBorder();
                        WB.setSize(this.level.getServer().overworld().getWorldBorder().getSize());
                        player.connection.send(new ClientboundInitializeBorderPacket(WB));
                    }
                }
            }
        }//*/
        this.dragonEvent.setColor(BossEvent.BossBarColor.PINK);
        if (this.dragonUUID!=null) {
            if (this.level.getEntity(this.dragonUUID)!=null) {
                if (this.level.getEntity(this.dragonUUID).entityTags().contains("omen")) {
                    this.dragonEvent.setColor(BossEvent.BossBarColor.PURPLE);
                }
            }
        }
    }

    @Redirect(method = "tryRespawn()V", at = @At(value = "INVOKE",
                                                    target = "Lnet/minecraft/world/level/dimension/end/EnderDragonFight;spawnExitPortal(Z)V"
    ))
    private void dontResetPortal(EnderDragonFight instance, boolean activated){
        if (this.hasPreviouslyKilledDragon) {
            this.spawnExitPortal(true);
        }
    }

    @Redirect(method = "respawnDragon(Ljava/util/List;)V", at = @At(value = "INVOKE",
                                                    target = "Lnet/minecraft/world/level/dimension/end/EnderDragonFight;spawnExitPortal(Z)V"
    ))
    private void dontResetPortal2(EnderDragonFight instance, boolean activated){
        if (this.hasPreviouslyKilledDragon) {
            this.spawnExitPortal(false);
            for (ServerPlayer serverPlayerEntity : (this.level)
                    .getPlayers( serverPlayerEntityx -> serverPlayerEntityx.position().horizontalDistance() < 128.0F)) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, Items.END_CRYSTAL.getDefaultInstance());
            }
        }
    }

    @Inject(method = "respawnDragon(Ljava/util/List;)V", at = @At(value = "HEAD"), cancellable = true)
    private void dontResetPortal3(List<EndCrystal> crystals, CallbackInfo ci){
        if (!this.hasPreviouslyKilledDragon) {
            if (this.dragonKilled && this.respawnStage == null) {
                this.respawnStage = DragonRespawnStage.START;
                this.respawnTime = 0;
                this.respawnCrystals = crystals.stream().map(EntityReference::of).toList();
            }

            ci.cancel();
        }
    }


    @Redirect(method = "findOrCreateDragon", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/end/EnderDragonFight;createNewDragon()Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;"))
    private EnderDragon dontSpawnDragon(EnderDragonFight instance){
        this.dragonKilled = true;
        return null;
    }

    @Redirect(method = "onCrystalDestroyed", at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
    private boolean crystalfixer(List<EndCrystal> instance, Object o){
        if (instance == null) {
            return false;
        }
        if (instance.isEmpty()) {
            return false;
        }
        return instance.contains((EndCrystal)o);
    }

    @Inject(method = "spawnExitPortal", at = @At(value = "TAIL"))
    private void placeCrystals(CallbackInfo ci, @Local(argsOnly = true) boolean activated){
        if (!this.hasPreviouslyKilledDragon && !activated) {

            List<EndCrystal> list = this.level.getEntitiesOfClass(EndCrystal.class, new AABB(-50, 50, -50, 50, 120, 50));
            for (EndCrystal endCrystalEntity : list) {
                endCrystalEntity.kill(this.level);
            }

            BlockPos blockPos = this.exitPortalLocation;
            assert blockPos != null;
            BlockPos b = blockPos.above(1);
            for (Direction d : Direction.values()) {
                if (d.getAxis().isHorizontal()) {
                    EndCrystal endCrystalEntity = EntityType.END_CRYSTAL.create(this.level.getChunkAt(b.relative(d, 3)).getLevel(), EntitySpawnReason.CHUNK_GENERATION);

                    if (endCrystalEntity != null) {
                        endCrystalEntity.snapTo(b.relative(d, 3).getX()+0.5, b.getY(), b.relative(d, 3).getZ() + 0.5, 0, 0.0F);
                        endCrystalEntity.setInvulnerable(true);
                        endCrystalEntity.setShowBottom(false);
                        this.level.addFreshEntity(endCrystalEntity);
                    }
                }
            }
        }
    }

    @ModifyConstant(method = "createNewDragon", constant = @Constant(intValue = 128))
    private int lowerDragonSpawn(int constant){
        return 108;
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    private void dontStartImmediately(CallbackInfo ci) {
        if (!this.hasPreviouslyKilledDragon && this.dragonUUID == null) {
            this.dragonEvent.setVisible(false);
            this.dragonKilled = true;
        }
    }

    @Inject(method = "createNewDragon", at= @At(value = "INVOKE",
                                             target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
    ))
    private void spawnOmenDragon(CallbackInfoReturnable<EnderDragon> cir, @Local EnderDragon dragon){
        Player playerEntity = this.level.getNearestPlayer(TargetingConditions.forCombat().range(150), dragon, dragon.getX(), dragon.getY(), dragon.getZ());
        if (playerEntity != null) {
            if (playerEntity.hasEffect(MobEffects.BAD_OMEN)) {
                playerEntity.removeEffect(MobEffects.BAD_OMEN);
                dragon.addTag("omen");
                dragon.getAttribute(Attributes.MAX_HEALTH).setBaseValue(dragon.getAttribute(Attributes.MAX_HEALTH).getValue() * 1.5);
                dragon.setHealth(dragon.getMaxHealth());
            }
        }
    }

    @Inject(method = "setDragonKilled", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/end/EnderDragonFight;spawnExitPortal(Z)V"))
    private void spawnElytraItem(EnderDragon dragon, CallbackInfo ci) {
        if (dragon.entityTags().contains("omen")) {
            ItemEntity itemEntity = new ItemEntity(dragon.level(), 0, dragon.getY()-2, 0, Items.ELYTRA.getDefaultInstance());
            itemEntity.snapTo(0.5f, dragon.getY(), 0.5f, 0.0F, 0);
            itemEntity.setDeltaMovement(new Vec3(0, 0, 0));
            dragon.level().addFreshEntity(itemEntity);
            this.level.setBlockAndUpdate(this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.getLocation(this.origin)), Blocks.DRAGON_EGG.defaultBlockState());
        }

        for (ServerPlayer serverPlayerEntity : (this.level)
                .getPlayers( serverPlayerEntityx -> serverPlayerEntityx.position().horizontalDistance() < 128.0F)) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, Items.DRAGON_HEAD.getDefaultInstance());
            if (dragon.entityTags().contains("omen")) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, Items.DRAGON_EGG.getDefaultInstance());
            }
        }
    }
}
