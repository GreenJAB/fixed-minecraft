package net.greenjab.fixedminecraft.mixin.redstone;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.UUID;

@Mixin(ItemDispenserBehavior.class)
public abstract class ItemDispenserBehaviorMixin  {

    @Shadow
    protected abstract void addStackOrSpawn(BlockPointer pointer, ItemStack stack);

    @Inject(at = @At("HEAD"), method = "dispenseSilently", cancellable = true)
    public void CauldronMixin(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {

        World world = pointer.world();
        if (world.isClient())  return;
        if (!pointer.state().isOf(Blocks.DISPENSER))  return;

        BlockPos pos = BlockPos.ofFloored(DispenserBlock.getOutputLocation(pointer));
        BlockState blockState = world.getBlockState(pos);

        if (!(blockState.getBlock() instanceof AbstractCauldronBlock cauldron)) return;
        if (cauldron.behaviorMap.map().containsKey(stack.getItem())) {
            PlayerEntity p = new PlayerEntity(world, new GameProfile(UUID.randomUUID(), "abc")) {
                @Override
                public @NotNull GameMode getGameMode() {
                    return GameMode.SURVIVAL;
                }
            };
            p.getInventory().setStack(0, stack);
            boolean b =cauldron.behaviorMap.map().get(stack.getItem()).interact(blockState, world, pos, p, Hand.MAIN_HAND, stack).isAccepted();
            if (!b) return;

            if (!(p.getInventory().getStack(1)).isEmpty()) {
                this.addStackOrSpawn(pointer, p.getInventory().getStack(1));
            }

            if (stack.isIn(ItemTags.DYEABLE)) {
                stack.remove(DataComponentTypes.DYED_COLOR);
                cir.setReturnValue(stack);
                return;
            }
            cir.setReturnValue(p.getInventory().getStack(0));
        }
    }

}
