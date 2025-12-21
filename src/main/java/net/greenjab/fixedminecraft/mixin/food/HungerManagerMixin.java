package net.greenjab.fixedminecraft.mixin.food;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.CustomData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {

    @Shadow
    public float exhaustion;

    @Shadow
    private float saturationLevel;

    @Shadow
    private int foodLevel;

    @Inject(method = "addInternal", at = @At("HEAD"), cancellable = true)
    private void dontCapSaturation(int food, float saturationModifier, CallbackInfo ci) {
        HungerManager instance = (HungerManager) (Object)this;
        instance.setFoodLevel(MathHelper.clamp(food + instance.getFoodLevel(), 0, 20));
        instance.setSaturationLevel(MathHelper.clamp(instance.getSaturationLevel() + saturationModifier, 0, 20.0f));
        ci.cancel();
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void HungerToSaturation(PlayerEntity player, CallbackInfo ci) {

        int airTime = CustomData.getData(player, "airTime");// player.getWorld().getScoreboard().getOrCreateScore(player, player.getWorld().getScoreboard().getNullableObjective("airTime")).getScore();//.getNullableObjective("airTime").getScoreboard().get
        if (player.isOnGround()|| player.hasVehicle() || player.isClimbing() || player.isTouchingWater()) airTime=0;
        else if (player.getAbilities().flying) airTime = 10;
        else airTime++;
        if (player.isUsingRiptide()) airTime =20;
        CustomData.setData(player, "airTime", airTime);

        float lastExhaustion = CustomData.getData(player, "lastExhaustion")/1000.0f;
        int ticksSinceLastExhaustion = CustomData.getData(player, "ticksSinceLastExhaustion");
        float saturationSinceLastHunger = CustomData.getData(player, "saturationSinceLastHunger")/1000.0f;

        int staminaPause = 20;

        if (Math.abs(this.exhaustion - lastExhaustion)<0.001f || ticksSinceLastExhaustion<0) {
            if (saturationLevel != 0 || player.getWorld().getTime()%3==0)
                ticksSinceLastExhaustion = Math.min(ticksSinceLastExhaustion+1, staminaPause);
        } else {
            ticksSinceLastExhaustion = 0;
            lastExhaustion = this.exhaustion;
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

        if (player.hasStatusEffect(StatusEffects.SATURATION)) {
            if (player.getStatusEffect(StatusEffects.SATURATION).getAmplifier() > 0 && this.saturationLevel <= 1) {
                this.saturationLevel = 1;
                this.exhaustion = 0;
            }
        }

        if (saturationLevel == 0) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 10, 99, true, false, true));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 10, 99, true, false, true));
            if (player.isBlocking()) player.stopUsingItem();
            player.getItemCooldownManager().set(Items.SHIELD, 10);
        }

        CustomData.setData(player, "lastExhaustion", (int)(lastExhaustion*1000));
        CustomData.setData(player, "ticksSinceLastExhaustion", ticksSinceLastExhaustion);
        CustomData.setData(player, "saturationSinceLastHunger", (int)(saturationSinceLastHunger*1000));
    }

    @ModifyConstant(method = "update", constant = @Constant(floatValue = 4.0f))
    private float lessExhastion(float value) {
        return 0.5f;
    }
    @ModifyConstant(method = "update", constant = @Constant(floatValue = 1.0f, ordinal = 0))
    private float lessStaminaCost(float value) {
        return 0.5f;
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
    private int fasterHeal(int value, @Local(argsOnly = true) PlayerEntity player) {
        HungerManager HM = (HungerManager) (Object)this;
        if (HM.getFoodLevel()==0) return 80;
        return 20;
    }
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;canFoodHeal()Z"))
    private boolean needSaturationToHeal(PlayerEntity instance) {
        HungerManager HM = (HungerManager) (Object)this;
        if (instance.hurtTime>0) return false;
        return instance.canFoodHeal() && (instance.getHealth() <= instance.getMaxHealth()-1) && HM.getSaturationLevel()>3 &&
               (HM.getSaturationLevel()>=HM.getFoodLevel() || instance.isSneaking());
    }
    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"), index = 0)
    private float healFromHunger(float value) {
        return 3;
    }

}
