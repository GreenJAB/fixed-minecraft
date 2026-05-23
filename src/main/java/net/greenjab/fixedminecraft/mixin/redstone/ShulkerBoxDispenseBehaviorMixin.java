package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ShulkerBoxDispenseBehavior.class)
public abstract class ShulkerBoxDispenseBehaviorMixin {

    @Inject(method = "execute", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;isEmptyBlock(Lnet/minecraft/core/BlockPos;)Z"
    ), cancellable = true)
    public void CauldronMixin(BlockSource source, ItemStack dispensed, CallbackInfoReturnable<ItemStack> cir, @Local BlockPos relativePos) {
        Level world = source.level();
        if (world.isClientSide())  return;
        if (!source.state().is(Blocks.DISPENSER))  return;
        if (!dispensed.is(ItemTags.SHULKER_BOXES)) return;
        if (dispensed.is(Items.SHULKER_BOX))  return;
        BlockState blockState = world.getBlockState(relativePos);
        if (!(blockState.getBlock() instanceof AbstractCauldronBlock cauldron)) return;
        if (cauldron.interactions.items.containsKey(dispensed.getItem())) {
            Player p = new Player(world, new GameProfile(UUID.randomUUID(), "abc")) {
                @Override
                public @NotNull GameType gameMode() {
                    return GameType.SURVIVAL;
                }
            };
            cauldron.interactions.items.get(dispensed.getItem()).interact(blockState, world, relativePos, p, InteractionHand.MAIN_HAND, dispensed);
            cir.setReturnValue(p.getItemInHand(InteractionHand.MAIN_HAND));
        }
    }

}
