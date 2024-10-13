package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.entity.boss.dragon.phase.SittingAttackingPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SittingAttackingPhase.class)
public class SittingAttackingPhaseMixin {

    @ModifyConstant(method = "serverTick", constant = @Constant(intValue = 40))
    private int fasterBreath(int constant){
        return 15;
    }

}
