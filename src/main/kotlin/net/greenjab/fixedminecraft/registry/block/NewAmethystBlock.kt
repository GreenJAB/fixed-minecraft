package net.greenjab.fixedminecraft.registry.block

import com.mojang.serialization.MapCodec
import net.minecraft.block.AmethystBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.NoteBlock
import net.minecraft.block.RedstoneTorchBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.block.WireOrientation
import net.minecraft.world.event.GameEvent
import net.minecraft.world.event.Vibrations

class NewAmethystBlock(settings: Settings?) : AmethystBlock(settings) {
    override fun getCodec(): MapCodec<NewAmethystBlock> {
        return CODEC
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(LIT, ctx.world.isReceivingRedstonePower(ctx.blockPos)) as BlockState
    }

    override fun neighborUpdate(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        sourceBlock: Block?,
        wireOrientation: WireOrientation?,
        notify: Boolean
    ) {
        if (world != null) {
            if (!world.isClient) {
                val bl = state?.get(LIT) as Boolean
                if (bl != world.isReceivingRedstonePower(pos)) {
                    if (bl) {
                        world.scheduleBlockTick(pos, this, 4)
                    } else {
                        world.setBlockState(pos, state.cycle(LIT) as BlockState, NOTIFY_LISTENERS)
                        val frequency = world.getReceivedRedstonePower(pos)
                        world.emitGameEvent(Vibrations.getResonation(frequency), pos, GameEvent.Emitter.of(null, state))
                        val f = RESONATION_NOTE_PITCHES[frequency]
                        world.playSound(null as PlayerEntity?,pos,SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,SoundCategory.BLOCKS,1.0f,f)
                    }
                }
            }
        }
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (state.get(LIT) as Boolean && !world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, state.cycle(LIT) as BlockState, NOTIFY_LISTENERS)
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(LIT)
    }

    init {
        this.defaultState = defaultState.with(LIT, false) as BlockState
    }

    companion object {
        val CODEC: MapCodec<NewAmethystBlock> = createCodec { settings: Settings? ->
            NewAmethystBlock(
                settings
            )
        }
        val LIT: BooleanProperty = RedstoneTorchBlock.LIT

        val RESONATION_NOTE_PITCHES: FloatArray =
            net.minecraft.util.Util.make(FloatArray(16)) { frequency: FloatArray? ->
                val freq: IntArray = intArrayOf(0, 0, 2, 4, 6, 7, 9, 10, 12, 14, 15, 18, 19, 21, 22, 24)
                for (i in 0..15) {
                    frequency?.set(i, NoteBlock.getNotePitch(freq[i]))
                }
            }
    }

}
