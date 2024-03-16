package net.greenjab.fixedminecraft.mixin;

import net.greenjab.fixedminecraft.data.Saturation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unchecked")
@Mixin(HungerManager.class)
public class HungerManagerMixin {

    /*@Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfoReturnable cir) {
        PlayerEntity instance = (PlayerEntity)(Object)this;
        if (instance.isWet() || instance.isInLava()) {
            cir.setReturnValue(false);
        }
    }*/
    /*@Inject(method = "eat", at = @At("HEAD"))//at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getFoodComponent()Lnet/minecraft/item/FoodComponent"))
    private void eatExhastion(CallbackInfo ci) {
        ((HungerManager) (Object)this).addExhaustion(0.005f);
    }*/

    @Inject(method = "update", at = @At("HEAD"))
    private void HungerToSaturation(PlayerEntity player, CallbackInfo ci) {
        Saturation.INSTANCE.HungerToSaturation(player, (HungerManager) (Object)this);
    }

    @ModifyConstant(method = "update", constant = @Constant(floatValue = 4.0f))
    private float lessExhastion(float value) {
        return 1.0f;
    }
    @ModifyConstant(method = "update", constant = @Constant(intValue = 20))
    private int noQuickHeal(int value) {
        return 20000;
    }
    @ModifyConstant(method = "update", constant = @Constant(intValue = 18))
    private int dontNeedHungerToHeal(int value) {
        return 0;
    }
    @ModifyConstant(method = "update", constant = @Constant(intValue = 80))
    private int fasterHeal(int value) {
        return 40;
    }
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;canFoodHeal()Z"))
    private boolean needSaturationToHeal(PlayerEntity instance) {
        return instance.canFoodHeal() &&((HungerManager) (Object)this).getSaturationLevel()>10;
    }
    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"), index = 0)
    private float healFromHunger(float value) {
        return 4;
    }

}
