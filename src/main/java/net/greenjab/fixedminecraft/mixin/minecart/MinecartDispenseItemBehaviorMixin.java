package net.greenjab.fixedminecraft.mixin.minecart;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.MinecartDispenseItemBehavior;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartDispenseItemBehavior.class)
public abstract class MinecartDispenseItemBehaviorMixin {

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void furnaceMinecartFaceAwayFromDispenser(BlockSource source, ItemStack dispensed, CallbackInfoReturnable<ItemStack> cir,
                                                      @Local AbstractMinecart minecart){
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        if (direction == Direction.NORTH || direction == Direction.WEST) {
            minecart.setYRot((minecart.getYRot() + 180) % 360);
            minecart.setDeltaMovement(0, 0.001f, 0);
            minecart.setFlipped(true);
        }
    }
}
