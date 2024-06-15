package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.TranslucentBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World

class BlueIceBlock (settings: Settings) : TranslucentBlock(settings) {
    public override fun getCodec() = CODEC

    companion object {
        val CODEC: MapCodec<BlueIceBlock> = createCodec { settings: Settings -> BlueIceBlock(settings) }
    }

	private fun getMeltedState():BlockState {
		return Blocks.PACKED_ICE.getDefaultState();
	}

    override fun afterBreak(world:World, player: PlayerEntity, pos:BlockPos, state:BlockState, blockEntity: BlockEntity?, tool: ItemStack) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
		if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) == 0) {
			if (world.getDimension().ultrawarm()) {
				world.removeBlock(pos, false);
				return;
			}
		}
	}

	override fun randomTick(state:BlockState, world:ServerWorld, pos:BlockPos, random:Random) {
		if (world.getDimension().ultrawarm()) {
			this.melt(state, world, pos);
		}
	}

	private fun melt(state:BlockState, world:World, pos:BlockPos) {
        world.setBlockState(pos, getMeltedState());
        world.updateNeighbor(pos, getMeltedState().getBlock(), pos);
	}
}
