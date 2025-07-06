package net.greenjab.fixedminecraft.registry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class NewPitcherPlantBlock extends TallPlantBlock {

    public static final BooleanProperty FULL = BooleanProperty.of("full");

    public NewPitcherPlantBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HALF, DoubleBlockHalf.LOWER).with(FULL, false));
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER && state.get(FULL) && random.nextInt(1) == 0) {
            BlockState blockState = state.with(FULL, false);
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
            world.setBlockState(pos.down(), blockState.with(HALF, DoubleBlockHalf.LOWER), Block.NOTIFY_LISTENERS);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
        }
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return state.get(HALF) == DoubleBlockHalf.UPPER && state.get(FULL);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
        if (entity instanceof LivingEntity livingEntity && state.get(HALF) == DoubleBlockHalf.UPPER && !state.get(FULL) && entity.getType() != EntityType.SNIFFER && livingEntity.hurtTime == 0 && !livingEntity.isInCreativeMode()) {
            //entity.slowMovement(state, new Vec3d(0.8F, 0.75, 0.8F));
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

}
