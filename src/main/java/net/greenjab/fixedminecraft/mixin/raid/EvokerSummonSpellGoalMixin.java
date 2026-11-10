package net.greenjab.fixedminecraft.mixin.raid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.monster.Vex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.entity.monster.illager.Evoker$EvokerSummonSpellGoal")
public abstract class EvokerSummonSpellGoalMixin {

    @WrapOperation(method = "performSpellCasting", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Vex;setLimitedLife(I)V"))
    private void only30SecVex(Vex instance, int lifeTicks, Operation<Void> original) {
        original.call(instance, 30);
    }
}
