package net.greenjab.fixedminecraft.mixin.villager;

import net.greenjab.fixedminecraft.registry.registries.MemoryRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.SleepInBed;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(SleepInBed.class)
public abstract class SleepInBedMixin {

    @Inject(method = "checkExtraStartConditions", at = @At("HEAD"), cancellable = true)
    private void requirePrivacy(ServerLevel level, LivingEntity body, CallbackInfoReturnable<Boolean> cir){
        List<Villager> list = body.level().getEntitiesOfClass(Villager.class, body.getBoundingBox().inflate(15, 5, 15), EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        int canSee = 0;
        for (Villager villager : list) {
            if (villager != body) {
                if (!villager.isBaby()&&!body.isBaby()) {
                    if (body.hasLineOfSight(villager)) canSee++;
                }
            }
        }
        if (canSee>1) cir.setReturnValue(false);
    }

    @Inject(method = "start", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;startSleeping(Lnet/minecraft/core/BlockPos;)V"
    ))
    private void resetSleepTimer(ServerLevel level, LivingEntity body, long timestamp, CallbackInfo ci){
        body.getBrain().setMemory(MemoryRegistry.TIME_SINCE_SLEEP, 0);
    }

}
