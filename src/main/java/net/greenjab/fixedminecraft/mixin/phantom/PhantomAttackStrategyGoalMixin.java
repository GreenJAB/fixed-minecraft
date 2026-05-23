package net.greenjab.fixedminecraft.mixin.phantom;

import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.world.entity.monster.Phantom$PhantomAttackStrategyGoal")
public abstract class PhantomAttackStrategyGoalMixin {

    @Shadow
    @Final
    Phantom this$0;

    @ModifyArg(method = "start", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/Phantom$PhantomAttackStrategyGoal;adjustedTickDelay(I)I"), index = 0)
    private int longerCooldown(int par1){
        return 20*(5+this.this$0.getRandom().nextInt(10));
    }

    @ModifyConstant(method = "stop", constant = @Constant(intValue = 10))
    private int lowerCircling1(int constant) {
        return 15;
    }
    @ModifyConstant(method = "stop", constant = @Constant(intValue = 20))
    private int lowerCircling2(int constant) {
        return 5;
    }

    @ModifyConstant(method = "setAnchorAboveTarget", constant = @Constant(intValue = 20, ordinal = 0))
    private int lowerCircling3(int constant) {
        return 15;
    }

    @ModifyConstant(method = "setAnchorAboveTarget", constant = @Constant(intValue = 20, ordinal = 1))
    private int lowerCircling4(int constant) {
        return 5;
    }

}
