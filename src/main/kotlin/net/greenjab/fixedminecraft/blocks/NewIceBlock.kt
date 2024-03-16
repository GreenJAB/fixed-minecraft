package net.greenjab.fixedminecraft.blocks;

import com.mojang.serialization.MapCodec
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.IceBlock
import net.minecraft.block.TranslucentBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.LightType
import net.minecraft.world.World

class NewIceBlock (settings: Settings) : IceBlock(settings) {

	override fun randomTick(state:BlockState, world:ServerWorld, pos:BlockPos, random:Random) {
		if (world.getLightLevel(LightType.BLOCK, pos) > 11 - state.getOpacity(world, pos) || world.getDimension().ultrawarm()) {
			this.melt(state, world, pos);
		}
	}
}
