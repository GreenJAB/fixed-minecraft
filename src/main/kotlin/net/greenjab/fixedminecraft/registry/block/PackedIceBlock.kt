package net.greenjab.fixedminecraft.registry.block

import com.mojang.serialization.MapCodec
import net.greenjab.fixedminecraft.registry.GameruleRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.TranslucentBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.EnchantmentTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World

class PackedIceBlock (settings: Settings) : TranslucentBlock(settings) {

    public override fun getCodec() = CODEC

    companion object {
        val CODEC: MapCodec<PackedIceBlock> = createCodec { settings: Settings -> PackedIceBlock(settings) }
    }

	private fun getMeltedState():BlockState {
		return Blocks.ICE.defaultState
	}

    override fun afterBreak(world:World, player: PlayerEntity, pos:BlockPos, state:BlockState, blockEntity: BlockEntity?, tool: ItemStack) {
        super.afterBreak(world, player, pos, state, blockEntity, tool)
        if (!EnchantmentHelper.hasAnyEnchantmentsIn(tool, EnchantmentTags.PREVENTS_ICE_MELTING)) {
			if (world.dimension.ultrawarm()) {
				world.removeBlock(pos, false)
				return
			}
		}
	}

	@Deprecated("Deprecated in Java")
    override fun randomTick(state:BlockState, world:ServerWorld, pos:BlockPos, random:Random) {
        if (world.gameRules.getBoolean(GameruleRegistry.Ice_Melt_In_Nether)) {
            if (random.nextFloat() < 0.33f) {
                if (world.dimension.ultrawarm()) {
                    this.melt(world, pos)
                }
            }
        }
	}

	private fun melt(world:World, pos:BlockPos) {
        world.setBlockState(pos, getMeltedState())
        world.updateNeighbor(pos, getMeltedState().block, null)
	}
}
