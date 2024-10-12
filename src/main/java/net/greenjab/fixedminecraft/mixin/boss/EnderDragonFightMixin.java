package net.greenjab.fixedminecraft.mixin.boss;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonSpawnState;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
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

@SuppressWarnings("unchecked")
@Mixin(EnderDragonFight.class)
public abstract class EnderDragonFightMixin {

    @Shadow
    @Final
    private ServerWorld world;

    @Shadow
    private @Nullable BlockPos exitPortalLocation;

    @Shadow
    private boolean previouslyKilled;

    @Shadow
    private boolean dragonKilled;

    @Shadow
    protected abstract void generateEndPortal(boolean previouslyKilled);

    @Shadow
    private @Nullable EnderDragonSpawnState dragonSpawnState;

    @Shadow
    private int spawnStateTimer;

    @Shadow
    private @Nullable List<EndCrystalEntity> crystals;

    @Shadow
    @Final
    private ServerBossBar bossBar;

    @Shadow
    private @Nullable UUID dragonUuid;

    @Inject(method = "respawnDragon()V", at = @At(value = "FIELD",
                                                  target = "Lnet/minecraft/entity/boss/dragon/EnderDragonFight;exitPortalLocation:Lnet/minecraft/util/math/BlockPos;", ordinal = 0, opcode = Opcodes.GETFIELD), cancellable = true)
    private void onlySpawnDragonWhenPlayersNearby(CallbackInfo ci) {
        List<ServerPlayerEntity> list = this.world.getNonSpectatingEntities(ServerPlayerEntity.class, new Box(-50, 0, -50, 50, 100, 50));
        if (list.isEmpty()) {
            ci.cancel();
        }
    }

    @Inject(method = "updatePlayers", at = @At(value = "HEAD"))
    private void resetWorldBorder(CallbackInfo ci) {
        if (!this.previouslyKilled) {
            if (this.world.getDifficulty().getId()>1) {
                this.world.getWorldBorder().setSize(400);

                List<ServerPlayerEntity> playerList = world.getPlayers();
                for (ServerPlayerEntity player : playerList) {
                    if (player.getWorld().getRegistryKey() == this.world.getRegistryKey()) {
                        world.getServer().getPlayerManager().sendWorldInfo(player, world);
                    }
                }
            }
        }
        this.bossBar.setColor(BossBar.Color.PINK);
        if (this.dragonUuid!=null) {
            if (this.world.getEntity(this.dragonUuid)!=null) {
                if (this.world.getEntity(this.dragonUuid).getCommandTags().contains("omen")) {
                    this.bossBar.setColor(BossBar.Color.PURPLE);
                }
            }
        }
    }

    @Redirect(method = "respawnDragon()V", at = @At(value = "INVOKE",
                                                    target = "Lnet/minecraft/entity/boss/dragon/EnderDragonFight;generateEndPortal(Z)V"
    ))
    private void dontResetPortal(EnderDragonFight instance, boolean previouslyKilled){
        if (this.previouslyKilled) {
            this.generateEndPortal(true);
        }
    }

    @Redirect(method = "respawnDragon(Ljava/util/List;)V", at = @At(value = "INVOKE",
                                                    target = "Lnet/minecraft/entity/boss/dragon/EnderDragonFight;generateEndPortal(Z)V"
    ))
    private void dontResetPortal2(EnderDragonFight instance, boolean previouslyKilled){
        if (this.previouslyKilled) {
            this.generateEndPortal(false);
        }
    }

    @Inject(method = "respawnDragon(Ljava/util/List;)V", at = @At(value = "HEAD"), cancellable = true)
    private void dontResetPortal3(List<EndCrystalEntity> crystals, CallbackInfo ci){
        if (!this.previouslyKilled) {
            if (this.dragonKilled && this.dragonSpawnState == null) {
                this.dragonSpawnState = EnderDragonSpawnState.START;
                this.spawnStateTimer = 0;
                this.crystals = crystals;
            }

            ci.cancel();
        }
    }


