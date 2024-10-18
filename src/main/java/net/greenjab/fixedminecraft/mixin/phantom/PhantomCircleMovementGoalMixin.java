package net.greenjab.fixedminecraft.mixin.phantom;

import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$CircleMovementGoal")
class PhantomCircleMovementGoalMixin {
    @Shadow
    @Final
    PhantomEntity field_7325;

    @Redirect(method = "tick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;isAir(Lnet/minecraft/util/math/BlockPos;)Z", ordinal = 0
    ))
    private boolean dive(World instance, BlockPos blockPos){
        PhantomEntity PE = this.field_7325;
        if (PE.noClip) {
            if (PE.getWorld().getBlockState(blockPos.up(4)).isSolid()) {
                PE.discard();
            }
            return true;
        } else {
            return instance.isAir(blockPos);
        }
    }
}