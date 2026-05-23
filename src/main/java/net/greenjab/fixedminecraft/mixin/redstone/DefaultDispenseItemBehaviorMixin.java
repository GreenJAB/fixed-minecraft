package net.greenjab.fixedminecraft.mixin.redstone;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(DefaultDispenseItemBehavior.class)
public abstract class DefaultDispenseItemBehaviorMixin {

    @Shadow
    protected abstract void addToInventoryOrDispense(BlockSource source, ItemStack itemStack);

    @Inject(at = @At("HEAD"), method = "execute", cancellable = true)
    public void CauldronMixin(BlockSource source, ItemStack dispensed, CallbackInfoReturnable<ItemStack> cir) {

        Level world = source.level();
        if (world.isClientSide())  return;
        if (!source.state().is(Blocks.DISPENSER))  return;

        BlockPos pos = BlockPos.containing(DispenserBlock.getDispensePosition(source));
        BlockState blockState = world.getBlockState(pos);

        if (!(blockState.getBlock() instanceof AbstractCauldronBlock cauldron)) return;
        if (cauldron.interactions.items.containsKey(dispensed.getItem())) {
            Player p = new Player(world, new GameProfile(UUID.randomUUID(), "abc")) {
                @Override
                public @NotNull GameType gameMode() {
                    return GameType.SURVIVAL;
                }
            };
            p.getInventory().setItem(0, dispensed);
            boolean b =cauldron.interactions.items.get(dispensed.getItem()).interact(blockState, world, pos, p, InteractionHand.MAIN_HAND, dispensed).consumesAction();
            if (!b) return;

            if (!(p.getInventory().getItem(1)).isEmpty()) {
                this.addToInventoryOrDispense(source, p.getInventory().getItem(1));
            }

            if (dispensed.is(ItemTags.CAULDRON_CAN_REMOVE_DYE)) {
                dispensed.remove(DataComponents.DYED_COLOR);
                cir.setReturnValue(dispensed);
                return;
            }
            cir.setReturnValue(p.getInventory().getItem(0));
        }
    }

}
