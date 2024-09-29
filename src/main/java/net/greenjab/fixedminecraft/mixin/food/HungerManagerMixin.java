package net.greenjab.fixedminecraft.mixin.food;

import net.greenjab.fixedminecraft.data.Saturation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
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


@SuppressWarnings("unchecked")
@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {

    @Shadow
    public abstract void readNbt(NbtCompound nbt);

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void dontCapSaturation(int food, float saturationModifier, CallbackInfo ci) {
        HungerManager instance = (HungerManager) (Object)this;
        instance.setFoodLevel(Math.min(food + instance.getFoodLevel(), 20));
        instance.setSaturationLevel(Math.min(instance.getSaturationLevel() + (float)food * saturationModifier * 2.0F, 20.0f));
        ci.cancel();
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void HungerToSaturation(PlayerEntity player, CallbackInfo ci) {
        Saturation.INSTANCE.hungerToSaturation(player, (HungerManager) (Object)this);
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
        //System.out.println(instance.getPos().toString());
        //System.out.println(Saturation.INSTANCE.getLastPos());
        //System.out.println(Saturation.INSTANCE.getLastPos().subtract(instance.getPos()).toString());
        Vec3d d = Saturation.INSTANCE.getLastPos2().subtract(instance.getPos());
        Saturation.INSTANCE.setLastPos2(Saturation.INSTANCE.getLastPos());
        Saturation.INSTANCE.setLastPos(instance.getPos());
        //System.out.println(d.horizontalLength());
        return instance.canFoodHeal() && HM.getSaturationLevel()>6 &&
               (HM.getSaturationLevel()>=HM.getFoodLevel() || (instance.isSneaking()&&d.horizontalLength()<0.01f));
    }
    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"), index = 0)
    private float healFromHunger(float value) {
        return 3;
    }

}
