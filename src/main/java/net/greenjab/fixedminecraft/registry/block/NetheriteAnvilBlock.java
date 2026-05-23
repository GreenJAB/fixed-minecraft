package net.greenjab.fixedminecraft.registry.block;

import net.greenjab.fixedminecraft.registry.other.NewAnvilMenu;
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class NetheriteAnvilBlock extends AnvilBlock {
    public NetheriteAnvilBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos, Player player, @NonNull BlockHitResult hit) {
        ItemStack[] items = {player.getMainHandItem(), player.getOffhandItem()};
        for (ItemStack itemStack : items) {
            if (itemStack.is(Items.NETHERITE_INGOT)) {
                if (state.is(BlockRegistry.CHIPPED_NETHERITE_ANVIL)) {
                    world.setBlock(pos, BlockRegistry.NETHERITE_ANVIL.withPropertiesOf(state), Block.UPDATE_ALL_IMMEDIATE);
                    world.playSound(player, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, BlockRegistry.NETHERITE_ANVIL.withPropertiesOf(state)));
                    itemStack.consume(1, player);
                    return InteractionResult.SUCCESS;
                }
                if (state.is(BlockRegistry.DAMAGED_NETHERITE_ANVIL)) {
                    world.setBlock(pos, BlockRegistry.CHIPPED_NETHERITE_ANVIL.withPropertiesOf(state), Block.UPDATE_ALL_IMMEDIATE);
                    world.playSound(player, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, BlockRegistry.CHIPPED_NETHERITE_ANVIL.withPropertiesOf(state)));
                    itemStack.consume(1, player);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (!world.isClientSide()) {
            player.openMenu(state.getMenuProvider(world, pos));
            player.awardStat(Stats.INTERACT_WITH_ANVIL);
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    protected MenuProvider getMenuProvider(final @NonNull BlockState state, final @NonNull Level level, final @NonNull BlockPos pos) {
        return new SimpleMenuProvider(
                 (containerId, inventory, _) -> new NewAnvilMenu(containerId, inventory, ContainerLevelAccess.create(level, pos), true),
                Component.translatable("container.netherite_anvil")
        );
    }
}
