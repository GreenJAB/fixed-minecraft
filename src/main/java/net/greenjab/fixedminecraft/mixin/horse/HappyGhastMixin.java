package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HappyGhast.class)
public abstract class HappyGhastMixin {

    @ModifyExpressionValue(method = "travel", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/happyghast/HappyGhast;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double happyGhastSwiftness(double constant){
        HappyGhast HG = (HappyGhast) (Object)this;
        if (HG.hasEffect(MobEffects.SPEED)) return constant * (1+0.2*(1+HG.getEffect(MobEffects.SPEED).getAmplifier()));
        return constant;
    }
}
