package net.greenjab.fixedminecraft.registry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class NewPitcherCropBlock extends PitcherCropBlock {

    public static final BooleanProperty FULL = BooleanProperty.of("full");

    public NewPitcherCropBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HALF, DoubleBlockHalf.LOWER).with(FULL, false));
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER && state.get(FULL) && random.nextInt(1) == 0) {
            BlockState blockState = state.with(FULL, false);
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
            world.setBlockState(pos.down(), blockState.with(HALF, DoubleBlockHalf.LOWER), Block.NOTIFY_LISTENERS);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
        } else if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            float f = getAvailableMoisture(this, world, pos);
            boolean bl = random.nextInt((int)(25.0F / f) + 1) == 0;
            if (bl) {
                tryGrow(world, state, pos, 1);
            }
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return (state.get(HALF) == DoubleBlockHalf.LOWER && isFullyGrown(state)) ||
               (state.get(HALF) == DoubleBlockHalf.UPPER && state.get(FULL));
    }


    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (world instanceof ServerWorld serverWorld && entity instanceof RavagerEntity && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            serverWorld.breakBlock(pos, true, entity);
        }
        if (entity instanceof LivingEntity livingEntity && state.get(HALF) == DoubleBlockHalf.UPPER && !state.get(FULL) && entity.getType() != EntityType.SNIFFER && livingEntity.hurtTime == 0 && !livingEntity.isInCreativeMode()) {
            if (world instanceof ServerWorld serverWorld) {
                livingEntity.damage(serverWorld, world.getDamageSources().sweetBerryBush(), 10.0F);
                BlockState blockState = state.with(FULL, true);
                world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
                world.setBlockState(pos.down(), blockState.with(HALF, DoubleBlockHalf.LOWER), Block.NOTIFY_LISTENERS);
                world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FULL);
        super.appendProperties(builder);
    }




    private boolean isFullyGrown(BlockState state) {
        return state.get(AGE) < 4;
    }
    protected static float getAvailableMoisture(Block block, BlockView world, BlockPos pos) {
        float f = 1.0F;
        BlockPos blockPos = pos.down();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                float g = 0.0F;
                BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
                if (blockState.isOf(Blocks.FARMLAND)) {
                    g = 1.0F;
                    if ((Integer)blockState.get(FarmlandBlock.MOISTURE) > 0) {
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
        boolean bl = world.getBlockState(blockPos4).isOf(block) || world.getBlockState(blockPos5).isOf(block);
        boolean bl2 = world.getBlockState(blockPos2).isOf(block) || world.getBlockState(blockPos3).isOf(block);
        if (bl && bl2) {
            f /= 2.0F;
        } else {
            boolean bl3 = world.getBlockState(blockPos4.north()).isOf(block)
                          || world.getBlockState(blockPos5.north()).isOf(block)
                          || world.getBlockState(blockPos5.south()).isOf(block)
                          || world.getBlockState(blockPos4.south()).isOf(block);
            if (bl3) {
                f /= 2.0F;
            }
        }

        return f;
    }
    private void tryGrow(ServerWorld world, BlockState state, BlockPos pos, int amount) {
        int i = Math.min((Integer)state.get(AGE) + amount, 4);
        if (this.canGrow(world, pos, state, i)) {
            BlockState blockState = state.with(AGE, i);
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
            if (isDoubleTallAtAge(i)) {
                world.setBlockState(pos.up(), blockState.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
            }
        }
    }
    private boolean canGrow(WorldView world, BlockPos pos, BlockState state, int age) {
        return !this.isFullyGrown(state) && canPlaceAt(world, pos) && (!isDoubleTallAtAge(age) || canGrowAt(world, pos.up()));
    }
    private static boolean isDoubleTallAtAge(int age) {
        return age >= 3;
    }
    private static boolean canGrowAt(WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isAir() || blockState.isOf(Blocks.PITCHER_CROP);
    }

    private static boolean canPlaceAt(WorldView world, BlockPos pos) {
        return hasEnoughLightAt(world, pos);
    }
    protected static boolean hasEnoughLightAt(WorldView world, BlockPos pos) {
        return world.getBaseLightLevel(pos, 0) >= 8;
    }

}
