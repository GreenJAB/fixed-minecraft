package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.IntUnaryOperator;

@Mixin(SkeletonHorseEntity.class)
public abstract class SkeletonHorseEntityMixin {

    @Inject(method = "initAttributes", at = @At(value = "TAIL"))
    private void zombieHorse(Random random, CallbackInfo ci){
        SkeletonHorseEntity SHE = (SkeletonHorseEntity)(Object)this;
        EntityAttributeInstance var10000 = SHE.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        Objects.requireNonNull(random);
        var10000.setBaseValue(getChildHealthBonus(random::nextInt));
        var10000 = SHE.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        Objects.requireNonNull(random);
        var10000.setBaseValue(getChildMovementSpeedBonus(random::nextDouble));
    }

    @Unique
    private float getChildHealthBonus(IntUnaryOperator randomIntGetter) {
        return 15.0F + (float)randomIntGetter.applyAsInt(8) + (float)randomIntGetter.applyAsInt(9);
    }

    @Unique
    private double getChildMovementSpeedBonus(DoubleSupplier randomDoubleGetter) {
        return (0.44999998807907104 + randomDoubleGetter.getAsDouble() * 0.3 + randomDoubleGetter.getAsDouble() * 0.3 + randomDoubleGetter.getAsDouble() * 0.3) * 0.25;
    }
}
