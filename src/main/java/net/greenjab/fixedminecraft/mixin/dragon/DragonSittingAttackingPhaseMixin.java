package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingAttackingPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DragonSittingAttackingPhase.class)
public abstract class DragonSittingAttackingPhaseMixin {

    @ModifyConstant(method = "doServerTick", constant = @Constant(intValue = 40))
    private int fasterBreath(int constant){
        return 15;
    }

}
