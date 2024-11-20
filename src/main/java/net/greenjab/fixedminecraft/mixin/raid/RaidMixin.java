package net.greenjab.fixedminecraft.mixin.raid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.village.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Raid.class)
public class RaidMixin {
    @ModifyExpressionValue(method = "spawnNextWave", at = @At(value = "FIELD",
                                                              target = "Lnet/minecraft/village/raid/Raid$Member;type:Lnet/minecraft/entity/EntityType;", ordinal = 0
    ))
    private EntityType<? extends RaiderEntity> replaceWithIllusioner(EntityType<? extends RaiderEntity> original){
        if (original == EntityType.EVOKER) {
            if (Math.random()<0.6666) {
                return EntityType.ILLUSIONER;
            }
        }
        return original;
    }

    @ModifyExpressionValue(method = "spawnNextWave", at = @At(value = "FIELD",
                                                              target = "Lnet/minecraft/entity/EntityType;EVOKER:Lnet/minecraft/entity/EntityType;"
    ))
    private EntityType<? extends RaiderEntity> replaceWithIllusionerRavager(EntityType<? extends RaiderEntity> original){
        if (Math.random()<0.6666) {
            return EntityType.ILLUSIONER;
        }
        return original;
    }
}
