package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Creeper.class)
public abstract class CreeperMixin {

    @WrapOperation(method = "spawnLingeringCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)V"))
    private void notInfiniteEffect(AreaEffectCloud instance, MobEffectInstance effect, Operation<Void> original){
        if (effect.getDuration() == -1) effect = new MobEffectInstance(effect.getEffect(), 20 * 300, effect.getAmplifier());
        original.call(instance, effect);
    }

    @ModifyArg(method = "spawnLingeringCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setRadiusOnUse(F)V"))
    private float noShrinkOnUse(float radiusOnUse){
        return 0;
    }

}
