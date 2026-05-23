package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.KineticWeapon;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KineticWeapon.class)
public abstract class KineticWeaponMixin {

    @ModifyExpressionValue(method = "damageEntities", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/component/KineticWeapon;getMotion(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/Vec3;"
            ))
    private Vec3 dontUseYVel(Vec3 original) {
        return original.horizontal();
    }

    @ModifyExpressionValue(method = "damageEntities", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getLookAngle()Lnet/minecraft/world/phys/Vec3;"
    ))
    private Vec3 dontUseYVel2(Vec3 original) {
        return original.horizontal();
    }

    @ModifyArg(method = "damageEntities", at = @At(
            value = "INVOKE",
       target = "Lnet/minecraft/util/Mth;floor(D)I"
    ))
    private double lessDamageForOtherSpear(double value,
                                           @Local(argsOnly = true) ItemStack stack) {
        return value*(stack.is(ItemRegistry.SPEAR)?1:0.66);
    }

    @Inject(method = "damageEntities", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;stabAttack(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/Entity;FZZZ)Z", shift = At.Shift.AFTER
    ), cancellable = true
    )
    private void stopAfter4Pierce(ItemStack stack, int ticksRemaining, LivingEntity livingEntity, EquipmentSlot equipmentSlot, CallbackInfo ci) {
        if(!stack.is(ItemRegistry.SPEAR)){
            int count = livingEntity.stabbedEntities(entityx -> entityx instanceof LivingEntity);
            if (count>=4) {
                livingEntity.releaseUsingItem();
                livingEntity.level().broadcastEntityEvent(livingEntity, EntityEvent.KINETIC_HIT);
                if (livingEntity instanceof ServerPlayer serverPlayerEntity) {
                    serverPlayerEntity.getCooldowns().addCooldown(stack, 20);
                    CriteriaTriggers.SPEAR_MOBS_TRIGGER.trigger(serverPlayerEntity, count);
                }
                ci.cancel();
            }
        }
    }
}
