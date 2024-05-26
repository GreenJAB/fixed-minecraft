package net.greenjab.fixedminecraft.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BeaconBlockEntity;
//import net.minecraft.entity.effect.SaturationStatusEffect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin {

    /*@Inject(method = "update", at = @At("HEAD"))
    public void ModifySaturation(LivingEntity entity, Runnable overwriteCallback, CallbackInfoReturnable<Boolean> cir) {
        StatusEffectInstance SEI = (StatusEffectInstance)(Object)this;

        //System.out.println(SEI.toString());
        //System.out.println(SEI.getEffectType().toString());
        //System.out.println(SEI.getEffectType().getName().toString());
        boolean b = SEI.toString().contains("saturation");
        System.out.println(b);
    }*/
   /* @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void ModifySaturation(LivingEntity entity, Runnable overwriteCallback, CallbackInfoReturnable<Boolean> cir) {
        StatusEffectInstance SEI = (StatusEffectInstance)(Object)this;

            int i = SEI.isInfinite() ? entity.age : SEI.getDuration();
            if (SEI.getEffectType().canApplyUpdateEffect(i, SEI.getAmplifier())) {
                SEI.getEffectType().applyUpdateEffect(entity, SEI.getAmplifier());
            }
    }*/

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;canApplyUpdateEffect(II)Z"))
    private boolean injected(StatusEffect instance, int duration, int amplifier) {
        StatusEffectInstance SEI = (StatusEffectInstance)(Object)this;
        boolean b = instance.toString().contains("Saturation");
        if (b) {
            int i = 100 >> amplifier;
            if (i > 0) {
                return duration % i == 0;
            } else {
                return true;
            }
        } else {
            boolean bb = SEI.getEffectType().canApplyUpdateEffect(duration, amplifier);
            System.out.println(bb);
            return bb;

        }
        //return false;
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;applyUpdateEffect(Lnet/minecraft/entity/LivingEntity;I)V"))
    private void injected2(StatusEffect instance, LivingEntity entity, int amplifier) {
        StatusEffectInstance SEI = (StatusEffectInstance)(Object)this;
        boolean b = instance.toString().contains("Saturation");
        if (b) {
            if (!entity.getWorld().isClient && entity instanceof PlayerEntity playerEntity) {
                playerEntity.getHungerManager().add(amplifier + 1, 0.0F);
            }
        } else {
            SEI.getEffectType().applyUpdateEffect(entity,amplifier);
        }
    }
}
