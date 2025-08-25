package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec;
import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlueIceBlock extends TranslucentBlock {
    public static final MapCodec<BlueIceBlock> CODEC = createCodec(BlueIceBlock::new);

    @Override
    public MapCodec<? extends BlueIceBlock> getCodec() {
        return CODEC;
    }

    public BlueIceBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    private BlockState getMeltedState() {
        return Blocks.PACKED_ICE.getDefaultState();
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        if (!EnchantmentHelper.hasAnyEnchantmentsIn(tool, EnchantmentTags.PREVENTS_ICE_MELTING)) {
            if (world.getDimension().ultrawarm()) {
                world.removeBlock(pos, false);
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getGameRules().getBoolean(GameruleRegistry.Ice_Melt_In_Nether)) {
            if (random.nextFloat() < 0.1f) {
                if (world.getDimension().ultrawarm()) {
                    if (!NewIceBlock.nextToCryingObsidian(world, pos)) this.melt(world, pos);
                }
            }
        }
    }

    protected void melt( World world, BlockPos pos) {
        world.setBlockState(pos, getMeltedState());
        world.updateNeighbor(pos, getMeltedState().getBlock(), null);
    }
}
