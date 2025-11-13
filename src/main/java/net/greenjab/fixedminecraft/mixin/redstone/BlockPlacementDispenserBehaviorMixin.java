package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.dispenser.BlockPlacementDispenserBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(BlockPlacementDispenserBehavior.class)
public abstract class BlockPlacementDispenserBehaviorMixin {

    @Inject(method = "dispenseSilently", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;isAir(Lnet/minecraft/util/math/BlockPos;)Z"
    ), cancellable = true)
    public void CauldronMixin(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir, @Local BlockPos blockPos) {
        World world = pointer.world();
        if (world.isClient())  return;
        if (!pointer.state().isOf(Blocks.DISPENSER))  return;
        if (!stack.isIn(ItemTags.SHULKER_BOXES)) return;
        if (stack.isOf(Items.SHULKER_BOX))  return;
        BlockState blockState = world.getBlockState(blockPos);
        if (!(blockState.getBlock() instanceof AbstractCauldronBlock cauldron)) return;
        if (cauldron.behaviorMap.map().containsKey(stack.getItem())) {
            PlayerEntity p = new PlayerEntity(world, new GameProfile(UUID.randomUUID(), "abc")) {
                @Override
                public @NotNull GameMode getGameMode() {
                    return GameMode.SURVIVAL;
                }
            };
            cauldron.behaviorMap.map().get(stack.getItem()).interact(blockState, world, blockPos, p, Hand.MAIN_HAND, stack);
            cir.setReturnValue(p.getStackInHand(Hand.MAIN_HAND));
        }
    }

}
