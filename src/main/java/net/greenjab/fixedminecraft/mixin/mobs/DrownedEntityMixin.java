package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.entity.mob.DrownedEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DrownedEntity.class)
public  class DrownedEntityMixin {

    @ModifyArg(method = "initCustomGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/DrownedEntity$TridentAttackGoal;<init>(Lnet/minecraft/entity/ai/RangedAttackMob;DIF)V"), index = 2)
    private int longerTridenDelay(int i) {
        return 80;
    }
}
