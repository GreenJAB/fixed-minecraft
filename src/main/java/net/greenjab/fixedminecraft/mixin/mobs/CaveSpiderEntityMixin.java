package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.entity.mob.CaveSpiderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CaveSpiderEntity.class)
public  class CaveSpiderEntityMixin {
    @ModifyConstant(method = "tryAttack", constant = @Constant(intValue = 20))
    private int lessPoisontime(int value) {
        return 10;
    }
}
