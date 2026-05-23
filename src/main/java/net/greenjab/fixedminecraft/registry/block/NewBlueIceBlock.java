package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class NewBlueIceBlock extends HalfTransparentBlock {
    public static final MapCodec<NewBlueIceBlock> CODEC = simpleCodec(NewBlueIceBlock::new);

    @Override
    public @NonNull MapCodec<? extends NewBlueIceBlock> codec() {
        return CODEC;
    }

    public NewBlueIceBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    private BlockState getMeltedState() {
        return Blocks.PACKED_ICE.defaultBlockState();
    }

    @Override
    public void playerDestroy(@NonNull Level level, @NonNull Player player, @NonNull BlockPos pos, @NonNull BlockState state, @Nullable BlockEntity blockEntity, @NonNull ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (!EnchantmentHelper.hasTag(tool, EnchantmentTags.PREVENTS_ICE_MELTING)) {
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
