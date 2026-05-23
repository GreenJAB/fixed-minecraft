package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatrolSpawner.class)
public abstract class PatrolSpawnerMixin {

    @Inject(method = "spawnPatrolMember", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/PatrollingMonster;setPatrolLeader(Z)V"))
    private void setMapPillager(ServerLevel level, BlockPos pos, RandomSource random, boolean isLeader, CallbackInfoReturnable<Boolean> cir,
                                @Local PatrollingMonster mob) {
        if (level.getRandom().nextInt(3) == 0) {
            mob.addTag("map");
        }
    }
}
