package net.greenjab.fixedminecraft.mixin.dragon;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.EnderDragonFight;

@Mixin(EndCrystal.class)
public abstract class EndCrystalMixin {

    @Shadow
    public int time;

    @Shadow
    @Final
    private static EntityDataAccessor<Optional<BlockPos>> DATA_BEAM_TARGET;

    @Inject(method = "tick", at = @At("HEAD"))
    private void checkForDragonFight(CallbackInfo ci) {
        EndCrystal ECE = (EndCrystal)(Object)this;
        if (this.time%100==0 && ECE.isInvulnerable()) {
            EnderDragonFight enderDragonFight = ((ServerLevel)ECE.level()).getDragonFight();
            if (enderDragonFight != null) {
                enderDragonFight.tryRespawn();
            }
        }
    }

    @Inject(method = "setBeamTarget", at = @At(
            value = "HEAD"), cancellable = true)
    private void lowerBeam(BlockPos target, CallbackInfo ci) {
        if (target != null) {
            EndCrystal ECE = (EndCrystal) (Object) this;
            if (target.getY() == 128) {
                ECE.getEntityData().set(DATA_BEAM_TARGET, Optional.ofNullable(target.below(20)));
            } else {
                ECE.getEntityData().set(DATA_BEAM_TARGET, Optional.ofNullable(target.below(2)));
            }
            ci.cancel();
        }
    }

    @Inject(method = "hurtServer", at = @At(
            value = "HEAD"), cancellable = true)
    private void ignoreExplosions(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if(source.getEntity() instanceof EnderDragon)cir.setReturnValue(false);
    }
}
