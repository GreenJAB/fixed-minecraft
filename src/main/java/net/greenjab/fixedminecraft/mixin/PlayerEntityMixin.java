package net.greenjab.fixedminecraft.mixin;

import kotlin.jvm.JvmStatic;
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unchecked")
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "checkFallFlying", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfoReturnable cir) {
        PlayerEntity instance = (PlayerEntity)(Object)this;
        if (instance.isWet() || instance.isInLava()) {
            cir.setReturnValue(false);
        }
    }

    /*@Inject(method = "getReachDistance", at = @At("HEAD"), cancellable = true)
    private void hasLongerReach(boolean creative, CallbackInfoReturnable<Float> cir) {
        PlayerEntity instance = (PlayerEntity)(Object)this;
        int level = instance.getStatusEffect(StatusRegistry.INSTANCE.getREACH()).getAmplifier(); //TODO get instance of player
        //cir.setReturnValue(10f);
        cir.setReturnValue((creative ? 5.0F : 4.5F)+(level+1)*0.5f);
    }*/


}
