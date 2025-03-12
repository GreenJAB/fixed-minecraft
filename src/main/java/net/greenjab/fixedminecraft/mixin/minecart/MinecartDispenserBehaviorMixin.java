package net.greenjab.fixedminecraft.mixin.minecart;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.MinecartDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartDispenserBehavior.class)
public class MinecartDispenserBehaviorMixin {

    @Inject(method = "dispenseSilently", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private void furnaceMinecartFaceAwayFromDispenser(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir, @Local AbstractMinecartEntity abstractMinecartEntity){
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        if (direction == Direction.NORTH || direction == Direction.WEST) {
            abstractMinecartEntity.setYaw((abstractMinecartEntity.getYaw()+180)%360);
            abstractMinecartEntity.setVelocity(0, 0.001f, 0);
            abstractMinecartEntity.setYawFlipped(true);
        }
    }
}
