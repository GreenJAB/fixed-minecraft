package net.greenjab.fixedminecraft.registry.block

import com.mojang.serialization.MapCodec
import net.greenjab.fixedminecraft.FixedMinecraft
import net.greenjab.fixedminecraft.registry.BlockRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.FallingBlock
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.entity.Entity
import net.minecraft.entity.FallingBlockEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.AnvilScreenHandler
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
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
import net.minecraft.world.event.GameEvent

class NetheriteAnvilBlock(settings: Settings) : FallingBlock(settings) {
    public override fun getCodec() = CODEC

    init {
        this.defaultState =
            (stateManager.defaultState as BlockState).with(
                FACING,
                Direction.NORTH
            ) as BlockState
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(FACING, ctx.horizontalPlayerFacing.rotateYClockwise()) as BlockState
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos?, player: PlayerEntity, hit: BlockHitResult?): ActionResult {

        for (itemStack: ItemStack in player.getHandItems()) {
            if (itemStack.isOf(Items.NETHERITE_INGOT)) {
                if (state.isOf(BlockRegistry.CHIPPED_NETHERITE_ANVIL)) {
                    world.setBlockState(pos, BlockRegistry.NETHERITE_ANVIL.getStateWithProperties(state), Block.NOTIFY_ALL_AND_REDRAW);
                    world.playSound(player, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, BlockRegistry.NETHERITE_ANVIL.getStateWithProperties(state)));
                    itemStack.decrementUnlessCreative(1, player);
                    return ActionResult.SUCCESS
                }
                if (state.isOf(BlockRegistry.DAMAGED_NETHERITE_ANVIL)) {
                    world.setBlockState(pos, BlockRegistry.CHIPPED_NETHERITE_ANVIL.getStateWithProperties(state), Block.NOTIFY_ALL_AND_REDRAW);
                    world.playSound(player, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, BlockRegistry.CHIPPED_NETHERITE_ANVIL.getStateWithProperties(state)));
                    itemStack.decrementUnlessCreative(1, player);
                    return ActionResult.SUCCESS
                }
            }
        }

        if (!world.isClient) {
            player.addCommandTag("netherite_anvil")
            FixedMinecraft.netheriteAnvil = true
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
            player.incrementStat(Stats.INTERACT_WITH_ANVIL)
        }

        return ActionResult.SUCCESS
    }

    @Deprecated("Deprecated in Java")
    override fun createScreenHandlerFactory(state: BlockState, world: World, pos: BlockPos): NamedScreenHandlerFactory {
        return SimpleNamedScreenHandlerFactory({ syncId: Int, inventory: PlayerInventory?, player: PlayerEntity? ->
            AnvilScreenHandler(
                syncId,
                inventory,
                ScreenHandlerContext.create(world, pos)
            )
        }, TITLE)
    }

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        val direction = state.get(FACING)
        if (direction.axis === Direction.Axis.X) {
            return X_AXIS_SHAPE
        }
        return Z_AXIS_SHAPE
    }

    override fun configureFallingBlockEntity(entity: FallingBlockEntity) {
        entity.setHurtEntities(2.0f, 40)
    }

    override fun onLanding(
        world: World,
        pos: BlockPos,
        fallingBlockState: BlockState,
        currentStateInPos: BlockState,
        fallingBlockEntity: FallingBlockEntity
    ) {
        if (!fallingBlockEntity.isSilent) {
            world.syncWorldEvent(WorldEvents.ANVIL_LANDS, pos, 0)
        }
    }

    override fun onDestroyedOnLanding(world: World, pos: BlockPos, fallingBlockEntity: FallingBlockEntity) {
        if (!fallingBlockEntity.isSilent) {
            world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0)
        }
    }

    override fun getDamageSource(attacker: Entity): DamageSource {
        return attacker.damageSources.fallingAnvil(attacker)
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "state.with(FACING, rotation.rotate(state.get(FACING))) as BlockState",
        "net.greenjab.fixedminecraft.registry.block.NetheriteAnvilBlock.Companion.FACING",
        "net.greenjab.fixedminecraft.registry.block.NetheriteAnvilBlock.Companion.FACING",
        "net.minecraft.block.BlockState"
    )
    )
    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return state.with(FACING, rotation.rotate(state.get(FACING))) as BlockState
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun canPathfindThrough(state: BlockState?, type: NavigationType?): Boolean {
        return false
    }

    override fun getColor(state: BlockState, world: BlockView, pos: BlockPos): Int {
        return state.getMapColor(world, pos).color
    }

    companion object {
        val CODEC: MapCodec<NetheriteAnvilBlock> = createCodec { settings: Settings -> NetheriteAnvilBlock(settings) }

        val FACING: EnumProperty<Direction> = HorizontalFacingBlock.FACING
        private val BASE_SHAPE: VoxelShape = createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0)
        private val X_STEP_SHAPE: VoxelShape = createCuboidShape(3.0, 4.0, 4.0, 13.0, 5.0, 12.0)
        private val X_STEM_SHAPE: VoxelShape = createCuboidShape(4.0, 5.0, 6.0, 12.0, 10.0, 10.0)
        private val X_FACE_SHAPE: VoxelShape = createCuboidShape(0.0, 10.0, 3.0, 16.0, 16.0, 13.0)
        private val Z_STEP_SHAPE: VoxelShape = createCuboidShape(4.0, 4.0, 3.0, 12.0, 5.0, 13.0)
        private val Z_STEM_SHAPE: VoxelShape = createCuboidShape(6.0, 5.0, 4.0, 10.0, 10.0, 12.0)
        private val Z_FACE_SHAPE: VoxelShape = createCuboidShape(3.0, 10.0, 0.0, 13.0, 16.0, 16.0)
        private val X_AXIS_SHAPE: VoxelShape = VoxelShapes.union(BASE_SHAPE, X_STEP_SHAPE, X_STEM_SHAPE, X_FACE_SHAPE)
        private val Z_AXIS_SHAPE: VoxelShape = VoxelShapes.union(BASE_SHAPE, Z_STEP_SHAPE, Z_STEM_SHAPE, Z_FACE_SHAPE)
        private val TITLE: Text = Text.translatable("container.repair")
    }
}

