package net.greenjab.fixedminecraft.mixin.enchanting;

import net.greenjab.fixedminecraft.registry.other.NewAnvilMenu;
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public abstract class AnvilBlockMixin {

    @Unique
    private static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private static void damageNetheriteAnvil(BlockState blockState, CallbackInfoReturnable<BlockState> cir) {

        if (blockState.is(BlockRegistry.NETHERITE_ANVIL)) {
            cir.setReturnValue(BlockRegistry.CHIPPED_NETHERITE_ANVIL
                    .defaultBlockState()
                    .setValue(FACING, blockState.getValue(FACING)));
        }
        if (blockState.is(BlockRegistry.CHIPPED_NETHERITE_ANVIL)) {
            cir.setReturnValue(BlockRegistry.DAMAGED_NETHERITE_ANVIL
                    .defaultBlockState()
                    .setValue(FACING, blockState.getValue(FACING)));
        }
    }

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;"))
    private void setNormalAnvilServer(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult,
                                      CallbackInfoReturnable<InteractionResult> cir){
        player.removeTag("netherite_anvil");
    }

    @Inject(method = "useWithoutItem", at = @At(value = "HEAD"), cancellable = true)
    private void repairAnvil(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult,
                             CallbackInfoReturnable<InteractionResult> cir){
        ItemStack[] items = {player.getMainHandItem(), player.getOffhandItem()};
        for (ItemStack itemStack: items) {
            if (itemStack.is(Items.IRON_BLOCK)) {
                if (state.is(Blocks.CHIPPED_ANVIL)) {
                    level.setBlock(pos, Blocks.ANVIL.withPropertiesOf(state), Block.UPDATE_ALL_IMMEDIATE);
                    level.playSound(player, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, Blocks.ANVIL.withPropertiesOf(state)));
                    itemStack.consume(1, player);
                    cir.setReturnValue(InteractionResult.SUCCESS);

                    cir.cancel();
                }
                if (state.is(Blocks.DAMAGED_ANVIL)) {
                    level.setBlock(pos, Blocks.CHIPPED_ANVIL.withPropertiesOf(state), Block.UPDATE_ALL_IMMEDIATE);
                    level.playSound(player, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, Blocks.CHIPPED_ANVIL.withPropertiesOf(state)));
                    itemStack.consume(1, player);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                    cir.cancel();
                }
            }
        }
    }

    @Inject(method = "getMenuProvider", at = @At(value = "HEAD"), cancellable = true)
    private void newAnvilMenu(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<MenuProvider> cir) {
        cir.setReturnValue( new SimpleMenuProvider(
                (containerId, inventory, _) -> new NewAnvilMenu(containerId, inventory, ContainerLevelAccess.create(level, pos), false),
                Component.translatable("container.anvil")
        ));
    }
}
