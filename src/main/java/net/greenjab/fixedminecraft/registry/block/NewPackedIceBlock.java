package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class NewPackedIceBlock extends HalfTransparentBlock {
    public static final MapCodec<NewPackedIceBlock> CODEC = simpleCodec(NewPackedIceBlock::new);

    @Override
    public @NonNull MapCodec<? extends NewPackedIceBlock> codec() {
        return CODEC;
    }

    public NewPackedIceBlock(Properties settings) {
        super(settings);
    }

    private BlockState getMeltedState() {
        return Blocks.ICE.defaultBlockState();
    }

    @Override
    public void playerDestroy(@NonNull Level world, @NonNull Player player, @NonNull BlockPos pos, @NonNull BlockState state, @Nullable BlockEntity blockEntity, @NonNull ItemStack tool) {
        super.playerDestroy(world, player, pos, state, blockEntity, tool);
        if (!EnchantmentHelper.hasTag(tool, EnchantmentTags.PREVENTS_ICE_MELTING)) {
            if (world.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)) {
                world.removeBlock(pos, false);
            }
        }
    }

    @Override
    protected void randomTick(@NonNull BlockState state, ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (level.getGameRules().get(GameRuleRegistry.ICE_MELT_IN_NETHER)) {
            if (random.nextFloat() < 0.33f) {
                if (level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)) {
                    if (notNextToCryingObsidian(level, pos)) this.melt(level, pos);
                }
            }
        }
    }

    protected void melt( Level world, BlockPos pos) {
        world.setBlockAndUpdate(pos, getMeltedState());
        world.neighborChanged(pos, getMeltedState().getBlock(), null);
    }

    public static boolean notNextToCryingObsidian(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.offset(direction.getUnitVec3i())).is(Blocks.CRYING_OBSIDIAN)) return false;
        }
        return true;
    }
}