    @Redirect(method = "checkDragonSeen", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/dragon/EnderDragonFight;createDragon()Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;"))
    private EnderDragonEntity dontSpawnDragon(EnderDragonFight instance){
        this.dragonKilled = true;
        return null;
    }

    @Redirect(method = "crystalDestroyed", at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
    private boolean crystalfixer(List instance, Object o){
        if (instance == null) {
            return false;
        }
        if (instance.isEmpty()) {
            return false;
        }
        return instance.contains(o);
    }

    @Inject(method = "generateEndPortal", at = @At(value = "TAIL"))
    private void placeCrystals(CallbackInfo ci, @Local boolean prev){
        if (!this.previouslyKilled && !prev) {

            List<EndCrystalEntity> list = this.world.getNonSpectatingEntities(EndCrystalEntity.class, new Box(-50, 50, -50, 50, 120, 50));
            for (EndCrystalEntity endCrystalEntity : list) {
                endCrystalEntity.kill();
            }

            BlockPos blockPos = this.exitPortalLocation;
            BlockPos b = blockPos.up(1);
            for (Direction d : Direction.values()) {
                if (d.getAxis().isHorizontal()) {
                    EndCrystalEntity endCrystalEntity = EntityType.END_CRYSTAL.create(this.world.getWorldChunk(b.offset(d, 3)).getWorld());
                    if (endCrystalEntity != null) {
                        endCrystalEntity.refreshPositionAndAngles(b.offset(d, 3).getX()+0.5, b.getY(), b.offset(d, 3).getZ() + 0.5, 0, 0.0F);
                        endCrystalEntity.setInvulnerable(true);
                        endCrystalEntity.setShowBottom(false);
                        this.world.spawnEntity(endCrystalEntity);
                    }
                }
            }
        }
    }

    @Inject(method = "dragonKilled", at = @At(value = "INVOKE",
                                              target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"
    ))
    private void expandWorldBorder(CallbackInfo ci) {
        this.world.getWorldBorder().setSize(60000000);
        List<ServerPlayerEntity> playerList = world.getPlayers();
        for (ServerPlayerEntity player : playerList) {
            if (player.getWorld().getRegistryKey() == this.world.getRegistryKey()) {
                world.getServer().getPlayerManager().sendWorldInfo(player, world);
            }
        }
    }

    @ModifyConstant(method = "createDragon", constant = @Constant(intValue = 128))
    private int lowerDragonSpawn(int constant){
        return 108;
    }

    @Inject(method = "<init>(Lnet/minecraft/server/world/ServerWorld;JLnet/minecraft/entity/boss/dragon/EnderDragonFight$Data;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "TAIL"))
    private void dontStartImmediately(CallbackInfo ci) {
        if (!this.previouslyKilled && this.dragonUuid==null) {
            this.bossBar.setVisible(false);
            this.dragonKilled = true;
        }
    }

    @Inject(method = "createDragon", at= @At(value = "INVOKE",
                                             target = "Lnet/minecraft/server/world/ServerWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z"
    ))
    private void summonBackupHitbox(CallbackInfoReturnable<EnderDragonEntity> cir, @Local EnderDragonEntity enderDragonEntity){
        InteractionEntity IE = EntityType.INTERACTION.create(this.world.getWorldChunk(new BlockPos(0, 0, 0)).getWorld());
        if (IE != null) {
            IE.refreshPositionAndAngles(0, 108, 0, 0, 0.0F);
            IE.addCommandTag("dragon");
            this.world.spawnEntity(IE);
        }
        PlayerEntity playerEntity = this.world.getClosestPlayer(TargetPredicate.createAttackable().setBaseMaxDistance(150), enderDragonEntity, enderDragonEntity.getX(), enderDragonEntity.getY(), enderDragonEntity.getZ());
        if (playerEntity != null) {
            if (playerEntity.hasStatusEffect(StatusEffects.BAD_OMEN)) {
                playerEntity.removeStatusEffect(StatusEffects.BAD_OMEN);
                enderDragonEntity.addCommandTag("omen");
                enderDragonEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(enderDragonEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).getValue()*1.5);
                enderDragonEntity.setHealth(enderDragonEntity.getMaxHealth());
            }
        }
    }

    @Inject(method = "dragonKilled", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/dragon/EnderDragonFight;generateEndPortal(Z)V"))
    private void spawnElytraItem(EnderDragonEntity dragon, CallbackInfo ci) {
        if (dragon.getCommandTags().contains("omen")) {
            ItemEntity itemEntity = new ItemEntity(dragon.getWorld(), 0, dragon.getY()-2, 0, Items.ELYTRA.getDefaultStack());
            itemEntity.refreshPositionAndAngles(0.5f, dragon.getY(), 0.5f, 0.0F, 0);
            itemEntity.setVelocity(new Vec3d(0, 0, 0));
            dragon.getWorld().spawnEntity(itemEntity);
        }
    }

}
