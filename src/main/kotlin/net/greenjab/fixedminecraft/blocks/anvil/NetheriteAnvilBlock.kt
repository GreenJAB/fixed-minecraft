package net.greenjab.fixedminecraft.blocks.anvil

import com.mojang.serialization.MapCodec
import net.greenjab.fixedminecraft.blocks.BlockRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FallingBlock
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.entity.Entity
import net.minecraft.entity.FallingBlockEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.screen.AnvilScreenHandler
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.stat.Stats
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldEvents

class NetheriteAnvilBlock(settings: Settings) : FallingBlock(settings) {
    public override fun getCodec() = CODEC

    init {
        defaultState = stateManager.defaultState.with(FACING, Direction.NORTH)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(FACING, ctx.horizontalPlayerFacing.rotateYClockwise())
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult,
    ): ActionResult {
        // hmmm
        if (world.isClient) {
            return ActionResult.SUCCESS
        }
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
        player.incrementStat(Stats.INTERACT_WITH_ANVIL)
        return ActionResult.CONSUME
    }

    override fun createScreenHandlerFactory(state: BlockState, world: World, pos: BlockPos): NamedScreenHandlerFactory {
        return SimpleNamedScreenHandlerFactory({ syncId, inventory, _ ->
            AnvilScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos))
        }, TITLE)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        val direction = state.get(FACING)
        return if (direction.axis === Direction.Axis.X)
            X_AXIS_SHAPE
        else
            Z_AXIS_SHAPE
    }

    override fun configureFallingBlockEntity(entity: FallingBlockEntity) {
        entity.setHurtEntities(FALLING_BLOCK_ENTITY_DAMAGE_MULTIPLIER, FALLING_BLOCK_ENTITY_MAX_DAMAGE)
    }

    override fun onLanding(
        world: World,
        pos: BlockPos,
        fallingBlockState: BlockState,
        currentStateInPos: BlockState,
        fallingBlockEntity: FallingBlockEntity,
    ) {
        if (!fallingBlockEntity.isSilent)
            world.syncWorldEvent(WorldEvents.ANVIL_LANDS, pos, 0)
    }

    override fun onDestroyedOnLanding(world: World, pos: BlockPos, fallingBlockEntity: FallingBlockEntity) {
        if (!fallingBlockEntity.isSilent)
            world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0)
    }

    override fun getDamageSource(attacker: Entity): DamageSource = attacker.damageSources.fallingAnvil(attacker)

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState = state.with(FACING, rotation.rotate(state.get(FACING)))

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun canPathfindThrough(state: BlockState, world: BlockView, pos: BlockPos, type: NavigationType) = false

    override fun getColor(state: BlockState, world: BlockView, pos: BlockPos) = state.getMapColor(world, pos).color

    companion object {
        val CODEC: MapCodec<NetheriteAnvilBlock> = createCodec { settings: Settings -> NetheriteAnvilBlock(settings) }

        val FACING: DirectionProperty = HorizontalFacingBlock.FACING
        private val BASE_SHAPE = createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0)
        private val X_STEP_SHAPE = createCuboidShape(3.0, 4.0, 4.0, 13.0, 5.0, 12.0)
        private val X_STEM_SHAPE = createCuboidShape(4.0, 5.0, 6.0, 12.0, 10.0, 10.0)
        private val X_FACE_SHAPE = createCuboidShape(0.0, 10.0, 3.0, 16.0, 16.0, 13.0)
        private val Z_STEP_SHAPE = createCuboidShape(4.0, 4.0, 3.0, 12.0, 5.0, 13.0)
        private val Z_STEM_SHAPE = createCuboidShape(6.0, 5.0, 4.0, 10.0, 10.0, 12.0)
        private val Z_FACE_SHAPE = createCuboidShape(3.0, 10.0, 0.0, 13.0, 16.0, 16.0)
        private val X_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, X_STEP_SHAPE, X_STEM_SHAPE, X_FACE_SHAPE)
        private val Z_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, Z_STEP_SHAPE, Z_STEM_SHAPE, Z_FACE_SHAPE)
        private val TITLE = Text.translatable("container.repair")
        private const val FALLING_BLOCK_ENTITY_DAMAGE_MULTIPLIER = 2.0f
        private const val FALLING_BLOCK_ENTITY_MAX_DAMAGE = 40

        // TODO: handle damaging of anvil
        fun getLandingState(fallingState: BlockState): BlockState? {
            return when {
                fallingState.isOf(BlockRegistry.NETHERITE_ANVIL) -> {
                    BlockRegistry.CHIPPED_NETHERITE_ANVIL.defaultState.with(FACING, fallingState.get(FACING)) as BlockState
                }

                fallingState.isOf(BlockRegistry.CHIPPED_NETHERITE_ANVIL) -> {
                    BlockRegistry.DAMAGED_NETHERITE_ANVIL.defaultState.with(FACING, fallingState.get(FACING)) as BlockState
                }

                else -> null
            }
        }
    }
}

