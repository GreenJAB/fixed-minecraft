package net.greenjab.fixedminecraft.registry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.NonNull;

public class NewPitcherPlantBlock extends DoublePlantBlock {

    public static final BooleanProperty FULL = BooleanProperty.create("full");

    public NewPitcherPlantBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(FULL, false));
    }

    @Override
    protected void randomTick(BlockState state, @NonNull ServerLevel world, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER && state.getValue(FULL) && random.nextInt(1) == 0) {
            BlockState blockState = state.setValue(FULL, false);
            world.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
            world.setBlock(pos.below(), blockState.setValue(HALF, DoubleBlockHalf.LOWER), Block.UPDATE_CLIENTS);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER && state.getValue(FULL);
    }

    @Override
    protected void entityInside(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos, @NonNull Entity entity, @NonNull InsideBlockEffectApplier handler, boolean bl) {
        if (entity instanceof LivingEntity livingEntity && state.getValue(HALF) == DoubleBlockHalf.UPPER && !state.getValue(FULL) && entity.getType() != EntityType.SNIFFER && livingEntity.hurtTime == 0 && !livingEntity.hasInfiniteMaterials()) {
            if (world instanceof ServerLevel serverWorld) {
                livingEntity.hurtServer(serverWorld, world.damageSources().sweetBerryBush(), 10.0F);
                BlockState blockState = state.setValue(FULL, true);
                world.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
                world.setBlock(pos.below(), blockState.setValue(HALF, DoubleBlockHalf.LOWER), Block.UPDATE_CLIENTS);
                world.playSound(null, pos, SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FULL);
        super.createBlockStateDefinition(builder);
    }

}
