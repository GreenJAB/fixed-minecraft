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
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.NonNull;

public class NewPitcherCropBlock extends PitcherCropBlock {

    public static final BooleanProperty FULL = BooleanProperty.create("full");

    public NewPitcherCropBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(FULL, false));
    }

    @Override
    public void randomTick(BlockState state, @NonNull ServerLevel world, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER && state.getValue(FULL) && random.nextInt(1) == 0) {
            BlockState blockState = state.setValue(FULL, false);
            world.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
            world.setBlock(pos.below(), blockState.setValue(HALF, DoubleBlockHalf.LOWER), Block.UPDATE_CLIENTS);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
        } else if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            float f = getAvailableMoisture(this, world, pos);
            boolean bl = random.nextInt((int)(25.0F / f) + 1) == 0;
            if (bl) {
                tryGrow(world, state, pos);
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return (state.getValue(HALF) == DoubleBlockHalf.LOWER && isMaxAge(state)) ||
               (state.getValue(HALF) == DoubleBlockHalf.UPPER && state.getValue(FULL));
    }


    @Override
    public void entityInside(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos, @NonNull Entity entity, @NonNull InsideBlockEffectApplier handler, boolean bl) {
        if (world instanceof ServerLevel serverWorld && entity instanceof Ravager && serverWorld.getGameRules().get(GameRules.MOB_GRIEFING)) {
            serverWorld.destroyBlock(pos, true, entity);
        }
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




    private boolean isMaxAge(BlockState state) {
        return state.getValue(AGE) < 4;
    }
    protected static float getAvailableMoisture(Block block, BlockGetter world, BlockPos pos) {
        float f = 1.0F;
        BlockPos blockPos = pos.below();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                float g = 0.0F;
                BlockState blockState = world.getBlockState(blockPos.offset(i, 0, j));
                if (blockState.is(Blocks.FARMLAND)) {
                    g = 1.0F;
                    if (blockState.getValue(FarmlandBlock.MOISTURE) > 0) {
                        g = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    g /= 4.0F;
                }

                f += g;
            }
        }

        BlockPos blockPos2 = pos.north();
        BlockPos blockPos3 = pos.south();
        BlockPos blockPos4 = pos.west();
        BlockPos blockPos5 = pos.east();
        boolean bl = world.getBlockState(blockPos4).is(block) || world.getBlockState(blockPos5).is(block);
        boolean bl2 = world.getBlockState(blockPos2).is(block) || world.getBlockState(blockPos3).is(block);
        if (bl && bl2) {
            f /= 2.0F;
        } else {
            boolean bl3 = world.getBlockState(blockPos4.north()).is(block)
                          || world.getBlockState(blockPos5.north()).is(block)
                          || world.getBlockState(blockPos5.south()).is(block)
                          || world.getBlockState(blockPos4.south()).is(block);
            if (bl3) {
                f /= 2.0F;
            }
        }

        return f;
    }
    private void tryGrow(ServerLevel world, BlockState state, BlockPos pos) {
        int i = Math.min(state.getValue(AGE) + 1, 4);
        if (this.canGrow(world, pos, state, i)) {
            BlockState blockState = state.setValue(AGE, i);
            world.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
            if (isDouble(i)) {
                world.setBlock(pos.above(), blockState.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
            }
        }
    }
    private boolean canGrow(LevelReader world, BlockPos pos, BlockState state, int age) {
        return !this.isMaxAge(state) && sufficientLight(world, pos) && (!isDouble(age) || canGrowInto(world, pos.above()));
    }
    private static boolean isDouble(int age) {
        return age >= 3;
    }
    private static boolean canGrowInto(LevelReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isAir() || blockState.is(Blocks.PITCHER_CROP);
    }

    private static boolean sufficientLight(LevelReader world, BlockPos pos) {
        return hasEnoughLightAt(world, pos);
    }
    protected static boolean hasEnoughLightAt(LevelReader world, BlockPos pos) {
        return world.getRawBrightness(pos, 0) >= 8;
    }

}
