package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Entity.class)
public abstract class EntityMixin {

    @ModifyVariable(method = "calculateDimensions", at = @At(value = "STORE"), ordinal = 1)
    private EntityDimensions smallerHorseInBoat(EntityDimensions original){
        Entity E = (Entity)(Object)this;
        if (E instanceof AbstractHorseEntity) {
            if (E.hasVehicle()) {
                return new EntityDimensions(original.width*0.9f, original.height, original.fixed);
            }
        }

        return original;
    }
}
