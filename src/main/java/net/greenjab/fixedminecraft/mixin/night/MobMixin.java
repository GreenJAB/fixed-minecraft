package net.greenjab.fixedminecraft.mixin.night;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.equine.ZombieHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "finalizeSpawn", at=@At(value = "HEAD"))
    private void addNightTag(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData,
                             CallbackInfoReturnable<SpawnGroupData> cir){
        Mob LE = (Mob)(Object)this;
        if (LE instanceof Monster HE) {
            if (level.getBrightness(LightLayer.SKY, HE.blockPosition()) > 10 && level.getSkyDarken() >= 5) {
                HE.addTag("night");
            }
        }
    }

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;burnUndead()V"))
    private void dontBurnPhantom(Mob entity, Operation<Void> original) {
        if (!(entity instanceof Phantom || entity instanceof ZombieHorse))original.call(entity);
    }
}
