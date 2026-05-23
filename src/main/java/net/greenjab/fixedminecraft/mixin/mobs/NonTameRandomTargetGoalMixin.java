package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NonTameRandomTargetGoal.class)
public abstract class NonTameRandomTargetGoalMixin {
    @Shadow
    @Final
    private TamableAnimal tamableMob;

    @Inject(method = "canUse", at = @At(value = "HEAD"), cancellable = true)
    public void mobGriefing(CallbackInfoReturnable<Boolean> cir) {
        if (tamableMob.level() instanceof ServerLevel serverLevel)
            if (!serverLevel.getGameRules().get(GameRules.MOB_GRIEFING)) cir.setReturnValue(false);
    }
}
