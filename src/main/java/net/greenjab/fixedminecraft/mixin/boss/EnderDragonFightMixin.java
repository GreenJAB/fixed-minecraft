package net.greenjab.fixedminecraft.mixin.boss;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonSpawnState;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

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

    @Inject(method = "respawnDragon()V", at = @At(value = "FIELD",
                                                  target = "Lnet/minecraft/entity/boss/dragon/EnderDragonFight;exitPortalLocation:Lnet/minecraft/util/math/BlockPos;", ordinal = 0, opcode = Opcodes.GETFIELD), cancellable = true)
    private void injected(CallbackInfo ci) {
        List<ServerPlayerEntity> list = this.world.getNonSpectatingEntities(ServerPlayerEntity.class, new Box(-20, 0, -20, 20, 100, 20));
        if (list.isEmpty()) {
            ci.cancel();
        } else {
        }
    }

    @Inject(method = "convertFromLegacy", at = @At(value = "HEAD"))
    private void check(CallbackInfo ci){
        System.out.println("legacy");
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
                    EndCrystalEntity endCrystalEntity = EntityType.END_CRYSTAL.create(this.world);//.getWorld());
                    endCrystalEntity.setInvulnerable(true);
                    endCrystalEntity.setShowBottom(false);
                    endCrystalEntity.setPos(b.offset(d, 3).getX() + 0.5, b.getY(), b.offset(d, 3).getZ() + 0.5);
                    this.world.spawnEntity(endCrystalEntity);
                    //structureWorldAccess.spawnEntity(endCrystalEntity);
                }
            }
        }
    }

}
