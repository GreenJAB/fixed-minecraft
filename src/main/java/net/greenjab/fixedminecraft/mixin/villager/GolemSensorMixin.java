package net.greenjab.fixedminecraft.mixin.villager;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.GolemSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GolemSensor.class)
public abstract class GolemSensorMixin extends Sensor<LivingEntity> {
    @ModifyArg(method = "golemDetected", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/Brain;setMemoryWithExpiry(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/lang/Object;J)V"), index = 2)
    private static long only20Seconds(long expiry) {
        return 399L;
    }
}
