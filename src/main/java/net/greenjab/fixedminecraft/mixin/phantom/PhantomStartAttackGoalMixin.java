package net.greenjab.fixedminecraft.mixin.phantom;

import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$StartAttackGoal")
class PhantomStartAttackGoalMixin {

    @Shadow
    @Final
    PhantomEntity field_7321;

    @ModifyArg(method = "start", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/mob/PhantomEntity$StartAttackGoal;getTickCount(I)I"), index = 0)
    private int longerCooldown(int par1){
        return 20*(5+this.field_7321.getRandom().nextInt(10));
    }

}
