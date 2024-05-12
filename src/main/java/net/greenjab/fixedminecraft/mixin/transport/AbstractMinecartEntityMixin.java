package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.block.CopperRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends VehicleEntity {
    public AbstractMinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }
    @Redirect(method = "moveOnRail", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"))
    private void modifyMaxVelocity(AbstractMinecartEntity instance, MovementType movementType, Vec3d vec3d, @Local(argsOnly = true) BlockState state) {
        double v = 8.0;
        if (state.getBlock() instanceof CopperRailBlock) v=CopperRailBlock.getMaxVelocity(state);
        if (instance.isTouchingWater()) v/=2.0;
        v/=20.0;

        double t = this.hasPassengers() ? 0.75 : 1.0;
        Vec3d vec3d2 = this.getVelocity();

        vec3d2 = new Vec3d(MathHelper.clamp(vec3d2.x, -v, v), 0.0, MathHelper.clamp(vec3d2.z, -v, v));
        instance.setVelocity(vec3d2);
        this.move(MovementType.SELF, new Vec3d(t * vec3d2.x, 0.0, t * vec3d2.z));
    }
}
