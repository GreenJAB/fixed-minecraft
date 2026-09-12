package net.greenjab.fixedminecraft.registry.block;

import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class NewBlueIceBlock extends HalfTransparentBlock {

    public NewBlueIceBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    private BlockState getMeltedState() {
        return Blocks.PACKED_ICE.defaultBlockState();
    }

    //TODO test break blue ice in nether
    @Override
    public void playerDestroy(final @NonNull ServerLevel level,final @NonNull ServerPlayer player, final @NonNull BlockPos pos, final @NonNull BlockState state, final @Nullable BlockEntity blockEntity, final @NonNull ItemStack destroyedWith) {
        super.playerDestroy(level, player, pos, state, blockEntity, destroyedWith);
        if (!EnchantmentHelper.hasTag(destroyedWith, EnchantmentTags.PREVENTS_ICE_MELTING)) {
            if (level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)) {
                level.removeBlock(pos, false);
            }
        }
    }

    @Override
    protected void randomTick(@NonNull BlockState state, ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (level.getGameRules().get(GameRuleRegistry.ICE_MELT_IN_NETHER)) {
            if (random.nextFloat() < 0.1f) {
                if (level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)) {
                    if (NewPackedIceBlock.notNextToCryingObsidian(level, pos)) this.melt(level, pos);
                }
            }
        }
    }

    protected void melt(Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, getMeltedState());
        level.neighborChanged(pos, getMeltedState().getBlock(), null);
    }
}
