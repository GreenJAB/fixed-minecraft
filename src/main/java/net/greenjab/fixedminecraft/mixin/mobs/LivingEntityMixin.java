package net.greenjab.fixedminecraft.mixin.mobs;

import net.greenjab.fixedminecraft.data.ModTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unchecked")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Redirect(method = "getGroup", at = @At(value = "FIELD",
                                            target = "Lnet/minecraft/entity/EntityGroup;DEFAULT:Lnet/minecraft/entity/EntityGroup;"//, opcode = Opcodes.GETFIELD
    ))
    private EntityGroup moreArthropods(){
        LivingEntity LE = (LivingEntity)(Object)this;
        EntityType<?> EntityType = LE.getType();
        if (EntityType.isIn(ModTags.INSTANCE.getARTHROPODS())) {return EntityGroup.ARTHROPOD;}
        return EntityGroup.DEFAULT;
    }

    @Inject(method = "damage", at = @At(
            value = "HEAD"), cancellable = true)
    private void witherSkeletonIgnoreWither(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof WitherSkeletonEntity) {
            if (source.getAttacker() instanceof WitherEntity) cir.cancel();
        }
    }

    @Inject(method = "damage",at = @At( value = "TAIL" ))
    private void exitVehicleOnDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        if (amount <= 0) return;
        if (entity.isPlayer()) return;

        Entity vehicle = entity.getVehicle();
        if (vehicle == null) return;
        EntityType<?> vehicleType = vehicle.getType();

        if (vehicleType.isIn(ModTags.INSTANCE.getVEHICLES())) entity.stopRiding();
    }
}
