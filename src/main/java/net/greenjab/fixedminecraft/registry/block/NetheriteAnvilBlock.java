package net.greenjab.fixedminecraft.registry.block;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class NetheriteAnvilBlock extends AnvilBlock {
    public NetheriteAnvilBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        for (ItemStack itemStack : player.getHandItems()) {
            if (itemStack.isOf(Items.NETHERITE_INGOT)) {
                if (state.isOf(BlockRegistry.CHIPPED_NETHERITE_ANVIL)) {
                    world.setBlockState(pos, BlockRegistry.NETHERITE_ANVIL.getStateWithProperties(state), Block.NOTIFY_ALL_AND_REDRAW);
                    world.playSound(player, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, BlockRegistry.NETHERITE_ANVIL.getStateWithProperties(state)));
                    itemStack.decrementUnlessCreative(1, player);
                    return ActionResult.SUCCESS;
                }
                if (state.isOf(BlockRegistry.DAMAGED_NETHERITE_ANVIL)) {
                    world.setBlockState(pos, BlockRegistry.CHIPPED_NETHERITE_ANVIL.getStateWithProperties(state), Block.NOTIFY_ALL_AND_REDRAW);
                    world.playSound(player, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, BlockRegistry.CHIPPED_NETHERITE_ANVIL.getStateWithProperties(state)));
                    itemStack.decrementUnlessCreative(1, player);
                    return ActionResult.SUCCESS;
                }
            }
        }

        if (!world.isClient) {
            player.addCommandTag("netherite_anvil");
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(Stats.INTERACT_WITH_ANVIL);
        } else {
            FixedMinecraft.INSTANCE.setNetheriteAnvil(true);
        }

        return ActionResult.SUCCESS;
    }
}
