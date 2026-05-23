package net.greenjab.fixedminecraft.mixin.horse;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.IntUnaryOperator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;

@Mixin(SkeletonHorse.class)
public abstract class SkeletonHorseMixin {

    @Inject(method = "randomizeAttributes", at = @At(value = "TAIL"))
    private void randomisedAttributes(RandomSource random, CallbackInfo ci){
        SkeletonHorse SHE = (SkeletonHorse)(Object)this;
        AttributeInstance attrribute = SHE.getAttribute(Attributes.MAX_HEALTH);
        Objects.requireNonNull(random);
        Objects.requireNonNull(attrribute);
        attrribute.setBaseValue(getChildHealthBonus(random::nextInt));
        attrribute = SHE.getAttribute(Attributes.MOVEMENT_SPEED);
        Objects.requireNonNull(random);
        Objects.requireNonNull(attrribute);
        attrribute.setBaseValue(getChildMovementSpeedBonus(random::nextDouble));
    }

    @Unique
    private float getChildHealthBonus(IntUnaryOperator randomIntGetter) {
        return 15.0F + (float)randomIntGetter.applyAsInt(8) + (float)randomIntGetter.applyAsInt(9);
    }

    @Unique
    private double getChildMovementSpeedBonus(DoubleSupplier randomDoubleGetter) {
        return (0.44999998807907104 + randomDoubleGetter.getAsDouble() * 0.3 + randomDoubleGetter.getAsDouble() * 0.3 + randomDoubleGetter.getAsDouble() * 0.3) * 0.25;
    }

    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/equine/SkeletonHorse;isTamed()Z"))
    private boolean allowRiding(SkeletonHorse instance){
        return true;
    }
}
