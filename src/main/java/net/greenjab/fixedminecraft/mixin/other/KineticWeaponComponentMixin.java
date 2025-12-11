package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.type.KineticWeaponComponent;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KineticWeaponComponent.class)
public abstract class KineticWeaponComponentMixin {

    @ModifyExpressionValue(method = "usageTick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/component/type/KineticWeaponComponent;getAmplifiedMovement(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/Vec3d;"
            ))
    private Vec3d dontUseYVel(Vec3d original) {
        return original.getHorizontal();
    }

    @ModifyExpressionValue(method = "usageTick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"
    ))
    private Vec3d dontUseYVel2(Vec3d original) {
        return original.getHorizontal();
    }

    @ModifyArg(method = "usageTick", at = @At(
            value = "INVOKE",
       target = "Lnet/minecraft/util/math/MathHelper;floor(D)I"
    ))
    private double lessDamageForOtherSpear(double value,
                                           @Local(argsOnly = true) ItemStack stack) {
        return value*(stack.isOf(ItemRegistry.SPEAR)?1:0.66);
    }


    @Inject(method = "usageTick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;pierce(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/entity/Entity;FZZZ)Z", shift = At.Shift.AFTER
    ), cancellable = true
    )
    private void stopAfter4Pierce(ItemStack stack, int remainingUseTicks, LivingEntity user, EquipmentSlot slot, CallbackInfo ci) {
        if(!stack.isOf(ItemRegistry.SPEAR)){
            int count = user.getPiercedEntityCount(entityx -> entityx instanceof LivingEntity);
            if (count>=4) {
                user.stopUsingItem();
                user.getEntityWorld().sendEntityStatus(user, EntityStatuses.KINETIC_ATTACK);
                if (user instanceof ServerPlayerEntity serverPlayerEntity) {
                    serverPlayerEntity.getItemCooldownManager().set(stack, 20);
                    Criteria.SPEAR_MOBS.trigger(serverPlayerEntity, count);
                }
                ci.cancel();
            }
        }
    }
}
