package net.greenjab.fixedminecraft.registry.other;

import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class RepairAnvilDispenserBehaviour extends DefaultDispenseItemBehavior {

    public RepairAnvilDispenserBehaviour() {
    }

    @Override
    public ItemStack execute(final BlockSource source, final ItemStack dispensed) {
        ServerLevel level = source.level();
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        BlockPos pos = source.pos().relative(direction);
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof AnvilBlock) {
            if (dispensed.is(Items.IRON_BLOCK)) {
                if (state.is(Blocks.CHIPPED_ANVIL)) {
                    level.setBlock(pos, Blocks.ANVIL.withPropertiesOf(state), Block.UPDATE_ALL_IMMEDIATE);
                    level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(null, Blocks.ANVIL.withPropertiesOf(state)));
                    dispensed.shrink(1);
                }
                if (state.is(Blocks.DAMAGED_ANVIL)) {
                    level.setBlock(pos, Blocks.CHIPPED_ANVIL.withPropertiesOf(state), Block.UPDATE_ALL_IMMEDIATE);
                    level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(null, Blocks.CHIPPED_ANVIL.withPropertiesOf(state)));
                    dispensed.shrink(1);
                }
            }
            if (dispensed.is(Items.NETHERITE_INGOT)) {
                if (state.is(BlockRegistry.CHIPPED_NETHERITE_ANVIL)) {
                    level.setBlock(pos, BlockRegistry.NETHERITE_ANVIL.withPropertiesOf(state), Block.UPDATE_ALL_IMMEDIATE);
                    level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(null, BlockRegistry.NETHERITE_ANVIL.withPropertiesOf(state)));
                    dispensed.shrink(1);
                }
                if (state.is(BlockRegistry.DAMAGED_NETHERITE_ANVIL)) {
                    level.setBlock(pos, BlockRegistry.CHIPPED_NETHERITE_ANVIL.withPropertiesOf(state), Block.UPDATE_ALL_IMMEDIATE);
                    level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(null, BlockRegistry.CHIPPED_NETHERITE_ANVIL.withPropertiesOf(state)));
                    dispensed.shrink(1);
                }
            }
        }
        return dispensed;
    }

    @Override
    protected void playSound(final BlockSource source) {
        source.level().levelEvent(1000, source.pos(), 0);
    }
}
