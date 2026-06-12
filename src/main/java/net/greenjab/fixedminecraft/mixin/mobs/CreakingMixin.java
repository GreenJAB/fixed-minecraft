package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Creaking.class)
public abstract class CreakingMixin {

    @Unique
    private static final EntityDataAccessor<Integer> DespawnTimer = SynchedEntityData.defineId(Creaking.class, EntityDataSerializers.INT);
    @Inject(method = "defineSynchedData", at = @At(value = "TAIL"))
    private void addDespawnData(SynchedEntityData.Builder entityData, CallbackInfo ci){
        entityData.define(DespawnTimer, 0);
    }

    @Inject(method = "aiStep", at = @At(value = "TAIL"))
    private void modifyDespawnData(CallbackInfo ci){
        Creaking CE = (Creaking)(Object)this;
        BlockPos home = CE.getHomePos();
        if (home!=null) {
            if (CE.level().getBlockEntity(home) instanceof CreakingHeartBlockEntity creakingHeartBlockEntity) {
                if (!CE.hasCustomName()) {
                    boolean root = CE.checkCanMove();
                    LivingEntity target = CE.getTarget();
                    if (root && target != null) {
                        float dist = CE.distanceTo(target);
                        if (dist > 10) {
                            int timer = CE.getEntityData().get(DespawnTimer) + 1;
                            CE.getEntityData().set(DespawnTimer, timer);
                            if (timer > 200) {
                                creakingHeartBlockEntity.removeProtector(CE.damageSources().cramming());
                            }
                            return;
                        }
                    }
                    if (target == null) {
                        if (CE.level().getGameTime() % 100 == 0) {
                            if (CE.level().getRandom().nextInt(33) == 0) {
                                creakingHeartBlockEntity.removeProtector(CE.damageSources().cramming());
                            }
                        }
                    }
                    CE.getEntityData().set(DespawnTimer, 0);
                }
            }
        }
    }

    @Inject(method = "createAttributes", at = @At(value = "HEAD"), cancellable = true)
    private static void slowerButStronger(CallbackInfoReturnable<AttributeSupplier.Builder> cir){
        cir.setReturnValue(Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.STEP_HEIGHT, 1.0625));
    }
}
