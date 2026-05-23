package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.world.entity.monster.spider.CaveSpider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CaveSpider.class)
public abstract class CaveSpiderMixin {
    @ModifyConstant(method = "doHurtTarget", constant = @Constant(intValue = 20))
    private int lessPoisontime(int value) {
        return 10;
    }
}
