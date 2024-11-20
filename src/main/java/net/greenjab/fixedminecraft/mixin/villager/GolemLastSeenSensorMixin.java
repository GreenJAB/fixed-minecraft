package net.greenjab.fixedminecraft.mixin.villager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GolemLastSeenSensor.class)
public abstract class GolemLastSeenSensorMixin extends Sensor<LivingEntity> {
    @ModifyArg(method = "rememberIronGolem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;remember(Lnet/minecraft/entity/ai/brain/MemoryModuleType;Ljava/lang/Object;J)V"), index = 2)
    private static long only20Seconds(long expiry) {
        return 399L;
    }
}
