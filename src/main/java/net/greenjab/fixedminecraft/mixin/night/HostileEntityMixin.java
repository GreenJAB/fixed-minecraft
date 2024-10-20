package net.greenjab.fixedminecraft.mixin.night;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HostileEntity.class)
public class HostileEntityMixin {

    @Inject(method = "canSpawnInDark", at = @At(value = "RETURN"), cancellable = true)
    private static void zombieVillagerOnSurface(EntityType<? extends HostileEntity> type, ServerWorldAccess world, SpawnReason spawnReason,
                                     BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if (type == EntityType.ZOMBIE_VILLAGER) {
            cir.setReturnValue(cir.getReturnValue() && (SpawnReason.isAnySpawner(spawnReason) || world.isSkyVisible(pos)));
        }
    }
}
