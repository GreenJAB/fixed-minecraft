package net.greenjab.fixedminecraft.mixin.food;

import net.greenjab.fixedminecraft.CustomData;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
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
    public abstract void readNbt(NbtCompound nbt);

    @Shadow
    private float exhaustion;

    @Shadow
    private float saturationLevel;

    @Shadow
    private int foodLevel;

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void dontCapSaturation(int food, float saturationModifier, CallbackInfo ci) {
        HungerManager instance = (HungerManager) (Object)this;
        instance.setFoodLevel(Math.min(food + instance.getFoodLevel(), 20));
        instance.setSaturationLevel(Math.min(instance.getSaturationLevel() + (float)food * saturationModifier * 2.0F, 20.0f));
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

        float h = player.isSneaking() ? 2.0f : 1.0f;
        System.out.println(this.exhaustion - lastExhaustion);
        if (Math.abs(this.exhaustion - lastExhaustion)<0.001f) {ticksSinceLastExhaustion = Math.min(ticksSinceLastExhaustion+(int)h, 40);
        } else {
            ticksSinceLastExhaustion = 0;
            lastExhaustion = this.exhaustion;
        }
        if (player.hurtTime>0) {
            ticksSinceLastExhaustion = 0;
        }
        if (this.saturationLevel < this.foodLevel) {
            if (ticksSinceLastExhaustion == 40) {
                h *=0.015f+this.saturationLevel/200.0f;
                this.saturationLevel = Math.min(this.saturationLevel+h,this.foodLevel);
                saturationSinceLastHunger += h  * (player.isSneaking() ? 2.0f : 1.0f);
                if (saturationSinceLastHunger >= 10) {
                    saturationSinceLastHunger = 0.0f;
                    this.foodLevel--;
                }
            }
        }

        CustomData.setData(player, "lastExhaustion", (int)(lastExhaustion*1000));
        CustomData.setData(player, "ticksSinceLastExhaustion", ticksSinceLastExhaustion);
        CustomData.setData(player, "saturationSinceLastHunger", (int)(saturationSinceLastHunger*1000));
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
        HungerManager HM = (HungerManager) (Object)this;
        if (HM.getFoodLevel()==0) return 80;
        return 30;
    }
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;canFoodHeal()Z"))
    private boolean needSaturationToHeal(PlayerEntity instance) {
        HungerManager HM = (HungerManager) (Object)this;
        if (instance.hurtTime>0) return false;
        return instance.canFoodHeal() && HM.getSaturationLevel()>6 &&
               (HM.getSaturationLevel()>=HM.getFoodLevel() || (instance.isSneaking()/*&& velocity<0.01f*/));
    }
    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"), index = 0)
    private float healFromHunger(float value) {
        return 3;
    }

}
