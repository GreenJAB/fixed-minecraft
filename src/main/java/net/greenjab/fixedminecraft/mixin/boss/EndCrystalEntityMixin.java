package net.greenjab.fixedminecraft.mixin.boss;

import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(EndCrystalEntity.class)
public class EndCrystalEntityMixin {

    @Shadow
    public int endCrystalAge;

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
}
