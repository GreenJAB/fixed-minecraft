package net.greenjab.fixedminecraft.mixin.redstone;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.UUID;

@Mixin(ItemDispenserBehavior.class)
public abstract class ItemDispenserBehaviorMixin  {

    @Inject(at = @At("HEAD"), method = "dispenseSilently", cancellable = true)
    public void CauldronMixin(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {

        World world = pointer.world();
        if (world.isClient)  return;
        if (!pointer.state().isOf(Blocks.DISPENSER))  return;

        BlockPos pos = BlockPos.ofFloored(DispenserBlock.getOutputLocation(pointer));
        BlockState blockState = world.getBlockState(pos);

        if (!(blockState.getBlock() instanceof AbstractCauldronBlock cauldron)) return;
        if (cauldron.behaviorMap.map().containsKey(stack.getItem())) {
            PlayerEntity p = new PlayerEntity(world, new GameProfile(UUID.randomUUID(), "abc")) {
                @Nullable
                @Override
                public GameMode getGameMode() {
                    return GameMode.SURVIVAL;
                }
            };
            cauldron.behaviorMap.map().get(stack.getItem()).interact(blockState, world, pos, p, Hand.MAIN_HAND, stack);
            cir.setReturnValue(p.getStackInHand(Hand.MAIN_HAND));
        }
    }

}
