package net.greenjab.fixedminecraft.mixin.night;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Monster.class)
public abstract class MonsterMixin {

    @Inject(method = "checkMonsterSpawnRules", at = @At(value = "RETURN"), cancellable = true)
    private static void zombieVillagerOnSurface(EntityType<? extends Monster> type, ServerLevelAccessor level, EntitySpawnReason spawnReason,
                                                BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        if (type == EntityType.ZOMBIE_VILLAGER ) {
            cir.setReturnValue(cir.getReturnValue() && (EntitySpawnReason.isSpawner(spawnReason) || level.getBrightness(LightLayer.SKY, pos) > 5));
        }
        if (type == EntityType.CAVE_SPIDER) {
            if (spawnReason==EntitySpawnReason.NATURAL) {
                cir.setReturnValue(level.getBrightness(LightLayer.SKY, pos) == 0);
            }
            cir.setReturnValue(cir.getReturnValue());
        }
    }
}
