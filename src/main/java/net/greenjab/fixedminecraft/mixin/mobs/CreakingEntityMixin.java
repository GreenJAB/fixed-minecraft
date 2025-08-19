package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.block.entity.CreakingHeartBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreakingEntity.class)
public abstract class CreakingEntityMixin {

    @Shadow
    public abstract boolean damage(ServerWorld world, DamageSource source, float amount);

    @Unique
    private static final TrackedData<Integer> DespawnTimer = DataTracker.registerData(CreakingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Inject(method = "initDataTracker", at = @At(value = "TAIL"))
    private void addDespawnData(DataTracker.Builder builder, CallbackInfo ci){
        builder.add(DespawnTimer, 0);
    }

    @Inject(method = "tickMovement", at = @At(value = "TAIL"))
    private void modifyDespawnData(CallbackInfo ci){
        CreakingEntity CE = (CreakingEntity)(Object)this;
        BlockPos home = CE.getHomePos();
        if (home!=null) {
            if (CE.getEntityWorld().getBlockEntity(home) instanceof CreakingHeartBlockEntity creakingHeartBlockEntity) {
                if (!CE.hasCustomName()) {
                    boolean root = CE.shouldBeUnrooted();
                    LivingEntity target = CE.getTarget();
                    if (root && target != null) {
                        float dist = CE.distanceTo(target);
                        if (dist > 10) {
                            int timer = CE.getDataTracker().get(DespawnTimer) + 1;
                            CE.getDataTracker().set(DespawnTimer, timer);
                            if (timer > 200) {
                                creakingHeartBlockEntity.killPuppet(CE.getDamageSources().cramming());
                            }
                            return;
                        }
                    }
                    if (target == null) {
                        if (CE.getEntityWorld().getTime() % 100 == 0) {
                            if (CE.getEntityWorld().random.nextInt(33) == 0) {
                                creakingHeartBlockEntity.killPuppet(CE.getDamageSources().cramming());
                            }
                        }
                    }
                    CE.getDataTracker().set(DespawnTimer, 0);
                }
            }
        }
    }

    @Inject(method = "createCreakingAttributes", at = @At(value = "HEAD"), cancellable = true)
    private static void slowerButStronger(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir){
        cir.setReturnValue(HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3F)
                .add(EntityAttributes.ATTACK_DAMAGE, 4.0)
                .add(EntityAttributes.FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.STEP_HEIGHT, 1.0625));
    }
}
