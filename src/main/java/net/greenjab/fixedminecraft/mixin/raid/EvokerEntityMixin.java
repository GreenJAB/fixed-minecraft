package net.greenjab.fixedminecraft.mixin.raid;

import net.minecraft.entity.mob.EvokerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EvokerEntity.class)
public abstract class EvokerEntityMixin {

    @ModifyArg(method = "createEvokerAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;add(Lnet/minecraft/entity/attribute/EntityAttribute;D)Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;", ordinal = 0), index = 1)
    private static double slowerBaseSpeed(double baseValue){
        return 0.35;
    }

}
