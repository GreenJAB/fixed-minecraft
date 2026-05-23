package net.greenjab.fixedminecraft.mixin.food;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.CustomData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Shadow
    private float exhaustionLevel;

    @Shadow
    private float saturationLevel;

    @Shadow
    private int foodLevel;

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void dontCapSaturation(int food, float saturation, CallbackInfo ci) {
        FoodData instance = (FoodData) (Object)this;
        instance.setFoodLevel(Mth.clamp(food + instance.getFoodLevel(), 0, 20));
        instance.setSaturation(Mth.clamp(instance.getSaturationLevel() + saturation, 0, 20.0f));
        ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void HungerToSaturation(ServerPlayer player, CallbackInfo ci) {

        int airTime = CustomData.getData(player, "airTime");// player.getEntityWorld().getScoreboard().getOrCreateScore(player, player.getEntityWorld().getScoreboard().getNullableObjective("airTime")).getScore();//.getNullableObjective("airTime").getScoreboard().get
        if (player.onGround()|| player.isPassenger() || player.onClimbable() || player.isInWater()) airTime=0;
        else if (player.getAbilities().flying) airTime = 10;
        else airTime++;
        if (player.isAutoSpinAttack()) airTime =20;
        CustomData.setData(player, "airTime", airTime);

        float lastExhaustion = CustomData.getData(player, "lastExhaustion")/1000.0f;
        int ticksSinceLastExhaustion = CustomData.getData(player, "ticksSinceLastExhaustion");
        float saturationSinceLastHunger = CustomData.getData(player, "saturationSinceLastHunger")/1000.0f;

        int staminaPause = 20;

        if (Math.abs(this.exhaustionLevel - lastExhaustion)<0.001f || ticksSinceLastExhaustion<0) {
            if (saturationLevel != 0 || player.level().getGameTime()%3==0)
                ticksSinceLastExhaustion = Math.min(ticksSinceLastExhaustion+1, staminaPause);
        } else {
            ticksSinceLastExhaustion = 0;
            lastExhaustion = this.exhaustionLevel;
        }
        if (player.hurtTime>0) {
            ticksSinceLastExhaustion = 0;
        }
        if (this.saturationLevel < this.foodLevel) {
            if (ticksSinceLastExhaustion == staminaPause) {
                float h = 0.03f + this.saturationLevel / 100.0f;
                this.saturationLevel = Math.min(this.saturationLevel + h, this.foodLevel);
                saturationSinceLastHunger += h;
                if (saturationSinceLastHunger >= 8) {
                    saturationSinceLastHunger = 0.0f;
                    this.foodLevel--;
                }
            }
        }

        if (player.hasEffect(MobEffects.SATURATION)) {
            if (player.getEffect(MobEffects.SATURATION).getAmplifier() > 0 && this.saturationLevel <= 1) {
                this.saturationLevel = 1;
                this.exhaustionLevel = 0;
            }
        }

        if (saturationLevel == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 10, 99, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10, 99, true, false, true));
            if (player.isBlocking()) player.releaseUsingItem();
            player.getCooldowns().addCooldown(Items.SHIELD.getDefaultInstance(), 10);
        }

        CustomData.setData(player, "lastExhaustion", (int)(lastExhaustion*1000));
        CustomData.setData(player, "ticksSinceLastExhaustion", ticksSinceLastExhaustion);
        CustomData.setData(player, "saturationSinceLastHunger", (int)(saturationSinceLastHunger*1000));
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 4.0f))
    private float lessExhastion(float value) {
        return 0.5f;
    }
    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 1.0f, ordinal = 0))
    private float lessStaminaCost(float value) {
        return 0.5f;
    }
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 20))
    private int noQuickHeal(int value) {
        return 20000;
    }
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 18))
    private int dontNeedHungerToHeal(int value) {
        return 0;
    }
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 80))
    private int fasterHeal(int value) {
        FoodData HM = (FoodData) (Object)this;
        if (HM.getFoodLevel()==0) return 80;
        return 20;
    }
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isHurt()Z"))
    private boolean needSaturationToHeal(ServerPlayer instance) {
        FoodData HM = (FoodData) (Object)this;
        if (instance.hurtTime>0) return false;
        return instance.isHurt() && (instance.getHealth() <= instance.getMaxHealth()-1) && HM.getSaturationLevel()>3 &&
               (HM.getSaturationLevel()>=HM.getFoodLevel() || instance.isShiftKeyDown());
    }
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"), index = 0)
    private float healFromHunger(float value) {
        return 3;
    }

    @Inject(method = "hasEnoughFood", at = @At("HEAD"), cancellable = true)
    private void cancelSprintAt0Saturation(CallbackInfoReturnable<Boolean> cir) {
        FoodData HM = (FoodData) (Object)this;
        cir.setReturnValue(HM.getSaturationLevel() > 0.0F);
    }

}
