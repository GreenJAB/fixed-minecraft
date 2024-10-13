package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(EndCrystalEntity.class)
public class EndCrystalEntityMixin {

    @Shadow
    public int endCrystalAge;

    @Shadow
    @Final
    private static TrackedData<Optional<BlockPos>> BEAM_TARGET;

    @Inject(method = "tick", at = @At("HEAD"))
    private void checkForDragonFight(CallbackInfo ci) {
        EndCrystalEntity ECE = (EndCrystalEntity)(Object)this;
        if (this.endCrystalAge%100==0 && ECE.isInvulnerable()) {
            EnderDragonFight enderDragonFight = ((ServerWorld)ECE.getWorld()).getEnderDragonFight();
            if (enderDragonFight != null) {
                enderDragonFight.respawnDragon();
            }
        }
    }

    @Inject(method = "setBeamTarget", at = @At(
            value = "HEAD"), cancellable = true)
    private void lowerBeam(BlockPos beamTarget, CallbackInfo ci) {
        if (beamTarget != null) {
            EndCrystalEntity ECE = (EndCrystalEntity) (Object) this;
            if (beamTarget.getY()==128) {
                ECE.getDataTracker().set(this.BEAM_TARGET, Optional.ofNullable(beamTarget.down(20)));
            } else {
                ECE.getDataTracker().set(this.BEAM_TARGET, Optional.ofNullable(beamTarget.down(2)));
            }
            ci.cancel();
        }
    }

    @Inject(method = "damage", at = @At(
            value = "HEAD"), cancellable = true)
    private void ignoreExplosions(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(source.getAttacker() instanceof EnderDragonEntity)cir.cancel();
    }
}
